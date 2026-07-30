package com.half.wowsca.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Retrofit API service for Wargaming.net public APIs.
 * All endpoints use @Url to support server-specific domains (.com, .eu, .asia).
 */
interface WargamingApiService {

    @GET
    suspend fun searchPlayers(
        @Url url: String,
        @Query("application_id") applicationId: String,
        @Query("search") search: String,
    ): WargamingResponse<List<CaptainDto>>

    @GET
    suspend fun getAccountInfo(
        @Url url: String,
        @Query("application_id") applicationId: String,
        @Query("account_id") accountId: Long,
        @Query("extra") extra: String = "statistics.club,statistics.pve,statistics.pvp_div2,statistics.pvp_div3,statistics.pvp_solo",
    ): WargamingResponse<Map<String, Any>>

    @GET
    suspend fun getShipStats(
        @Url url: String,
        @Query("application_id") applicationId: String,
        @Query("account_id") accountId: Long,
        @Query("extra") extra: String = "club,pve,pvp_div2,pvp_div3,pvp_solo",
    ): WargamingResponse<Map<String, Any>>

    @GET
    suspend fun getAccountAchievements(
        @Url url: String,
        @Query("application_id") applicationId: String,
        @Query("account_id") accountId: Long,
    ): WargamingResponse<Map<String, Any>>

    @GET
    suspend fun getSeasonsAccountInfo(
        @Url url: String,
        @Query("application_id") applicationId: String,
        @Query("account_id") accountId: Long,
    ): WargamingResponse<Map<String, Any>>

    @GET
    suspend fun getShips(
        @Url url: String,
        @Query("application_id") applicationId: String,
        @Query("language") language: String = "en",
    ): WargamingResponse<Map<String, Any>>

    @GET
    suspend fun getAchievements(
        @Url url: String,
        @Query("application_id") applicationId: String,
        @Query("language") language: String = "en",
    ): WargamingResponse<Map<String, Any>>

    @GET
    suspend fun getConsumables(
        @Url url: String,
        @Query("application_id") applicationId: String,
        @Query("type") type: String,
        @Query("language") language: String = "en",
    ): WargamingResponse<Map<String, Any>>

    @GET
    suspend fun getCrewSkills(
        @Url url: String,
        @Query("application_id") applicationId: String,
        @Query("language") language: String = "en",
    ): WargamingResponse<Map<String, Any>>

    @GET
    suspend fun getShipProfile(
        @Url url: String,
        @Query("application_id") applicationId: String,
        @Query("ship_id") shipId: Long,
        @Query("language") language: String = "en",
    ): WargamingResponse<Map<String, ShipProfileDto>>
}

data class CaptainDto(
    val account_id: Long,
    val nickname: String,
)

data class ShipProfileDto(
    val ship_id: Long?,
    val modules: Map<String, Long>?,
)

data class WargamingResponse<T>(
    val status: String,
    val data: T?,
    val error: WargamingError?,
)

data class WargamingError(
    val code: Int,
    val message: String,
)
