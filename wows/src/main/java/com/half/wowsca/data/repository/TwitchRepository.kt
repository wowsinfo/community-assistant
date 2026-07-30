package com.half.wowsca.data.repository

import com.half.wowsca.model.enums.TwitchStatus
import com.half.wowsca.model.TwitchObj
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TwitchRepository @Inject constructor(
    private val okHttpClient: OkHttpClient,
) {
    /**
     * Fetches Twitch stream info for a given channel name.
     * Uses raw OkHttp + JSONObject for compatibility with the existing model.
     * TODO: Replace with Retrofit/Ktor + kotlinx.serialization when migrating to Helix API.
     */
    suspend fun getTwitchInfo(channelName: String): TwitchObj {
        val obj = TwitchObj(channelName)

        try {
            // Fetch stream info
            val streamUrl = "https://api.twitch.tv/kraken/streams/$channelName"
            val streamRequest = Request.Builder()
                .url(streamUrl)
                .header("Accept", "application/vnd.twitchtv.v5+json")
                .build()
            val streamResponse = okHttpClient.newCall(streamRequest).execute()
            val streamBody = streamResponse.body?.string()
            if (streamBody != null) {
                val streamJson = JSONObject(streamBody)
                val stream = streamJson.optJSONObject("stream")
                obj.isLive = if (stream != null) TwitchStatus.LIVE else TwitchStatus.OFFLINE
                if (stream != null) {
                    obj.gamePlaying = stream.optString("game")
                    /* viewers not in TwitchObj */
                }
            }

            // Fetch channel info for status
            val channelUrl = "https://api.twitch.tv/kraken/channels/$channelName"
            val channelRequest = Request.Builder()
                .url(channelUrl)
                .header("Accept", "application/vnd.twitchtv.v5+json")
                .build()
            val channelResponse = okHttpClient.newCall(channelRequest).execute()
            val channelBody = channelResponse.body?.string()
            if (channelBody != null) {
                val channelJson = JSONObject(channelBody)
                obj.streamName = channelJson.optString("status")
                obj.gamePlaying = channelJson.optString("game")
            }
        } catch (_: Exception) {
            // Twitch API may be unavailable; return obj with default values
        }

        return obj
    }
}
