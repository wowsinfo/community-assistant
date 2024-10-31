package com.half.wowsca.ui.views

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.gridlayout.widget.GridLayout
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.R
import com.half.wowsca.backend.GetShipEncyclopediaInfo
import com.half.wowsca.managers.CompareManager
import com.half.wowsca.managers.CompareManager.moduleList
import com.half.wowsca.managers.CompareManager.searchShip
import com.half.wowsca.managers.InfoManager
import com.half.wowsca.model.ProgressEvent
import com.half.wowsca.model.encyclopedia.items.ShipInfo
import com.half.wowsca.model.encyclopedia.items.ShipModuleItem
import com.utilities.logging.Dlog.d
import java.util.Collections

/**
 * Created by slai47 on 5/21/2017.
 */
class ShipModuleView(context: Context) : LinearLayout(context) {
    @JvmField
    var shipID: Long = 0

    var gridView: GridLayout? = null

    var tvTitle: TextView? = null

    var tvState: TextView? = null

    init {
        addBaseViews(context)
        initView()
    }

    private fun addBaseViews(context: Context) {
        setPadding(20, 20, 20, 20)
        orientation = VERTICAL
        val params = AbsListView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        tvTitle = TextView(context)
        tvTitle!!.layoutParams = params
        tvTitle!!.setPadding(10, 10, 10, 20)
        addView(tvTitle)
        gridView = GridLayout(context)
        gridView!!.layoutParams = params
        gridView!!.columnCount = 2
        addView(gridView)
        tvState = TextView(context)
        tvState!!.layoutParams = params
        tvState!!.visibility = GONE
        addView(tvState)
    }

    fun initView() {
        // grab module list for the ship ID;
        val info = InfoManager().getShipInfo(context)[shipID]
        if (info != null) {
            tvTitle!!.text = info.name
            // build out grid view
            createGrid()
        }
    }

    private fun createGrid() {
        gridView!!.removeAllViews()
        d("ShipModuleView", "createGrid")
        gridView!!.post(object : Runnable {
            override fun run() {
                val shipInfo = infoManager!!.getShipInfo(context)[shipID]
                d("ShipModuleView", "items = " + shipInfo!!.items)

                var moduleList: MutableMap<String, Long>? = moduleList!![shipID]
                if (moduleList == null) moduleList = HashMap()
                buildDefaultModuleList(shipInfo, moduleList)

                d("ShipModuleView", "moduleList = $moduleList")
                CompareManager.moduleList!!.put(shipID, moduleList)

                val artillery = shipInfo.items[moduleList[GetShipEncyclopediaInfo.ARTILLERY]]
                val torps = shipInfo.items[moduleList[GetShipEncyclopediaInfo.TORPEDOES]]
                val fireControl = shipInfo.items[moduleList[GetShipEncyclopediaInfo.FIRE_CONTROL]]
                val flightControl =
                    shipInfo.items[moduleList[GetShipEncyclopediaInfo.FLIGHT_CONTROL]]
                val hull = shipInfo.items[moduleList[GetShipEncyclopediaInfo.HULL]]
                val engine = shipInfo.items[moduleList[GetShipEncyclopediaInfo.ENGINE]]
                val fighter = shipInfo.items[moduleList[GetShipEncyclopediaInfo.FIGHTER]]
                val diveBomber = shipInfo.items[moduleList[GetShipEncyclopediaInfo.DIVE_BOMBER]]
                val torpBomber = shipInfo.items[moduleList[GetShipEncyclopediaInfo.TORPEDO_BOMBER]]

                // Add needed items to list
                val hasOptions: MutableList<Boolean> = ArrayList()
                val items: MutableList<ShipModuleItem> = ArrayList()

                addNecessaryModules(
                    shipInfo,
                    artillery,
                    torps,
                    fireControl,
                    flightControl,
                    hull,
                    engine,
                    fighter,
                    diveBomber,
                    torpBomber,
                    hasOptions,
                    items
                )

                buildModuleLists(hasOptions, items)
            }

            private fun addNecessaryModules(
                shipInfo: ShipInfo?,
                artillery: ShipModuleItem?,
                torps: ShipModuleItem?,
                fireControl: ShipModuleItem?,
                flightControl: ShipModuleItem?,
                hull: ShipModuleItem?,
                engine: ShipModuleItem?,
                fighter: ShipModuleItem?,
                diveBomber: ShipModuleItem?,
                torpBomber: ShipModuleItem?,
                hasOptions: MutableList<Boolean>,
                items: MutableList<ShipModuleItem>
            ) {
                if (artillery != null) {
                    items.add(artillery)
                    hasOptions.add(shipInfo!!.artillery.size > 1)
                }
                if (torps != null) {
                    items.add(torps)
                    hasOptions.add(shipInfo!!.torps.size > 1)
                }
                if (fireControl != null) {
                    items.add(fireControl)
                    hasOptions.add(shipInfo!!.fireControl.size > 1)
                }
                if (flightControl != null) {
                    items.add(flightControl)
                    hasOptions.add(shipInfo!!.flightControl.size > 1)
                }
                if (hull != null) {
                    items.add(hull)
                    hasOptions.add(shipInfo!!.hull.size > 1)
                }
                if (engine != null) {
                    items.add(engine)
                    hasOptions.add(shipInfo!!.engine.size > 1)
                }
                if (fighter != null) {
                    items.add(fighter)
                    hasOptions.add(shipInfo!!.fighter.size > 1)
                }
                if (diveBomber != null) {
                    items.add(diveBomber)
                    hasOptions.add(shipInfo!!.diveBomber.size > 1)
                }
                if (torpBomber != null) {
                    items.add(torpBomber)
                    hasOptions.add(shipInfo!!.torpBomb.size > 1)
                }
            }

            private fun buildDefaultModuleList(
                shipInfo: ShipInfo?,
                moduleList: MutableMap<String, Long>
            ) {
                if (shipInfo != null && moduleList.isEmpty()) {
                    moduleList[GetShipEncyclopediaInfo.ARTILLERY] =
                        getBaseModuleToList(shipInfo, shipInfo.artillery)
                    moduleList[GetShipEncyclopediaInfo.TORPEDOES] =
                        getBaseModuleToList(shipInfo, shipInfo.torps)
                    moduleList[GetShipEncyclopediaInfo.FIRE_CONTROL] =
                        getBaseModuleToList(shipInfo, shipInfo.fireControl)
                    moduleList[GetShipEncyclopediaInfo.FLIGHT_CONTROL] =
                        getBaseModuleToList(shipInfo, shipInfo.flightControl)
                    moduleList[GetShipEncyclopediaInfo.HULL] =
                        getBaseModuleToList(shipInfo, shipInfo.hull)
                    moduleList[GetShipEncyclopediaInfo.ENGINE] =
                        getBaseModuleToList(shipInfo, shipInfo.engine)
                    moduleList[GetShipEncyclopediaInfo.FIGHTER] =
                        getBaseModuleToList(shipInfo, shipInfo.fighter)
                    moduleList[GetShipEncyclopediaInfo.DIVE_BOMBER] =
                        getBaseModuleToList(shipInfo, shipInfo.diveBomber)
                    moduleList[GetShipEncyclopediaInfo.TORPEDO_BOMBER] =
                        getBaseModuleToList(shipInfo, shipInfo.torpBomb)
                }
            }

            private fun getBaseModuleToList(info: ShipInfo, modules: List<Long>?): Long {
                if (modules != null && modules.size > 0) {
                    for (i in modules.indices) {
                        val item = info.items[modules[i]]
                        if (item!!.isDefault) {
                            return item.id
                        }
                    }
                    return modules[0]
                } else return 0
            }

            private fun cleanModuleTitle(tv: TextView, title: String) {
                var title: String? = title
                when (title) {
                    "Suo" -> title = context.getString(R.string.fire_control)
                    "FlightControl" -> title = context.getString(R.string.flight_control)
                    "TorpedoBomber" -> title = context.getString(R.string.torpedo_bomber)
                    "DiveBomber" -> title = context.getString(R.string.dive_bomber)
                }
                tv.text = title
            }

            private fun buildModuleLists(
                hasOptions: List<Boolean>,
                items: List<ShipModuleItem>
            ): Boolean {
                var hasAnOption = false
                for (i in items.indices) {
                    val convertView = LayoutInflater.from(gridView!!.context)
                        .inflate(R.layout.list_ship_module, gridView, false)
                    val params = GridLayout.LayoutParams()
                    params.columnSpec = GridLayout.spec(i % 2, 1, 1f)
                    params.setMargins(5, 5, 5, 5)
                    convertView.layoutParams = params

                    val tv = convertView.findViewById<TextView>(R.id.list_module_top)
                    val tvText = convertView.findViewById<TextView>(R.id.list_module_text)

                    val item = items[i]

                    val hasOpt = hasOptions[i]

                    if (hasOpt) {
                        hasAnOption = true
                        convertView.setBackgroundResource(R.drawable.encyclopedia_module_white)
                    } else {
                        convertView.setBackgroundResource(R.drawable.compare_normal_grid)
                    }

                    tvText.text = item.name

                    cleanModuleTitle(tv, item.type)

                    convertView.tag = item.id

                    convertView.setOnClickListener { v ->
                        val id = v.tag as Long
                        if (id != null) {
                            val shipInfo = infoManager!!.getShipInfo(gridView!!.context)[shipID]
                            val item = shipInfo!!.items[id]
                            var typeIds: List<Long?> = ArrayList()
                            when (item!!.type) {
                                "Suo" -> typeIds = shipInfo.fireControl
                                "FlightControl" -> typeIds = shipInfo.flightControl
                                "DiveBomber" -> typeIds = shipInfo.diveBomber
                                "Fighter" -> typeIds = shipInfo.fighter
                                "Artillery" -> typeIds = shipInfo.artillery
                                "Hull" -> typeIds = shipInfo.hull
                                "TorpedoBomber" -> typeIds = shipInfo.torpBomb
                                "Torpedoes" -> typeIds = shipInfo.torps
                                "Engine" -> typeIds = shipInfo.engine
                            }
                            Collections.sort(typeIds) { lhs, rhs ->
                                val lhsItem = shipInfo.items[lhs]
                                val rhsItem = shipInfo.items[rhs]
                                lhsItem!!.name.compareTo(rhsItem!!.name, ignoreCase = true)
                            }
                            if (typeIds.size > 1) {
                                val menu = PopupMenu(
                                    gridView!!.context, v
                                )
                                menu.gravity = Gravity.CENTER
                                val mapOfItems: MutableMap<Int, ShipModuleItem> = HashMap()
                                val m = menu.menu

                                val current = v.tag as Long
                                for (i in typeIds.indices) {
                                    val it = shipInfo.items[typeIds[i]]
                                    if (it != null && it.id != current) {
                                        m.add(0, i, 0, it.name)
                                        mapOfItems[i] = it
                                    }
                                }
                                menu.setOnMenuItemClickListener { item ->
                                    val i = mapOfItems[item.itemId]
                                    var MODULE_LIST: MutableMap<String, Long>? =
                                        moduleList!![shipID]
                                    if (MODULE_LIST == null) MODULE_LIST = HashMap()
                                    d("ShipModuleView", "id = " + i!!.id)
                                    d("ShipModuleView", "moduleListB = $MODULE_LIST")
                                    when (i.type) {
                                        "Suo" -> MODULE_LIST[GetShipEncyclopediaInfo.FIRE_CONTROL] =
                                            i.id

                                        "FlightControl" -> MODULE_LIST[GetShipEncyclopediaInfo.FLIGHT_CONTROL] =
                                            i.id

                                        "DiveBomber" -> MODULE_LIST[GetShipEncyclopediaInfo.DIVE_BOMBER] =
                                            i.id

                                        "Fighter" -> MODULE_LIST[GetShipEncyclopediaInfo.FIGHTER] =
                                            i.id

                                        "Artillery" -> MODULE_LIST[GetShipEncyclopediaInfo.ARTILLERY] =
                                            i.id

                                        "Hull" -> MODULE_LIST[GetShipEncyclopediaInfo.HULL] = i.id
                                        "TorpedoBomber" -> MODULE_LIST[GetShipEncyclopediaInfo.TORPEDO_BOMBER] =
                                            i.id

                                        "Torpedoes" -> MODULE_LIST[GetShipEncyclopediaInfo.TORPEDOES] =
                                            i.id

                                        "Engine" -> MODULE_LIST[GetShipEncyclopediaInfo.ENGINE] =
                                            i.id
                                    }
                                    d("ShipModuleView", "moduleListA = $MODULE_LIST")
                                    moduleList!!.put(shipID, MODULE_LIST)
                                    d(
                                        "ShipModuleView",
                                        "moduleListS = " + moduleList!![shipID].toString()
                                    )
                                    //Update the screen
                                    CompareManager.GRABBING_INFO = true
                                    searchShip(context, shipID)
                                    eventBus.post(ProgressEvent(true))
                                    false
                                }
                                menu.show()
                            }
                        }
                    }

                    gridView!!.addView(convertView)
                }
                return hasAnOption
            }
        })
    }

    companion object {
        const val PATTERN: String = "###,###,###"
    }
}
