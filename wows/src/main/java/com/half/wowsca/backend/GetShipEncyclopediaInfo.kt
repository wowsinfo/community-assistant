package com.half.wowsca.backend

import android.os.AsyncTask
import android.text.TextUtils
import com.half.wowsca.CAApp
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.model.queries.ShipQuery
import com.half.wowsca.model.result.ShipResult
import com.utilities.Utils.getInputStreamResponse
import com.utilities.logging.Dlog.wtf
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

/**
 * Created by slai4 on 11/1/2015.
 */
class GetShipEncyclopediaInfo : AsyncTask<ShipQuery?, Void?, ShipResult>() {
    override fun doInBackground(vararg params: ShipQuery?): ShipResult? {
        val query = params[0] ?: return null
        val result = ShipResult()

        val sb = StringBuilder()
        sb.append(CAApp.WOWS_API_SITE_ADDRESS)
        sb.append(query.server.suffix)
        sb.append("/wows/encyclopedia/shipprofile/")
        sb.append("?application_id=" + query.server.appId)
        sb.append("&ship_id=" + query.shipId)
        sb.append("&language=" + query.language)
        if (query.modules != null) {
            // artillery id
            val artillery = query.modules[ARTILLERY]
            if (artillery != null && artillery != 0L) {
                sb.append("&artillery_id=$artillery")
            }
            //torpedoes id
            val torps = query.modules[TORPEDOES]
            if (torps != null && torps != 0L) {
                sb.append("&torpedoes_id=$torps")
            }
            //fire control id
            val fireControl = query.modules[FIRE_CONTROL]
            if (fireControl != null && fireControl != 0L) {
                sb.append("&fire_control_id=$fireControl")
            }
            //flight control id
            val flightControl = query.modules[FLIGHT_CONTROL]
            if (flightControl != null && flightControl != 0L) {
                sb.append("&flight_control_id=$flightControl")
            }
            //hull id
            val hull = query.modules[HULL]
            if (hull != null && hull != 0L) {
                sb.append("&hull_id=$hull")
            }
            //engine id
            val engine = query.modules[ENGINE]
            if (engine != null && engine != 0L) {
                sb.append("&engine_id=$engine")
            }
            //fighter id
            val fighter = query.modules[FIGHTER]
            if (fighter != null && fighter != 0L) {
                sb.append("&fighter_id=$fighter")
            }
            //dive bomber id
            val diveBomber = query.modules[DIVE_BOMBER]
            if (diveBomber != null && diveBomber != 0L) {
                sb.append("&dive_bomber_id=$diveBomber")
            }
            //torpedo bomber id
            val torpBomber = query.modules[TORPEDO_BOMBER]
            if (torpBomber != null && torpBomber != 0L) {
                sb.append("&torpedo_bomber_id=$torpBomber")
            }
        }
        val url = sb.toString()
        wtf("SHIPINFO URL", url)

        val shipFeed = getURLResult(url)
        result.shipId = query.shipId
        //parse this
        if (!TextUtils.isEmpty(shipFeed)) {
            var feed: JSONObject? = null
            try {
                feed = JSONObject(shipFeed)
            } catch (e: JSONException) {
            }
            if (feed != null) {
                val data = feed.optJSONObject("data")
                if (data != null) {
                    val ship = data.optJSONObject(query.shipId.toString() + "")
                    if (ship != null) result.shipInfo = ship.toString()
                }
            }
        }
        return result
    }


    override fun onPostExecute(shipResult: ShipResult) {
        super.onPostExecute(shipResult)
        eventBus.post(shipResult)
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

    companion object {
        const val ARTILLERY: String = "artillery"
        const val TORPEDOES: String = "torpedoes"
        const val FIRE_CONTROL: String = "fire_control"
        const val FLIGHT_CONTROL: String = "flight_control"
        const val HULL: String = "hull"
        const val ENGINE: String = "engine"
        const val FIGHTER: String = "fighter"
        const val DIVE_BOMBER: String = "dive_bomber"
        const val TORPEDO_BOMBER: String = "torpedo_bomber"
    }
}
