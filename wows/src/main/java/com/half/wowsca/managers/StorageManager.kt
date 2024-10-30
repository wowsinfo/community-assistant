package com.half.wowsca.managers

import android.content.Context
import com.google.gson.GsonBuilder
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.managers.CaptainManager.getCapIdStr
import com.half.wowsca.model.Achievement
import com.half.wowsca.model.Captain
import com.half.wowsca.model.CaptainDetails
import com.half.wowsca.model.Ship
import com.half.wowsca.model.events.CaptainSavedEvent
import com.half.wowsca.model.saveobjects.SavedAchievements
import com.half.wowsca.model.saveobjects.SavedDetails
import com.half.wowsca.model.saveobjects.SavedShips
import com.utilities.logging.Dlog.d
import com.utilities.preferences.Prefs
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.io.PrintWriter

/**
 * Created by slai4 on 9/15/2015.
 */
object StorageManager {
    private const val STATS_FOLDER = "wowscacaptains"

    @JvmStatic
    fun getStatsMax(ctx: Context?): Int {
        val prefs = Prefs(ctx)
        return prefs.getInt("stats_max", 10)
    }

    @JvmStatic
    fun setStatsMax(ctx: Context?, num: Int) {
        val prefs = Prefs(ctx)
        prefs.setInt("stats_max", num)
    }

    @JvmStatic
    fun getShipsStatsMax(ctx: Context?): Int {
        val prefs = Prefs(ctx)
        return prefs.getInt("ships_stats_max", 5)
    }

    @JvmStatic
    fun setShipsStatsMax(ctx: Context?, num: Int) {
        val prefs = Prefs(ctx)
        prefs.setInt("ships_stats_max", num)
    }

    @JvmStatic
    fun savePlayerStats(ctx: Context, p: Captain) {
        val runnable = Runnable {
            val statsList = getPlayerStats(ctx, getCapIdStr(p))
            var save = true
            try {
                if (!statsList.details.isEmpty()) {
                    val stat1 = statsList.details[0]
                    if (stat1 != null) {
                        d("StorageManager", "stat= " + stat1.battles + " p" + p.details.battles)
                        if (stat1.battles == p.details.battles) save = false
                    }
                }
            } catch (e: Exception) {
            }
            //                Dlog.wtf("Storage amange", "save = " + save + " list = " + statsList.getDetails().size());
            if (save) {
                val stats_max = getStatsMax(ctx)
                val ship_stat_max = getShipsStatsMax(ctx)

                addStat(statsList, p.details, stats_max)

                val achievements = getPlayerAchievements(ctx, getCapIdStr(p))
                addAchievements(achievements, p.achievements, stats_max)

                val ships = getPlayerShips(ctx, getCapIdStr(p))
                if (p.ships != null && ships != null) {
                    for (s in p.ships) {
                        if (ships != null && ships.savedShips[s.shipId] != null) {
                            val last = ships.savedShips[s.shipId]!![0]
                            if (last.battles != s.battles) addShipStat(ships, s, ship_stat_max)
                        } else {
                            val ss: MutableList<Ship> = ArrayList()
                            ss.add(s)
                            ships.savedShips[s.shipId] = ss
                        }
                    }
                }

                val builder = GsonBuilder()
                builder.serializeSpecialFloatingPointValues()
                val gson = builder.create()

                val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
                val statsDir = File(dir, STATS_FOLDER)
                val statsFile = File(statsDir, getCapIdStr(p))
                if (!statsFile.exists()) try {
                    statsFile.createNewFile()
                } catch (e: IOException) {
                }
                try {
                    val fos = FileOutputStream(statsFile)
                    val pw = PrintWriter(fos)
                    pw.print(gson.toJson(statsList))
                    pw.flush()
                    fos.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val achievementsFile = File(statsDir, "a" + getCapIdStr(p))
                try {
                    val fos = FileOutputStream(achievementsFile)
                    val pw = PrintWriter(fos)
                    pw.print(gson.toJson(achievements))
                    pw.flush()
                    fos.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (p.ships != null) {
                    val shipsFile = File(statsDir, "s" + getCapIdStr(p))
                    try {
                        val fos = FileOutputStream(shipsFile)
                        val pw = PrintWriter(fos)
                        pw.print(gson.toJson(ships))
                        pw.flush()
                        fos.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            eventBus.post(CaptainSavedEvent())
        }
        val t = Thread(runnable)
        t.start()
    }

    /**
     * DO this in a thread. Might take a long time
     *
     * @param ctx
     * @param accountId
     * @return
     */
    @JvmStatic
    fun getPlayerStats(ctx: Context, accountId: String?): SavedDetails {
        var savedDetails = SavedDetails()
        val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
        val statsDir = File(dir, STATS_FOLDER)
        if (!statsDir.exists()) {
            statsDir.mkdir()
        }
        try {
            val tempStats = File(statsDir, accountId)
            val fr = FileReader(tempStats)

            val builder = GsonBuilder()
            builder.serializeSpecialFloatingPointValues()
            val gson = builder.create()
            savedDetails = gson.fromJson(fr, SavedDetails::class.java)
            fr.close()
        } catch (e: FileNotFoundException) {
        } catch (e: IOException) {
        }
        return savedDetails
    }

    private fun addStat(stats: SavedDetails?, stat: CaptainDetails, stats_max: Int) {
        stats!!.details.add(0, stat)
        if (stats != null) if (stats.details != null) if (stats.details.size > stats_max) stats.details.removeAt(
            stats.details.size - 1
        )
    }

    /**
     * DO this in a thread. Might take a long time
     *
     * @param ctx
     * @param accountId
     * @return
     */
    @JvmStatic
    fun getPlayerAchievements(ctx: Context, accountId: String): SavedAchievements {
        var savedAchievements = SavedAchievements()
        val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
        val statsDir = File(dir, STATS_FOLDER)
        if (!statsDir.exists()) {
            statsDir.mkdir()
        }
        try {
            val tempStats = File(statsDir, "a$accountId")
            val fr = FileReader(tempStats)

            val builder = GsonBuilder()
            builder.serializeSpecialFloatingPointValues()
            val gson = builder.create()
            savedAchievements = gson.fromJson(fr, SavedAchievements::class.java)
            fr.close()
        } catch (e: FileNotFoundException) {
        } catch (e: IOException) {
        }
        return savedAchievements
    }

    private fun addAchievements(stats: SavedAchievements, stat: List<Achievement>, stats_max: Int) {
        var stats: SavedAchievements? = stats
        if (stats!!.savedAchievements == null) stats = SavedAchievements()
        stats.savedAchievements.add(0, stat)
        if (stats != null) if (stats.savedAchievements != null) if (stats.savedAchievements.size > stats_max) stats.savedAchievements.removeAt(
            stats.savedAchievements.size - 1
        )
    }

    @JvmStatic
    fun getPlayerShips(ctx: Context, accountId: String): SavedShips {
        var savedDetails = SavedShips()
        val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
        val statsDir = File(dir, STATS_FOLDER)
        if (!statsDir.exists()) {
            statsDir.mkdir()
        }
        try {
            val tempStats = File(statsDir, "s$accountId")
            val fr = FileReader(tempStats)

            val builder = GsonBuilder()
            builder.serializeSpecialFloatingPointValues()
            val gson = builder.create()
            savedDetails = gson.fromJson(fr, SavedShips::class.java)
            fr.close()
        } catch (e: FileNotFoundException) {
        } catch (e: IOException) {
        }
        return savedDetails
    }

    private fun addShipStat(stats: SavedShips, ship: Ship, ship_stat_max: Int) {
        stats.savedShips[ship.shipId]!!.add(0, ship)
        if (stats.savedShips[ship.shipId]!!.size > ship_stat_max) stats.savedShips[ship.shipId]!!
            .removeAt(stats.savedShips[ship.shipId]!!.size - 1)
    }


    @JvmStatic
    fun clearDownloadedPlayers(ctx: Context): Boolean {
        val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
        val statsDir = File(dir, STATS_FOLDER)
        delete(statsDir)
        return statsDir.delete()
    }

    fun delete(file: File) {
        if (file.isDirectory) {
            for (f in file.listFiles()) delete(f)
        }
        file.delete()
    }
}
