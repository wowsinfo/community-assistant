package com.half.wowsca.backend

import android.os.AsyncTask
import android.text.TextUtils
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.model.TwitchObj
import com.half.wowsca.model.enums.TwitchStatus
import com.utilities.Utils.getInputStreamResponse
import com.utilities.logging.Dlog.wtf
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

/**
 * Created by slai4 on 12/3/2015.
 */
class GetTwitchInfo : AsyncTask<String?, Void?, TwitchObj>() {
    protected override fun doInBackground(vararg params: String?): TwitchObj? {
        val obj = TwitchObj(params[0] ?: return null)

        val url = "https://api.twitch.tv/kraken/streams/" + params[0]
        val url2 = "https://api.twitch.tv/kraken/channels/" + params[0]
        wtf("Stream URL", url)

        val feed = getURLResult(url)

        if (!TextUtils.isEmpty(feed)) {
            var result: JSONObject? = null
            try {
                result = JSONObject(feed)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (result != null) {
                val stream = result.optJSONObject("stream")
                if (stream != null) {
                    obj.isLive = TwitchStatus.LIVE
                    obj.gamePlaying = stream.optString("game")

                    val preview = stream.optJSONObject("preview")
                    obj.thumbnail = preview.optString("large")

                    val channel = stream.optJSONObject("channel")
                    obj.logo = channel.optString("logo")
                    obj.streamName = channel.optString("status")
                } else {
                    obj.isLive = TwitchStatus.OFFLINE
                    val channelInfoFeed = getURLResult(url2)
                    wtf("Channel URL", url2)
                    var channelResult: JSONObject? = null
                    try {
                        channelResult = JSONObject(channelInfoFeed)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    if (channelInfoFeed != null) {
                        obj.thumbnail = channelResult!!.optString("video_banner")
                        obj.logo = channelResult.optString("logo")
                    } else {
                    }
                }
            } else {
                obj.isLive = TwitchStatus.OFFLINE
            }
        }
        return obj
    }


    override fun onPostExecute(twitchObj: TwitchObj) {
        super.onPostExecute(twitchObj)
        eventBus.post(twitchObj)
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
