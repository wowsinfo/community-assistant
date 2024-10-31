package com.half.wowsca.backend

import android.content.Context
import android.os.AsyncTask
import android.text.TextUtils
import android.util.SparseArray
import com.half.wowsca.CAApp
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.managers.CARatingManager.CalculateCAShipRating
import com.half.wowsca.model.Achievement
import com.half.wowsca.model.Captain
import com.half.wowsca.model.CaptainDetails
import com.half.wowsca.model.CaptainPrivateInformation
import com.half.wowsca.model.Ship
import com.half.wowsca.model.ShipCompare
import com.half.wowsca.model.Statistics
import com.half.wowsca.model.queries.CaptainQuery
import com.half.wowsca.model.ranked.RankedInfo
import com.half.wowsca.model.ranked.SeasonInfo
import com.half.wowsca.model.result.CaptainResult
import com.utilities.Utils.getInputStreamResponse
import com.utilities.logging.Dlog.d
import com.utilities.logging.Dlog.wtf
import org.json.JSONException
import org.json.JSONObject
import java.net.URL
import java.util.Collections

/**
 * Created by slai4 on 9/19/2015.
 */
class GetCaptainTask : AsyncTask<CaptainQuery?, Int?, CaptainResult>() {
    var ctx: Context? = null

    override fun doInBackground(vararg params: CaptainQuery?): CaptainResult? {
        val query = params[0] ?: return null

        val token = if (!TextUtils.isEmpty(query.token)) "&access_token=" + query.token else ""

        val result = CaptainResult()
        val url =
            CAApp.WOWS_API_SITE_ADDRESS + query.server.suffix + "/wows/account/info/?application_id=" + query.server.appId + "&account_id=" + query.id + token + "&extra=statistics.club,statistics.pve,statistics.pvp_div2,statistics.pvp_div3,statistics.pvp_solo"
        wtf("Details URL", url)

        val playerDetailsFeed = getURLResult(url)

        val url2 =
            CAApp.WOWS_API_SITE_ADDRESS + query.server.suffix + "/wows/ships/stats/?application_id=" + query.server.appId + "&account_id=" + query.id + "&extra=club,pve,pvp_div2,pvp_div3,pvp_solo"
        wtf("Ships URL", url2)

        val playerShipsFeed = getURLResult(url2)

        val url3 =
            CAApp.WOWS_API_SITE_ADDRESS + query.server.suffix + "/wows/account/achievements/?application_id=" + query.server.appId + "&account_id=" + query.id
        wtf("Achievement URL", url3)

        val achievementsFeed = getURLResult(url3)

        val ranked =
            CAApp.WOWS_API_SITE_ADDRESS + query.server.suffix + "/wows/seasons/accountinfo/?application_id=" + query.server.appId + "&account_id=" + query.id
        wtf("RankedAccountURL", ranked)

        val rankedAccountFeed = getURLResult(ranked)

        val rankedShipStats =
            CAApp.WOWS_API_SITE_ADDRESS + query.server.suffix + "/wows/seasons/shipstats/?application_id=" + query.server.appId + "&account_id=" + query.id
        wtf("RankedShipURL", rankedShipStats)

        val rankedShipsFeed = getURLResult(rankedShipStats)

        val clanInfoQuery =
            CAApp.WOWS_API_SITE_ADDRESS + query.server.suffix + "/wows/clans/accountinfo/?application_id=" + query.server.appId + "&account_id=" + query.id + "&extra=clan"

        wtf("ClanInfoQuery", clanInfoQuery)

        val clanInfoFeed = getURLResult(clanInfoQuery)

        if (!TextUtils.isEmpty(playerDetailsFeed)) {
            val captain = Captain()
            var playerDetailsResult: JSONObject? = null
            try {
                playerDetailsResult = JSONObject(playerDetailsFeed)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (playerDetailsResult != null) {
                captain.id = query.id
                captain.name = query.name
                captain.server = query.server
                val data = playerDetailsResult.optJSONObject(DATA)
                if (data != null && data.has("" + query.id)) {
                    val playerObject = data.optJSONObject("" + query.id)
                    if (playerObject != null) {
                        val hidden = playerObject.optBoolean("hidden_profile")
                        if (!hidden) {
                            captain.details = CaptainDetails.parse(playerObject)

                            val stats = playerObject.optJSONObject("statistics")

                            captain.pveDetails = Statistics.parse(stats.optJSONObject("pve"))
                            captain.setPvpSoloDetails(Statistics.parse(stats.optJSONObject("pvp_solo")))
                            captain.pvpDiv2Details =
                                Statistics.parse(stats.optJSONObject("pvp_div2"))
                            captain.pvpDiv3Details =
                                Statistics.parse(stats.optJSONObject("pvp_div3"))
                            captain.teamBattleDetails =
                                Statistics.parse(stats.optJSONObject("club"))

                            grabPrivateInformation(captain, playerObject)

                            grabShipInformationAndGraphs(
                                query,
                                playerShipsFeed,
                                achievementsFeed,
                                captain
                            )

                            grabRankedInfo(query, rankedAccountFeed, captain)

                            grabRankedShipsInfo(query, rankedShipsFeed, captain)

                            grabClanInfo(captain, clanInfoFeed)

                            result.isHidden = false
                        } else {
                            result.isHidden = true
                        }
                    } else {
                    }
                    result.captain = captain
                }
            }
        }
        return result
    }

    private fun grabClanInfo(captain: Captain, clanInfoFeed: String?) {
        var playerDetailsResult: JSONObject? = null
        try {
            playerDetailsResult = JSONObject(clanInfoFeed)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (playerDetailsResult != null) {
            val data = playerDetailsResult.optJSONObject(DATA)
            if (data != null) {
                val playerObject = data.optJSONObject(captain.id.toString() + "")
                if (playerObject != null) {
                    val clan = playerObject.optJSONObject("clan")
                    if (clan != null) {
                        captain.clanName = clan.optString("tag")
                    }
                }
            }
        }
    }

    private fun grabPrivateInformation(captain: Captain, playerObject: JSONObject) {
        val privInfo = playerObject.optJSONObject("private")
        if (privInfo != null) {
            val information = CaptainPrivateInformation()
            information.parse(privInfo)
            captain.information = information
        }
    }

    private fun grabShipInformationAndGraphs(
        query: CaptainQuery,
        playerShipsFeed: String?,
        achievementsFeed: String?,
        captain: Captain
    ) {
        if (!TextUtils.isEmpty(playerShipsFeed)) {
            var shipsResult: JSONObject? = null
            try {
                shipsResult = JSONObject(playerShipsFeed)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (shipsResult != null) {
                val shipsData = shipsResult.optJSONObject(DATA)
                if (shipsData != null) {
                    val ships = shipsData.optJSONArray("" + query.id)
                    captain.ships = ArrayList()
                    var eDamage = 0f
                    var eWinRate = 0f
                    var eKills = 0f //                            , eXP = 0
                    var ePlanes = 0f
                    //                            , eSurvial = 0, eSurWins = 0

                    val ratingsPerTier = SparseArray<Float>()
                    val shipsPerTier = SparseArray<Float>() // this is to average the ratings
                    val battlePerTier = SparseArray<Float>()

                    var tiers = 0
                    var totalCARating = 0f
                    var numOfShips = 0f
                    val stats = infoManager!!.getShipStats(ctx!!)
                    val shipsMap = infoManager!!.getShipInfo(ctx!!)
                    if (ships != null) {
                        var maxSurvivalRate = 0f
                        var maxSurvivalRateShipId = 0L

                        var maxWinRate = 0f
                        var maxWinRateShipId = 0L

                        var maxTotalKills = 0
                        var maxTotalKillsShipId = 0L

                        var maxPlayed = 0
                        var maxPlayedShipId = 0L

                        var maxCARating = 0f
                        var maxCARatingShipId = 0L

                        var maxMostTraveled = 0
                        var maxMostTraveledShipId: Long = 0

                        var maxAvgDmg = 0f
                        var maxAvgDmgShipId = 0L

                        var maxTotalPlanes = 0f
                        var maxTotalPlanesShipId = 0L

                        var maxTotalExp: Long = 0
                        var maxTotalExpShipId = 0L

                        var maxTotalDamage = 0.0
                        var maxTotalDmgShipId = 0L

                        var maxSurvivedWins = 0f
                        var maxTotalSurWinsShipId = 0L

                        var maxMBAccuracy = 0f
                        var maxMBAccuracyShipId = 0L

                        var maxTBAccuracy = 0f
                        var maxTBAccuracyShipId = 0L

                        var maxSpotted = 0.0
                        var maxSpottedShipId = 0L

                        var maxDamageScouting = 0.0
                        var maxDamageScoutingShipId = 0L

                        var maxArgo = 0.0
                        var maxArgoShipId = 0L

                        var maxTorpArgo = 0.0
                        var maxTorpArgoShipId = 0L

                        var maxSuppressionCount = 0.0
                        var maxSuppressionCountShipId = 0L

                        for (i in 0 until ships.length()) {
                            val ship = ships.optJSONObject(i)
                            val s = Ship.parse(ship)
                            val stat = stats[s.shipId]
                            val shipInfo = shipsMap[s.shipId]
                            val battles = s.battles.toFloat()
                            if (battles > 0 && shipInfo != null) {
                                val tier = shipInfo.tier
                                tiers = (tiers + (tier * battles)).toInt()
                                if (stat != null) {
                                    val rating = CalculateCAShipRating(s, stat)
                                    s.caRating = rating
                                    addTierNumber(ratingsPerTier, tier, rating)
                                    totalCARating += rating

                                    eDamage += stat.dmg_dlt
                                    eWinRate += stat.wins
                                    eKills += stat.frags
                                    ePlanes += stat.pls_kd
                                }
                                addTierNumber(shipsPerTier, tier, 1f)
                                addTierNumber(battlePerTier, tier, s.battles.toFloat())

                                if (maxTotalKills < s.frags) {
                                    maxTotalKills = s.frags
                                    maxTotalKillsShipId = s.shipId
                                }
                                if (maxPlayed < s.battles) {
                                    maxPlayed = s.battles
                                    maxPlayedShipId = s.shipId
                                }
                                if (maxTotalPlanes < s.planesKilled) {
                                    maxTotalPlanes = s.planesKilled.toFloat()
                                    maxTotalPlanesShipId = s.shipId
                                }
                                if (maxTotalExp < s.totalXP) {
                                    maxTotalExp = s.totalXP
                                    maxTotalExpShipId = s.shipId
                                }
                                if (maxTotalDamage < s.totalDamage) {
                                    maxTotalDamage = s.totalDamage
                                    maxTotalDmgShipId = s.shipId
                                }
                                if (maxMostTraveled < s.distanceTraveled) {
                                    maxMostTraveled = s.distanceTraveled
                                    maxMostTraveledShipId = s.shipId
                                }
                                //                            float maxSpotted = 0;
                                if (maxSpotted < s.maxSpotted) {
                                    maxSpotted = s.maxSpotted
                                    maxSpottedShipId = s.shipId
                                }
                                //                            float maxDamageScouting = 0;
                                if (maxDamageScouting < s.maxDamageScouting) {
                                    maxDamageScouting = s.maxDamageScouting
                                    maxDamageScoutingShipId = s.shipId
                                }
                                //                            float maxArgo = 0;
                                if (maxArgo < s.totalArgoDamage) {
                                    maxArgo = s.totalArgoDamage
                                    maxArgoShipId = s.shipId
                                }
                                //                            float maxTorpArgo = 0;
                                if (maxTorpArgo < s.torpArgoDamage) {
                                    maxTorpArgo = s.torpArgoDamage
                                    maxTorpArgoShipId = s.shipId
                                }
                                //                            float maxSuppressionCount = 0;
                                if (maxSuppressionCount < s.maxSuppressionCount) {
                                    maxSuppressionCount = s.maxSuppressionCount
                                    maxSuppressionCountShipId = s.shipId
                                }
                                val avgDmg = (s.totalDamage / battles).toFloat()
                                if (maxAvgDmg < avgDmg) {
                                    maxAvgDmg = avgDmg
                                    maxAvgDmgShipId = s.shipId
                                }
                                if (s.mainBattery.shots > 0) {
                                    val mbAcc = s.mainBattery.hits / s.mainBattery.shots.toFloat()
                                    if (maxMBAccuracy < mbAcc) {
                                        maxMBAccuracy = mbAcc
                                        maxMBAccuracyShipId = s.shipId
                                    }
                                }

                                if (s.torpedoes.shots > 0) {
                                    val tbAcc = s.torpedoes.hits / s.torpedoes.shots.toFloat()
                                    if (maxTBAccuracy < tbAcc) {
                                        maxTBAccuracy = tbAcc
                                        maxTBAccuracyShipId = s.shipId
                                    }
                                }
                                if (battles > 4) {
                                    val winRate = s.wins / battles
                                    val survivalRate = s.wins / battles
                                    val surviedWins = s.survivedWins / battles
                                    if (maxWinRate < winRate) {
                                        maxWinRate = winRate
                                        maxWinRateShipId = s.shipId
                                    }
                                    if (maxSurvivalRate < survivalRate) {
                                        maxSurvivalRate = survivalRate
                                        maxSurvivalRateShipId = s.shipId
                                    }
                                    if (maxSurvivedWins < surviedWins) {
                                        maxSurvivedWins = surviedWins
                                        maxTotalSurWinsShipId = s.shipId
                                    }
                                    //Calculate per ship number
                                    val carating = s.caRating
                                    if (maxCARating < carating) {
                                        maxCARating = carating
                                        maxCARatingShipId = s.shipId
                                    }
                                }
                                numOfShips++
                                captain.ships.add(s)
                            }
                        }

                        val details = captain.details
                        if (details != null && numOfShips > 0 && details.battles > 0) {
                            details.maxSurvivalRate = maxSurvivalRate
                            details.maxSurvivalRateShipId = maxSurvivalRateShipId

                            details.maxWinRate = maxWinRate
                            details.maxWinRateShipId = maxWinRateShipId

                            details.maxTotalKills = maxTotalKills
                            details.maxTotalKillsShipId = maxTotalKillsShipId

                            details.maxPlayed = maxPlayed
                            details.maxPlayedShipId = maxPlayedShipId

                            details.maxCARating = maxCARating
                            details.maxCARatingShipId = maxCARatingShipId

                            details.maxMostTraveled = maxMostTraveled
                            details.maxMostTraveledShipId = maxMostTraveledShipId

                            details.maxAvgDmg = maxAvgDmg
                            details.maxAvgDmgShipId = maxAvgDmgShipId

                            details.maxTotalPlanes = maxTotalPlanes
                            details.maxTotalPlanesShipId = maxTotalPlanesShipId

                            details.maxTotalExp = maxTotalExp
                            details.maxTotalExpShipId = maxTotalExpShipId

                            details.maxTotalDamage = maxTotalDamage
                            details.maxTotalDmgShipId = maxTotalDmgShipId

                            details.maxSurvivedWins = maxSurvivedWins
                            details.maxSurvivedWinsShipId = maxTotalSurWinsShipId

                            details.maxTBAccuracy = maxTBAccuracy
                            details.maxTBAccuracyShipId = maxTBAccuracyShipId

                            details.maxMBAccuracy = maxMBAccuracy
                            details.maxMBAccuracyShipId = maxMBAccuracyShipId

                            //                            float maxSpotted = 0;
                            details.maxSpotted = maxSpotted
                            details.maxSpottedShipId = maxSpottedShipId
                            //                            float maxDamageScouting = 0;
                            details.maxDamageScouting = maxDamageScouting
                            details.maxDamageScoutingShipId = maxDamageScoutingShipId
                            //                            float maxArgo = 0;
                            details.maxTotalArgo = maxArgo
                            details.maxArgoDamageShipId = maxArgoShipId

                            //                            float maxTorpArgo = 0;
                            d("GetCaptain", "torp = $maxTorpArgo id = $maxTorpArgoShipId")
                            details.maxTorpTotalArgo = maxTorpArgo
                            details.maxTorpArgoDamageShipId = maxTorpArgoShipId
                            //                            float maxSuppressionCount = 0;
                            details.setMaxSuppressionCount(maxSuppressionCount)
                            details.setMaxSuppressionShipId(maxSuppressionCountShipId)

                            val battles = details.battles.toFloat()
                            details.setcDamage((details.totalDamage / battles).toFloat())
                            details.setcWinRate(((details.wins / battles)))
                            details.setcCaptures(((details.capturePoints / battles)))
                            details.setcDefReset(((details.droppedCapturePoints / battles)))
                            details.setcKills(((details.frags / battles)))
                            details.setcPlanes(((details.planesKilled / battles)))

                            //                            details.setcXP(((details.getTotalXP() / battles)));
//                            details.setcSurvival(((details.getSurvivedBattles() / battles)));
//                            details.setcSurWins(((details.getSurvivedWins() / battles)));
                            details.expectedDamage = eDamage / numOfShips
                            details.expectedKills = eKills / numOfShips
                            details.expectedPlanes = ePlanes / numOfShips
                            details.expectedWinRate = eWinRate / numOfShips

                            //                            details.setExpectedSurvival(eSurvial / total);
//                            details.setExpectedSurWins(eSurWins / total);
//                            details.setExpectedXP(eXP / total);

                            // average out the ratings with shipspertier. Ratingspertier / ships per tier.
                            // take every battle per tier and find ratio of games
                            // take ratio per tier of games and average rating per and times them.
                            var caRating = 0f
                            for (i in 1..shipsPerTier.size()) {
                                val ratingTotal = ratingsPerTier[i]
                                val shipsTotal = shipsPerTier[i]
                                val battlesTotal = battlePerTier[i]

                                if (ratingTotal != null && shipsTotal != null && battlesTotal != null && battlesTotal > 0) {
                                    // avgRating = total rating per tier / ship per tier
                                    val tierRatingAverage = ratingTotal / shipsTotal
                                    // percentageRatio = total battles per tier / total games
                                    val tierRatio = battlesTotal / battles

                                    //                                Dlog.d("CARating", tierRatingAverage + " ratio = " + tierRatio + " tier = " + i + " ratio = " + (tierRatingAverage * tierRatio));
                                    caRating += tierRatingAverage * tierRatio
                                }
                            }

                            // rating += avgRating per tier * percentageRatio of games for tier
                            val averageTier = tiers / battles
                            details.averageTier = averageTier

                            //                            Dlog.wtf("Captain Rating", "rating = " + caRating);
                            details.caRating = caRating
                        }
                        Collections.sort(captain.ships, ShipCompare().battlesComparator)
                    }
                }
                grabAchievements(query, achievementsFeed, captain)
            }
        }
    }

    private fun grabRankedShipsInfo(
        query: CaptainQuery,
        rankedShipsFeed: String?,
        captain: Captain
    ) {
        if (!TextUtils.isEmpty(rankedShipsFeed)) {
            var rankedShipsResult: JSONObject? = null
            try {
                rankedShipsResult = JSONObject(rankedShipsFeed)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (rankedShipsResult != null) {
                val rankedShipData = rankedShipsResult.optJSONObject(DATA)
                val playerRankedShip = rankedShipData.optJSONArray(query.id.toString() + "")
                val seasonMap: MutableMap<Long, List<SeasonInfo>> = HashMap()
                val shipsMap = infoManager!!.getShipInfo(ctx!!)
                if (playerRankedShip != null) {
                    for (i in 0 until playerRankedShip.length()) {
                        val obj = playerRankedShip.optJSONObject(i)
                        val seasons = obj.optJSONObject("seasons")
                        if (seasons.length() > 0) {
                            val shipId = obj.optLong("ship_id")
                            val seasonList: MutableList<SeasonInfo> = ArrayList()
                            val itea = seasons.keys()
                            while (itea.hasNext()) {
                                val key = itea.next()
                                val seasonObj = seasons.optJSONObject(key)
                                val sInfo: SeasonInfo = RankedInfo.parse(seasonObj)
                                sInfo.seasonNum = key
                                try {
                                    val shipName = shipsMap[shipId]!!.name
                                } catch (e: Exception) {
                                }
                                seasonList.add(sInfo)
                            }
                            seasonMap[shipId] = seasonList
                        }
                    }

                    if (captain.ships != null) {
                        for (s in captain.ships) {
                            val info = seasonMap[s.shipId]
                            if (info != null) {
                                s.rankedInfo = info
                            }
                        }
                    }
                }
            }
        }
    }

    private fun grabRankedInfo(query: CaptainQuery, rankedAccountFeed: String?, captain: Captain) {
        if (!TextUtils.isEmpty(rankedAccountFeed)) {
            var rankedAccountResult: JSONObject? = null
            try {
                rankedAccountResult = JSONObject(rankedAccountFeed)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (rankedAccountResult != null) {
                val rankedData = rankedAccountResult.optJSONObject(DATA)
                val playerRanked = rankedData.optJSONObject(query.id.toString() + "")
                if (playerRanked != null) {
                    val seasons = playerRanked.optJSONObject("seasons")
                    val iter = seasons.keys()
                    captain.rankedSeasons = ArrayList()
                    while (iter.hasNext()) {
                        val key = iter.next()
                        val season = seasons.optJSONObject(key)
                        val seasonInfo = RankedInfo.parse(season)
                        seasonInfo.seasonNum = key
                        try {
                            val seasonKey = key.toInt()
                            seasonInfo.seasonInt = seasonKey
                        } catch (e: NumberFormatException) {
                        }
                        captain.rankedSeasons.add(seasonInfo)
                    }
                }
            }
        }
    }

    private fun grabAchievements(query: CaptainQuery, achievementsFeed: String?, captain: Captain) {
        if (!TextUtils.isEmpty(achievementsFeed)) {
            var achievementResult: JSONObject? = null
            try {
                achievementResult = JSONObject(achievementsFeed)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (achievementResult != null) {
                val acheievementData = achievementResult.optJSONObject(DATA)
                if (acheievementData != null) {
                    val achievementsUser = acheievementData.optJSONObject("" + query.id)
                    if (achievementsUser != null) {
                        val battleAchievements = achievementsUser.optJSONObject("battle")
                        val progressAchievements = achievementsUser.optJSONObject("progress")
                        captain.achievements = ArrayList()
                        val iter = battleAchievements.keys()
                        while (iter.hasNext()) {
                            val key = iter.next()
                            AddAchievement(captain, battleAchievements, key)
                        }

                        val iter2 = progressAchievements.keys()
                        while (iter2.hasNext()) {
                            val key = iter2.next()
                            AddAchievement(captain, progressAchievements, key)
                        }
                    }
                }
            }
        }
    }

    private fun AddAchievement(captain: Captain, battleAchievements: JSONObject, key: String) {
        val number = battleAchievements.optInt(key)
        val achi = Achievement()
        achi.name = key
        achi.number = number
        captain.achievements.add(achi)
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


    override fun onPostExecute(captainResult: CaptainResult) {
        super.onPostExecute(captainResult)
        eventBus.post(captainResult)
    }

    companion object {
        private const val DATA = "data"

        fun addTierNumber(array: SparseArray<Float>, key: Int, value: Float) {
            var cKill = array[key]
            if (cKill == null) {
                cKill = value
            } else {
                cKill += value
            }
            array.put(key, cKill)
        }
    }
}
