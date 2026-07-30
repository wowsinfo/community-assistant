package com.half.wowsca.data.api

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API service for the Wargaming server info endpoint (uses worldoftanks base).
 * Returns online player counts per server for both WoT and WoWS.
 */
interface ServerInfoApiService {

    @GET("/wgn/servers/info/")
    suspend fun getServerInfo(
        @Query("application_id") applicationId: String,
    ): WargamingResponse<ServerInfoData>
}

data class ServerInfoData(
    val wot: List<ServerInfoDto>?,
    val wotb: List<ServerInfoDto>?,
)

data class ServerInfoDto(
    val server: String?,
    val players_online: Int?,
)
