package com.half.wowsca.managers

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import androidx.collection.LongSparseArray
import com.half.wowsca.CAApp.Companion.getServerLanguage
import com.half.wowsca.CAApp.Companion.getServerType
import com.half.wowsca.R
import com.half.wowsca.backend.GetCaptainTask
import com.half.wowsca.backend.GetShipEncyclopediaInfo
import com.half.wowsca.model.Captain
import com.half.wowsca.model.enums.Server
import com.half.wowsca.model.queries.CaptainQuery
import com.half.wowsca.model.queries.ShipQuery
import com.utilities.logging.Dlog.d

/**
 * Created by slai4 on 10/12/2015.
 */
object CompareManager {
    private val captains: MutableList<Captain> = ArrayList()
    @JvmField
    var GRABBING_INFO: Boolean = false

    /**
     * SHIP COMPARE AREA
     */
    private var SHIPS: MutableList<Long>? = null
    private var SHIP_INFORMATION: LongSparseArray<String>? = null
    private var MODULE_LIST: LongSparseArray<Map<String, Long>>? = null
    private var asyncTasks: MutableList<GetShipEncyclopediaInfo>? = null

    @JvmStatic
    fun search(ctx: Context?) {
        if (size() > 1) {
            for (i in 0 until size()) {
                val c = captains[i]
                val query = CaptainQuery()
                query.id = c.id
                query.name = c.name
                query.server = c.server
                val task = GetCaptainTask()
                task.ctx = ctx
                task.execute(query)
            }
        } else {
            Toast.makeText(ctx, R.string.compare_needs_two, Toast.LENGTH_SHORT).show()
        }
    }

    @JvmStatic
    fun captainsHaveInfo(): Boolean {
        var allInfoThere = true
        for (i in captains.indices) {
            val c = captains[i]
            if (c.details == null || c.ships == null) {
                allInfoThere = false
            }
        }
        return allInfoThere
    }

    @JvmStatic
    fun addCaptain(cap: Captain, addToFirst: Boolean): Boolean {
        val c = cap.copy()
        var added = false
        if (size() < 3) {
            if (!isAlreadyThere(c.server, c.id)) {
                if (!addToFirst) captains.add(c)
                else captains.add(0, c)
                added = true
            }
        }
        return added
    }

    @JvmStatic
    fun overrideCaptain(cap: Captain) {
        for (i in captains.indices) {
            val c = captains[i]
            if (c.id == cap.id && c.server.ordinal == cap.server.ordinal) {
                captains[i] = cap
            }
        }
    }

    @JvmStatic
    fun removeCaptain(s: Server, id: Long) {
        if (size() > 0) {
            for (i in captains.indices) {
                val c = captains[i]
                if (c.id == id && c.server.ordinal == s.ordinal) {
                    captains.removeAt(i)
                    break
                }
            }
        }
    }

    @JvmStatic
    fun clear() {
        captains.clear()
    }

    @JvmStatic
    fun isAlreadyThere(s: Server, id: Long): Boolean {
        var there = false
        if (size() > 0) {
            for (i in captains.indices) {
                val c = captains[i]
                if (c.id == id && c.server.ordinal == s.ordinal) {
                    there = true
                    break
                }
            }
        }
        return there
    }

    @JvmStatic
    fun size(): Int {
        return captains.size
    }

    @JvmStatic
    fun getCaptains(): List<Captain> {
        return captains
    }

    @JvmStatic
    fun searchShips(ctx: Context?) {
        GRABBING_INFO = true
        asyncTasks = mutableListOf()

        for (i in SHIPS!!) {
            val info = GetShipEncyclopediaInfo()
            asyncTasks?.add(info)
            val query = ShipQuery()
            query.shipId = i
            query.server = getServerType(ctx)
            query.language = getServerLanguage(ctx!!)
            query.modules = moduleList!![i]
            info.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query)
        }
    }

    @JvmStatic
    fun searchShip(ctx: Context?, shipId: Long) {
        val info = GetShipEncyclopediaInfo()
        val query = ShipQuery()
        query.shipId = shipId
        query.server = getServerType(ctx)
        query.language = getServerLanguage(ctx!!)
        query.modules = moduleList!![shipId]
        info.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query)
    }

    @JvmStatic
    fun getSHIPS(): List<Long>? {
        if (SHIPS == null) SHIPS = ArrayList()
        return SHIPS
    }

    @JvmStatic
    val shipInformation: LongSparseArray<String>?
        get() {
            if (SHIP_INFORMATION == null) SHIP_INFORMATION = LongSparseArray()
            return SHIP_INFORMATION
        }

    @JvmStatic
    val moduleList: LongSparseArray<Map<String, Long>>?
        get() {
            if (MODULE_LIST == null) MODULE_LIST = LongSparseArray()
            return MODULE_LIST
        }

    @JvmStatic
    fun addShipInfo(id: Long?, shipInfo: String) {
        if (SHIP_INFORMATION == null) {
            SHIP_INFORMATION = LongSparseArray()
        }
        SHIP_INFORMATION!!.put(id!!, shipInfo)
    }

    @JvmStatic
    fun addShipID(shipID: Long) {
        if (SHIPS == null) {
            SHIPS = ArrayList()
            SHIP_INFORMATION = LongSparseArray()
            MODULE_LIST = LongSparseArray()
        }
        SHIPS!!.add(shipID)
    }

    @JvmStatic
    fun removeShipID(shipID: Long) {
        SHIPS!!.remove(shipID)
    }

    @JvmStatic
    fun clearShips(clearShips: Boolean) {
        if (clearShips) SHIPS = null
        SHIP_INFORMATION = null
        MODULE_LIST = null
    }

    @JvmStatic
    fun checkForDone() {
        if (asyncTasks != null) {
            GRABBING_INFO = shipInformation!!.size() != asyncTasks!!.size
            d("Checkfordone", "grabbing = " + GRABBING_INFO)
            if (GRABBING_INFO) {
                asyncTasks = null
            }
        }
    }
}