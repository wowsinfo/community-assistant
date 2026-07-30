package com.half.wowsca.data.api

import com.half.wowsca.model.Captain
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Retrofit API service for Wargaming.net public APIs.
 */
interface WargamingApiService {

    @GET("/wows/encyclopedia/ships/")
    suspend fun getShips(
        @Query("application_id") applicationId: String,
        @Query("language") language: String = "en",
    ): WargamingResponse<Map<String, Any>>

    @GET("/wows/encyclopedia/achievements/")
    suspend fun getAchievements(
        @Query("application_id") applicationId: String,
        @Query("language") language: String = "en",
    ): WargamingResponse<Map<String, Any>>

    @GET("/wows/encyclopedia/consumables/")
    suspend fun getConsumables(
        @Query("application_id") applicationId: String,
        @Query("type") type: String,
        @Query("language") language: String = "en",
    ): WargamingResponse<Map<String, Any>>

    @GET("/wows/encyclopedia/crewskills/")
    suspend fun getCrewSkills(
        @Query("application_id") applicationId: String,
        @Query("language") language: String = "en",
    ): WargamingResponse<Map<String, Any>>

    @GET("/wows/encyclopedia/shipprofile/")
    suspend fun getShipProfile(
        @Query("application_id") applicationId: String,
        @Query("ship_id") shipId: Long,
        @Query("language") language: String = "en",
    ): WargamingResponse<Map<String, ShipProfileDto>>

    @GET("/wows/account/list/")
    suspend fun searchPlayers(
        @Query("application_id") applicationId: String,
        @Query("search") search: String,
    ): WargamingResponse<List<CaptainDto>>
}

data class CaptainDto(
    val account_id: Long,
    val nickname: String,
)

data class WargamingResponse<T>(
    val status: String,
    val data: T?,
    val error: WargamingError?,
)

data class ShipProfileDto(
    val ship_id: Long?,
    val modules: Map<String, Long>?,
)

data class WargamingError(
    val code: Int,
    val message: String,
)
