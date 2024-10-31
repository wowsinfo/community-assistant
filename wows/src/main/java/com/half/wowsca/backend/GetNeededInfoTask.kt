package com.half.wowsca.backend

import android.content.Context
import android.os.AsyncTask
import android.text.TextUtils
import com.half.wowsca.CAApp
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.getServerLanguage
import com.half.wowsca.CAApp.Companion.getServerType
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.model.encyclopedia.holders.AchievementsHolder
import com.half.wowsca.model.encyclopedia.holders.CaptainSkillHolder
import com.half.wowsca.model.encyclopedia.holders.ExteriorHolder
import com.half.wowsca.model.encyclopedia.holders.ShipsHolder
import com.half.wowsca.model.encyclopedia.holders.UpgradeHolder
import com.half.wowsca.model.encyclopedia.holders.WarshipsStats
import com.half.wowsca.model.encyclopedia.items.AchievementInfo
import com.half.wowsca.model.encyclopedia.items.CaptainSkill
import com.half.wowsca.model.encyclopedia.items.EquipmentInfo
import com.half.wowsca.model.encyclopedia.items.ExteriorItem
import com.half.wowsca.model.encyclopedia.items.ShipInfo
import com.half.wowsca.model.encyclopedia.items.ShipStat
import com.half.wowsca.model.enums.Server
import com.half.wowsca.model.queries.InfoQuery
import com.half.wowsca.model.result.InfoResult
import com.utilities.Utils.getInputStreamResponse
import com.utilities.logging.Dlog.d
import com.utilities.logging.Dlog.wtf
import com.utilities.preferences.Prefs
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

/**
 * Created by slai4 on 9/21/2015.
 */
class GetNeededInfoTask : AsyncTask<InfoQuery?, Void?, InfoResult>() {
    private var ctx: Context? = null

    override fun doInBackground(vararg params: InfoQuery?): InfoResult? {
        val languagePart = "&language=" + getServerLanguage(ctx!!)
        val query = params[0] ?: return null
        val result = InfoResult()
        val url =
            CAApp.WOWS_API_SITE_ADDRESS + query.server.suffix + "/wows/encyclopedia/ships/?application_id=" + query.server.appId + languagePart
        wtf("SHIPS URL", url)
        val shipFeed = getURLResult(url)

        val url2 =
            CAApp.WOWS_API_SITE_ADDRESS + query.server.suffix + "/wows/encyclopedia/achievements/?application_id=" + query.server.appId + languagePart
        wtf("ACHIEVEMENTS URL", url2)
        val achievementFeed = getURLResult(url2)

        val url4 =
            CAApp.WOWS_API_SITE_ADDRESS + query.server.suffix + "/wows/encyclopedia/consumables/?application_id=" + query.server.appId + languagePart + "&type=Modernization"
        wtf("UPGRADES URL", url4)
        val upgradesFeed = getURLResult(url4)

        val url5 =
            CAApp.WOWS_API_SITE_ADDRESS + query.server.suffix + "/wows/encyclopedia/crewskills/?application_id=" + query.server.appId + languagePart
        wtf("SKILLS URL", url5)
        val captainSkillsFeed = getURLResult(url5)

        val url6 =
            CAApp.WOWS_API_SITE_ADDRESS + query.server.suffix + "/wows/encyclopedia/consumables/?application_id=" + query.server.appId + languagePart + "&type=Flags"
        wtf("EXTERIOR URL", url6)
        val exteriorItemsFeed = getURLResult(url6)

        //        Server s = CAApp.getServerType(ctx);
//        String url7 = "https://api." + s.getWarshipsToday() + ".warships.today/json/wows/ratings/warships-today-rating/coefficients";
//        Dlog.wtf("WARSHIPS_TODAY URL", url7);
//        String WARSHIPS_TODAY = getURLResult(url7);
        val s = getServerType(ctx)
        var prefix = ""
        when (s) {
            Server.NA -> prefix = "na"
            Server.EU -> {}
            Server.SEA -> prefix = "asia"
        }
        val url7 = "https://$prefix.wows-numbers.com/ships"
        wtf("WARSHIPS_TODAY URL", url7)
        val wowsNumbers = getURLResult(url7)

        parseShipInformation(query, result, shipFeed, languagePart)

        parseAchievementInfo(result, achievementFeed)

        parseUpgrades(result, upgradesFeed)

        parseCaptainSkills(result, captainSkillsFeed)

        parseExteriorItems(result, exteriorItemsFeed)

        parseWoWsNumbers(result, wowsNumbers)

        //save ships and infos to InfoManager
        if (result.ships != null && !result.ships.isEmpty()) {
            val shipsHolder = ShipsHolder()
            shipsHolder.items = result.ships
            infoManager!!.setShipInfo(ctx!!, shipsHolder)
        }

        if (result.achievements != null && !result.achievements.isEmpty()) {
            val achi = AchievementsHolder()
            achi.items = result.achievements
            infoManager!!.setAchievements(ctx!!, achi)
        }

        if (result.shipStat != null && !result.shipStat.isEmpty()) {
            val stat = WarshipsStats()
            stat.set(result.shipStat)
            infoManager!!.setWarshipsStats(ctx!!, stat)
        }

        if (result.equipment != null && !result.equipment.isEmpty()) {
            val e = UpgradeHolder()
            e.items = result.equipment
            infoManager!!.setUpgrades(ctx!!, e)
        }

        if (result.exteriorItem != null && !result.exteriorItem.items!!.isEmpty()) {
            infoManager!!.setExteriorItems(ctx!!, result.exteriorItem)
        }

        if (result.skillHolder != null && !result.skillHolder.items!!.isEmpty()) {
            infoManager!!.setCaptainSkills(ctx!!, result.skillHolder)
        }
        wtf("Infomanager", "done $result")

        infoManager!!.updated(ctx)
        return result
    }

    private fun parseWoWsNumbers(result: InfoResult, wowsNumbers: String?) {
        try {
            val doc = Jsoup.parse(wowsNumbers)
            val scriptTags = doc.getElementsByTag("script")
            for (tag in scriptTags) {
                for (node in tag.dataNodes()) {
                    val nodeData = node.wholeData
                    if (nodeData.contains("var dataProvider")) {
                        val dataSplit = nodeData.split("=".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        val ships = dataSplit[2]
                        val secondSplit =
                            ships.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val sb = StringBuilder()
                        for (i in 0 until secondSplit.size - 1) {
                            sb.append(secondSplit[i])
                        }
                        val list = JSONArray(sb.toString())
                        val shipStatMap: MutableMap<Long, ShipStat> = HashMap()
                        for (i in 0 until list.length()) {
                            val `object` = list.optJSONObject(i)
                            val stat = ShipStat()
                            val shipId = `object`.optLong("ship_id")
                            stat.dmg_dlt = `object`.optInt("average_damage_dealt").toFloat()
                            stat.frags = `object`.optDouble("average_frags").toFloat()
                            stat.pls_kd = `object`.optDouble("average_planes_killed").toFloat()
                            stat.wins = `object`.optDouble("win_rate").toFloat() / 100
                            if (stat.dmg_dlt != -1f) shipStatMap[shipId] = stat
                        }
                        result.shipStat = shipStatMap
                        if (shipStatMap.size > 250) {
                            try {
                                val prefs = Prefs(ctx)
                                prefs.setString(SAVED_FRESH_DATA, sb.toString())
                                d("GetNeededInfo", "Saved Info")
                            } catch (e: OutOfMemoryError) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            setupDefaultData(result)
        }
    }

    private fun setupDefaultData(result: InfoResult) {
        // last updated 9/5/18
        val s = getServerType(ctx)
        var fileName = ""
        fileName = when (s) {
            Server.NA -> "raw-data-na.txt"
            Server.EU -> "raw-data-eu.txt"
            Server.SEA -> "raw-data-asia.txt"
        }
        val prefs = Prefs(ctx)
        val lastOutput = prefs.getString(SAVED_FRESH_DATA, "")
        val sb = StringBuilder()
        if (TextUtils.isEmpty(lastOutput)) {
            var reader: BufferedReader? = null
            try {
                reader = BufferedReader(
                    InputStreamReader(ctx!!.assets.open(fileName))
                )

                // do reading, usually loop until end of file reading
                var mLine: String?
                while ((reader.readLine().also { mLine = it }) != null) {
                    //process line
                    sb.append(mLine)
                }
            } catch (e: IOException) {
                //log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        //log the exception
                    }
                }
            }
        } else {
            sb.append(lastOutput)
        }
        val output = sb.toString()
        if (!TextUtils.isEmpty(output)) {
            try {
                val body = JSONArray(output)
                val shipStatMap: MutableMap<Long, ShipStat> = HashMap()
                for (i in 0 until body.length()) {
                    val `object` = body.optJSONObject(i)
                    val stat = ShipStat()
                    val shipId = `object`.optLong("ship_id")
                    stat.dmg_dlt = `object`.optInt("average_damage_dealt").toFloat()
                    stat.frags = `object`.optDouble("average_frags").toFloat()
                    stat.pls_kd = `object`.optDouble("average_planes_killed").toFloat()
                    stat.wins = `object`.optDouble("win_rate").toFloat() / 100
                    if (stat.dmg_dlt != -1f) shipStatMap[shipId] = stat
                }
                result.shipStat = shipStatMap
            } catch (e: JSONException) {
            }
        }
    }

    private fun parseWarshipsToday(result: InfoResult, warships_today: String?) {
        if (warships_today != null) {
            var `object`: JSONObject? = null
            try {
                `object` = JSONObject(warships_today)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (`object` != null) {
                val data = `object`.optJSONArray("expected")
                val shipStatMap: MutableMap<Long, ShipStat> = HashMap()
                for (i in 0 until data.length()) {
                    val obj = data.optJSONObject(i)
                    val shipId = obj.optLong("ship_id")
                    val info = ShipStat()
                    info.parse(obj)
                    shipStatMap[shipId] = info
                }
                result.shipStat = shipStatMap
            }
        }
    }

    private fun parseCaptainSkills(result: InfoResult, captainSkillsFeed: String?) {
        if (captainSkillsFeed != null) {
            var `object`: JSONObject? = null
            try {
                `object` = JSONObject(captainSkillsFeed)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (`object` != null) {
                val data = `object`.optJSONObject("data")
                if (data != null) {
                    val iter = data.keys()
                    val exteriorItems = CaptainSkillHolder()
                    while (iter.hasNext()) {
                        val key = iter.next()
                        val item = data.optJSONObject(key)
                        val skill = CaptainSkill()
                        skill.id = key.toLong()
                        skill.parse(item)
                        exteriorItems.put(key, skill)
                    }
                    result.skillHolder = exteriorItems
                }
            }
        }
    }

    private fun parseExteriorItems(result: InfoResult, exteriorItemsFeed: String?) {
        if (exteriorItemsFeed != null) {
            var `object`: JSONObject? = null
            try {
                `object` = JSONObject(exteriorItemsFeed)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (`object` != null) {
                val data = `object`.optJSONObject("data")
                if (data != null) {
                    val iter = data.keys()
                    val exteriorItems = ExteriorHolder()
                    while (iter.hasNext()) {
                        val key = iter.next()
                        val item = data.optJSONObject(key)
                        val exteriorItem = ExteriorItem()
                        exteriorItem.parse(item)
                        exteriorItems.put(exteriorItem.id, exteriorItem)
                    }
                    result.exteriorItem = exteriorItems
                }
            }
        }
    }

    private fun parseUpgrades(result: InfoResult, upgradesFeed: String?) {
        if (upgradesFeed != null) {
            var `object`: JSONObject? = null
            try {
                `object` = JSONObject(upgradesFeed)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (`object` != null) {
                val data = `object`.optJSONObject("data")
                if (data != null) {
                    val equipmentInfo: MutableMap<Long, EquipmentInfo> = HashMap()
                    val keys = data.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val id = key.toLong()
                        val equipment = data.optJSONObject(key)
                        if (equipment != null) {
                            val info = EquipmentInfo()
                            info.parse(equipment)
                            info.id = id
                            equipmentInfo[id] = info
                        }
                    }
                    result.equipment = equipmentInfo
                }
            }
        }
    }

    private fun parseAverageData(result: InfoResult, warshipstatsFeed: String?) {
        if (warshipstatsFeed != null) {
            var `object`: JSONObject? = null
            try {
                `object` = JSONObject(warshipstatsFeed)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (`object` != null) {
                val data = `object`.optJSONObject("data")
                val shipStatMap: MutableMap<Long, ShipStat> = HashMap()
                val keys = data.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val shipStat = data.optJSONObject(key)
                    if (shipStat != null) {
                        val info = ShipStat()
                        info.parse(shipStat)
                        shipStatMap[key.toLong()] = info
                    }
                }
                result.shipStat = shipStatMap
            }
        }
    }

    private fun parseAchievementInfo(result: InfoResult, achievementFeed: String?) {
        if (achievementFeed != null) {
            var `object`: JSONObject? = null
            try {
                `object` = JSONObject(achievementFeed)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (`object` != null) {
                val data = `object`.optJSONObject("data")
                if (data != null) {
                    val battle = data.optJSONObject("battle")
                    if (battle != null) {
                        val achievementInfo: MutableMap<String, AchievementInfo> = HashMap()
                        val keys = battle.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            val achievement = battle.optJSONObject(key)
                            if (achievement != null) {
                                val info = AchievementInfo()
                                info.name = achievement.optString("name")
                                info.description = achievement.optString("description")
                                info.image = achievement.optString("image")
                                info.id = key
                                achievementInfo[key] = info
                            }
                        }
                        result.achievements = achievementInfo
                    }
                }
            }
        }
    }

    private fun parseShipInformation(
        query: InfoQuery,
        result: InfoResult,
        shipFeed: String?,
        languagePart: String
    ) {
        if (shipFeed != null) {
            var `object`: JSONObject? = null
            try {
                `object` = JSONObject(shipFeed)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (`object` != null) {
                val meta = `object`.optJSONObject("meta")
                var page = meta.optInt("page")
                val pageTotal = meta.optInt("page_total")
                page++

                val feeds: MutableList<JSONObject> = ArrayList()
                val data = `object`.optJSONObject("data")
                feeds.add(data)
                while (page <= pageTotal) {
                    val url =
                        CAApp.WOWS_API_SITE_ADDRESS + query.server.suffix + "/wows/encyclopedia/ships/?application_id=" + query.server.appId + languagePart + "&page_no=" + page
                    wtf("SHIPS URL AGAIN", url)
                    val feed = getURLResult(url)
                    var obj: JSONObject? = null
                    try {
                        obj = JSONObject(feed)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (obj != null) {
                        val datum = obj.optJSONObject("data")
                        feeds.add(datum)
                    }
                    page++
                }
                val ships: MutableMap<Long, ShipInfo> = HashMap()
                for (datum in feeds) {
                    if (datum != null) {
                        val keys = datum.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            val id = key.toLong()
                            val ship = datum.optJSONObject(key)
                            if (ship != null) {
                                val info = ShipInfo()
                                info.parse(ship)
                                info.shipId = id
                                ships[id] = info
                            }
                        }
                    }
                }
                result.ships = ships
            }
        }
    }

    fun getURLResult(url: String?): String? {
        var results: String? = null
        try {
            val feed = URL(url)
            results = getInputStreamResponse(feed)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return results
    }

    override fun onPostExecute(shipsResult: InfoResult) {
        super.onPostExecute(shipsResult)
        eventBus.post(shipsResult)
    }

    fun setCtx(ctx: Context?) {
        this.ctx = ctx
    }

    companion object {
        const val SAVED_FRESH_DATA: String = "SAVED_FRESH_DATA"
    }
}
