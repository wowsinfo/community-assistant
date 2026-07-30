package com.half.wowsca.data.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

/**
 * Retrofit API service for Twitch stream info.
 * Uses the legacy Kraken API (v5) for compatibility with existing models.
 * TODO: Migrate to Helix API when Twitch authentication is implemented.
 */
interface TwitchApiService {

    @GET("https://api.twitch.tv/kraken/streams/{channel}")
    suspend fun getStreamInfo(
        @Path("channel") channel: String,
        @Header("Accept") accept: String = "application/vnd.twitchtv.v5+json",
    ): TwitchStreamResponse

    @GET("https://api.twitch.tv/kraken/channels/{channel}")
    suspend fun getChannelInfo(
        @Path("channel") channel: String,
        @Header("Accept") accept: String = "application/vnd.twitchtv.v5+json",
    ): TwitchChannelResponse
}

data class TwitchStreamResponse(
    val stream: Any?,
)

data class TwitchChannelResponse(
    val status: String?,
    val game: String?,
)
