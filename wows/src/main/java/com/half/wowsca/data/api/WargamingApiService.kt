package com.half.wowsca.data.api

import com.half.wowsca.model.Captain
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API service for Wargaming.net public APIs.
 */
interface WargamingApiService {

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

data class WargamingError(
    val code: Int,
    val message: String,
)
