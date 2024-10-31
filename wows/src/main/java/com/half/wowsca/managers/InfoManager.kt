package com.half.wowsca.managers

import android.content.Context
import com.google.gson.GsonBuilder
import com.half.wowsca.model.encyclopedia.holders.AchievementsHolder
import com.half.wowsca.model.encyclopedia.holders.CaptainSkillHolder
import com.half.wowsca.model.encyclopedia.holders.ExteriorHolder
import com.half.wowsca.model.encyclopedia.holders.ShipsHolder
import com.half.wowsca.model.encyclopedia.holders.UpgradeHolder
import com.half.wowsca.model.encyclopedia.holders.WarshipsStats
import com.utilities.logging.Dlog.wtf
import com.utilities.preferences.Prefs
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileReader
import java.io.PrintWriter
import java.util.Calendar

/**
 * Created by slai4 on 9/23/2015.
 */
class InfoManager {
    private var shipInfo: ShipsHolder? = null

    private var upgrades: UpgradeHolder? = null

    private var achievementInfo: AchievementsHolder? = null

    private var warshipsStats: WarshipsStats? = null

    private var exteriorItems: ExteriorHolder? = null

    private var captainSkills: CaptainSkillHolder? = null

    fun isInfoThere(ctx: Context): Boolean {
        var isInfoThere = false
        val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
        val shipInfo = File(dir, SHIP_INFO_FILE)
        val achievementInfo = File(dir, ACHIEVEMENT_INFO_FILE)
        val warshipsInfo = File(dir, WARSHIP_STATS_INFO_FILE)
        val equipmentInfo = File(dir, EQUIPMENT_INFO_FILE)
        isInfoThere = (shipInfo.exists() && achievementInfo.exists()
                && warshipsInfo.exists() && equipmentInfo.exists())
        if (timeToUpdate(ctx)) {
            isInfoThere = false
        }
        return isInfoThere
    }

    private fun timeToUpdate(ctx: Context): Boolean {
        val pref = Prefs(ctx)
        val time = pref.getLong(INFO_UPDATED_TIME, Calendar.getInstance().timeInMillis)
        var canUpdate = true
        if (time != 0L) {
            val now = Calendar.getInstance().timeInMillis
            val dif = now - time
            val days = (((dif / 1000) / 60) / 60) / 24
            if (days < DAYS_BETWEEN_DOWNLOAD) {
                canUpdate = false
            }
            wtf("InfoManager", "canUpdate = $canUpdate days = $days dif = $dif")
        }
        return canUpdate
    }

    /**
     * Load on backthread. Grabs all saved data and starts up info manager
     *
     * @param ctx
     */
    fun load(ctx: Context) {
        getAchievements(ctx)
        getShipInfo(ctx)
        getShipStats(ctx)
        getUpgrades(ctx)
        getCaptainSkills(ctx)
        getExteriorItems(ctx)
    }

    fun updated(ctx: Context?) {
        val pref = Prefs(ctx)
        pref.setLong(INFO_UPDATED_TIME, Calendar.getInstance().timeInMillis)
    }

    fun getShipInfo(ctx: Context): ShipsHolder {
        if (shipInfo == null || (shipInfo?.items != null && shipInfo?.items?.size == 0)) {
            try {
                val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
                val tempStats = File(dir, SHIP_INFO_FILE)
                val fr = FileReader(tempStats)
                val builder = GsonBuilder()
                builder.serializeSpecialFloatingPointValues()
                val gson = builder.create()
                shipInfo = gson.fromJson(fr, ShipsHolder::class.java)
                fr.close()
            } catch (e: FileNotFoundException) {
            } catch (e: Exception) {
            }
        }
        if (shipInfo == null) {
            shipInfo = ShipsHolder()
        }
        return shipInfo!!
    }

    fun setShipInfo(ctx: Context, shipInfo: ShipsHolder?) {
        this.shipInfo = shipInfo

        val builder = GsonBuilder()
        builder.serializeSpecialFloatingPointValues()
        val gson = builder.create()

        val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
        val achievementsFile = File(dir, SHIP_INFO_FILE)
        try {
            val fos = FileOutputStream(achievementsFile)
            val pw = PrintWriter(fos)
            pw.print(gson.toJson(shipInfo))
            pw.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAchievements(ctx: Context): AchievementsHolder {
        if (achievementInfo == null || (achievementInfo?.items != null && achievementInfo?.items?.size == 0)) {
            try {
                val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
                val tempStats = File(dir, ACHIEVEMENT_INFO_FILE)
                val fr = FileReader(tempStats)
                val builder = GsonBuilder()
                builder.serializeSpecialFloatingPointValues()
                val gson = builder.create()
                achievementInfo = gson.fromJson(fr, AchievementsHolder::class.java)
                fr.close()
            } catch (e: FileNotFoundException) {
            } catch (e: Exception) {
            }
        }
        if (achievementInfo == null) {
            achievementInfo = AchievementsHolder()
        }
        return achievementInfo!!
    }

    fun setAchievements(ctx: Context, achievementInfo: AchievementsHolder?) {
        this.achievementInfo = achievementInfo
        val builder = GsonBuilder()
        builder.serializeSpecialFloatingPointValues()
        val gson = builder.create()

        val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
        val achievementsFile = File(dir, ACHIEVEMENT_INFO_FILE)
        try {
            val fos = FileOutputStream(achievementsFile)
            val pw = PrintWriter(fos)
            pw.print(gson.toJson(achievementInfo))
            pw.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getShipStats(ctx: Context): WarshipsStats {
        if (warshipsStats == null || (warshipsStats!!.shiP_STATS != null && warshipsStats!!.shiP_STATS.size == 0)) {
            try {
                val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
                val tempStats = File(dir, WARSHIP_STATS_INFO_FILE)
                val fr = FileReader(tempStats)
                val builder = GsonBuilder()
                builder.serializeSpecialFloatingPointValues()
                val gson = builder.create()
                warshipsStats = gson.fromJson(fr, WarshipsStats::class.java)
                fr.close()
            } catch (e: FileNotFoundException) {
            } catch (e: Exception) {
            }
        }
        if (warshipsStats == null) warshipsStats = WarshipsStats()
        return warshipsStats!!
    }

    fun setWarshipsStats(ctx: Context, warshipsStats: WarshipsStats?) {
        this.warshipsStats = warshipsStats
        val builder = GsonBuilder()
        builder.serializeSpecialFloatingPointValues()
        val gson = builder.create()

        val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
        val achievementsFile = File(dir, WARSHIP_STATS_INFO_FILE)
        try {
            val fos = FileOutputStream(achievementsFile)
            val pw = PrintWriter(fos)
            pw.print(gson.toJson(warshipsStats))
            pw.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getUpgrades(ctx: Context): UpgradeHolder {
        if (upgrades == null || (upgrades!!.items != null && upgrades?.items?.size == 0)) {
            try {
                val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
                val tempStats = File(dir, EQUIPMENT_INFO_FILE)
                val fr = FileReader(tempStats)
                val builder = GsonBuilder()
                builder.serializeSpecialFloatingPointValues()
                val gson = builder.create()
                upgrades = gson.fromJson(fr, UpgradeHolder::class.java)
                fr.close()
            } catch (e: FileNotFoundException) {
            } catch (e: Exception) {
            }
        }
        if (upgrades == null) {
            upgrades = UpgradeHolder()
        }
        return upgrades!!
    }

    fun setUpgrades(ctx: Context, equipment: UpgradeHolder?) {
        this.upgrades = equipment

        val builder = GsonBuilder()
        builder.serializeSpecialFloatingPointValues()
        val gson = builder.create()

        val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
        val achievementsFile = File(dir, EQUIPMENT_INFO_FILE)
        try {
            val fos = FileOutputStream(achievementsFile)
            val pw = PrintWriter(fos)
            pw.print(gson.toJson(equipment))
            pw.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getExteriorItems(ctx: Context): ExteriorHolder {
        if (exteriorItems == null || (exteriorItems?.items != null && exteriorItems?.items?.size == 0)) {
            try {
                val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
                val tempStats = File(dir, EXTERIOR_ITEMS_FILE)
                val fr = FileReader(tempStats)
                val builder = GsonBuilder()
                builder.serializeSpecialFloatingPointValues()
                val gson = builder.create()
                exteriorItems = gson.fromJson(fr, ExteriorHolder::class.java)
                fr.close()
            } catch (e: FileNotFoundException) {
            } catch (e: Exception) {
            }
        }
        if (exteriorItems == null) {
            exteriorItems = ExteriorHolder()
        }
        return exteriorItems!!
    }

    fun setExteriorItems(ctx: Context, exteriorHolder: ExteriorHolder?) {
        this.exteriorItems = exteriorHolder

        val builder = GsonBuilder()
        builder.serializeSpecialFloatingPointValues()
        val gson = builder.create()

        val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
        val achievementsFile = File(dir, EXTERIOR_ITEMS_FILE)
        try {
            val fos = FileOutputStream(achievementsFile)
            val pw = PrintWriter(fos)
            pw.print(gson.toJson(exteriorHolder))
            pw.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCaptainSkills(ctx: Context): CaptainSkillHolder {
        if (captainSkills == null || (captainSkills?.items != null && captainSkills?.items?.size == 0)) {
            try {
                val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
                val tempStats = File(dir, CAPTAIN_SKILLS_FILE)
                val fr = FileReader(tempStats)
                val builder = GsonBuilder()
                builder.serializeSpecialFloatingPointValues()
                val gson = builder.create()
                captainSkills = gson.fromJson(fr, CaptainSkillHolder::class.java)
                fr.close()
            } catch (e: FileNotFoundException) {
            } catch (e: Exception) {
            }
        }
        if (captainSkills == null) {
            captainSkills = CaptainSkillHolder()
        }
        return captainSkills!!
    }

    fun setCaptainSkills(ctx: Context, captainSkillHolder: CaptainSkillHolder?) {
        this.captainSkills = captainSkillHolder

        val builder = GsonBuilder()
        builder.serializeSpecialFloatingPointValues()
        val gson = builder.create()

        val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
        val achievementsFile = File(dir, CAPTAIN_SKILLS_FILE)
        try {
            val fos = FileOutputStream(achievementsFile)
            val pw = PrintWriter(fos)
            pw.print(gson.toJson(captainSkillHolder))
            pw.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val SHIP_INFO_FILE: String = "shipInfoFile"
        const val ACHIEVEMENT_INFO_FILE: String = "achievementInfoFile"
        const val WARSHIP_STATS_INFO_FILE: String = "warshipStatsInfoFile"
        const val EQUIPMENT_INFO_FILE: String = "equipmentInfoFile"
        const val INFO_UPDATED_TIME: String = "info_updated_time"
        const val EXTERIOR_ITEMS_FILE: String = "exterior_items"
        const val CAPTAIN_SKILLS_FILE: String = "captain_skills_file"
        private const val DAYS_BETWEEN_DOWNLOAD = 5

        @JvmStatic
        fun purge(ctx: Context) {
            val dir = ctx.getDir(CaptainManager.DIRECTORY_NAME, Context.MODE_PRIVATE)
            val shipInfo = File(dir, SHIP_INFO_FILE)
            val achievementInfo = File(dir, ACHIEVEMENT_INFO_FILE)
            val warshipStatsFile = File(dir, WARSHIP_STATS_INFO_FILE)
            val upgradesFile = File(dir, EQUIPMENT_INFO_FILE)
            val exteriorFile = File(dir, EXTERIOR_ITEMS_FILE)
            val skillsFile = File(dir, CAPTAIN_SKILLS_FILE)
            shipInfo.delete()
            achievementInfo.delete()
            warshipStatsFile.delete()
            upgradesFile.delete()
            exteriorFile.delete()
            skillsFile.delete()
        }
    }
}
