package com.half.wowsca.managers

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.half.wowsca.model.Captain
import com.half.wowsca.model.enums.Server
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.io.PrintWriter

/**
 * Created by slai4 on 9/15/2015.
 */
object CaptainManager {
    const val DIRECTORY_NAME: String = "wowsassist"
    private const val PLAYER_SAVED_FILE_NAME = "list_of_captains"
    private const val TEMP_STORAGE_FILE = "tempstored"

    private var CAPTAINS: MutableMap<String?, Captain?>? = null

    private var TEMP: Captain? = null

    @JvmStatic
    fun getCaptains(ctx: Context): MutableMap<String?, Captain?>? {
        var caps: MutableMap<String?, Captain?>? = HashMap()
        if (CAPTAINS == null) {
            try {
                val typeOfHashMap = object : TypeToken<Map<String?, Captain?>?>() {
                }.type
                val dir = ctx.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE)
                val clansFile = File(dir, PLAYER_SAVED_FILE_NAME)
                val fr = FileReader(clansFile)
                val builder = GsonBuilder()
                builder.serializeSpecialFloatingPointValues()
                val gson = builder.create()
                caps = gson.fromJson(fr, typeOfHashMap)
                fr.close()
            } catch (e: Exception) {
            }
            CAPTAINS = caps
        } else {
            caps = CAPTAINS
        }
        return caps
    }

    @JvmStatic
    fun getCapIdStr(c: Captain?): String {
        return c!!.server.toString() + c.id
    }

    @JvmStatic
    fun createCapIdStr(s: Server?, id: Long?): String {
        return s.toString() + id
    }

    @JvmStatic
    fun saveCaptain(ctx: Context, c: Captain?) {
        if (c != null) { // put in to prevent crash on line 59. If not fixed might be c.getServer is null
            if (CAPTAINS != null) {
                CAPTAINS!![getCapIdStr(c)] = c
            } else {
                val captains = getCaptains(ctx)
                CAPTAINS = captains
                CAPTAINS!![getCapIdStr(c)] = c
            }
            saveCaptains(ctx, CAPTAINS)
        }
    }

    private fun saveCaptains(ctx: Context, clans: Map<String?, Captain?>?) {
        // Clear out members? maybe turn all clans into copies here instead of in the memory one.
        val saveCaptains: MutableMap<String, Captain> = HashMap()
        for (c in clans!!.values) {
            val copy = c!!.copy()
            saveCaptains[getCapIdStr(c)] = copy
        }
        val dir = ctx.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE)
        val captainFiles = File(dir, PLAYER_SAVED_FILE_NAME)
        if (!captainFiles.exists()) try {
            captainFiles.createNewFile()
        } catch (e: IOException) {
        }
        try {
            val fos = FileOutputStream(captainFiles)
            val pw = PrintWriter(fos)
            val builder = GsonBuilder()
            builder.serializeSpecialFloatingPointValues()
            val gson = builder.create()
            pw.print(gson.toJson(saveCaptains))
            pw.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun removeCaptain(ctx: Context, id: String?) {
        val captains = getCaptains(ctx)
        captains!!.remove(id)
        if (CAPTAINS != null) {
            CAPTAINS!!.remove(id)
        }
        saveCaptains(ctx, captains)
    }


    @JvmStatic
    fun fromSearch(ctx: Context, s: Server, id: Long): Boolean {
        return getCaptains(ctx)!![createCapIdStr(s, id)] == null
    }

    @JvmStatic
    fun getTEMP(ctx: Context): Captain? {
        if (TEMP == null) TEMP = getTempStoredPlayer(ctx)
        return TEMP
    }

    @JvmStatic
    fun saveTempStoredCaptain(ctx: Context, captain: Captain?) {
        val dir = ctx.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE)
        val tempCaptainFile = File(dir, TEMP_STORAGE_FILE)
        if (!tempCaptainFile.exists()) try {
            tempCaptainFile.createNewFile()
        } catch (e: IOException) {
        }
        try {
            val fos = FileOutputStream(tempCaptainFile)
            val pw = PrintWriter(fos)
            val builder = GsonBuilder()
            builder.serializeSpecialFloatingPointValues()
            val gson = builder.create()
            pw.print(gson.toJson(captain))
            pw.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getTempStoredPlayer(ctx: Context): Captain? {
        var c: Captain? = null
        try {
            val dir = ctx.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE)
            val tempClan = File(dir, TEMP_STORAGE_FILE)
            val fr = FileReader(tempClan)
            val builder = GsonBuilder()
            builder.serializeSpecialFloatingPointValues()
            val gson = builder.create()
            c = gson.fromJson(fr, Captain::class.java)
            fr.close()
        } catch (e: FileNotFoundException) {
        } catch (e: IOException) {
        }
        return c
    }

    @JvmStatic
    fun deleteTemp(ctx: Context) {
        try {
            TEMP = null
            val dir = ctx.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE)
            val temp = File(dir, TEMP_STORAGE_FILE)
            if (temp.exists()) temp.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
