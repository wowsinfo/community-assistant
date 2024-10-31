package com.half.wowsca.backend

import android.os.AsyncTask
import android.text.TextUtils
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.model.ServerInfo
import com.half.wowsca.model.enums.Server
import com.half.wowsca.model.result.ServerResult
import com.utilities.Utils.getInputStreamResponse
import com.utilities.logging.Dlog.wtf
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

/**
 * Created by slai4 on 12/2/2015.
 */
class GetServerInfo : AsyncTask<String?, Void?, ServerResult>() {
    override fun doInBackground(vararg params: String?): ServerResult {
        val result = ServerResult()
        val startUrl = "https://api.worldoftanks"
        val url =
            startUrl + Server.NA.suffix + "/wgn/servers/info/?application_id=" + Server.NA.appId
        wtf("server URL", url)

        val url2 =
            startUrl + Server.EU.suffix + "/wgn/servers/info/?application_id=" + Server.EU.appId
        wtf("server URL", url2)


        val url4 =
            startUrl + Server.SEA.suffix + "/wgn/servers/info/?application_id=" + Server.SEA.appId
        wtf("server URL", url4)

        val naFeed = getURLResult(url)
        val euFeed = getURLResult(url2)
        val seaFeed = getURLResult(url4)

        if (!TextUtils.isEmpty(naFeed)) parseRegion(naFeed, result, Server.NA)
        if (!TextUtils.isEmpty(euFeed)) parseRegion(euFeed, result, Server.EU)
        if (!TextUtils.isEmpty(seaFeed)) parseRegion(seaFeed, result, Server.SEA)

        return result
    }

    override fun onPostExecute(server: ServerResult) {
        super.onPostExecute(server)
        eventBus.post(server)
    }

    private fun parseRegion(feed: String?, result: ServerResult, s: Server) {
        var res: JSONObject? = null
        try {
            res = JSONObject(feed)
        } catch (e: JSONException) {
        }

        if (res != null) {
            val data = res.optJSONObject("data")
            if (data != null) {
                val wot = data.optJSONArray("wot")
                val wows = data.optJSONArray("wotb")
                if (wot != null) {
                    for (i in 0 until wot.length()) {
                        val obj = wot.optJSONObject(i)
                        val info = ServerInfo()
                        info.name = obj.optString("server")
                        info.players = obj.optInt("players_online")
                        info.server = s
                        result.wotNumbers.add(info)
                    }
                }
                if (wows != null) {
                    for (i in 0 until wows.length()) {
                        val obj = wows.optJSONObject(i)
                        val info = ServerInfo()
                        info.name = obj.optString("server")
                        info.players = obj.optInt("players_online")
                        info.server = s
                        result.wowsNumbers.add(info)
                    }
                }
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
}
