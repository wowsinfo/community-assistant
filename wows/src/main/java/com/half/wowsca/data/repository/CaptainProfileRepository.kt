package com.half.wowsca.data.repository

import com.half.wowsca.data.api.WargamingApiService
import com.half.wowsca.data.api.WargamingResponse
import com.half.wowsca.model.enums.Server
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for fetching full captain profile data from the Wargaming API.
 * Returns raw JSON strings for compatibility with existing [com.half.wowsca.model.Captain] parsing.
 */
@Singleton
class CaptainProfileRepository @Inject constructor(
    private val api: WargamingApiService,
) {
    suspend fun getAccountInfo(accountId: Long, server: Server): Result<String> =
        fetchRaw { api.getAccountInfo(applicationId = server.appId, accountId = accountId) }

    suspend fun getShipStats(accountId: Long, server: Server): Result<String> =
        fetchRaw { api.getShipStats(applicationId = server.appId, accountId = accountId) }

    suspend fun getAchievements(accountId: Long, server: Server): Result<String> =
        fetchRaw { api.getAccountAchievements(applicationId = server.appId, accountId = accountId) }

    suspend fun getSeasons(accountId: Long, server: Server): Result<String> =
        fetchRaw { api.getSeasonsAccountInfo(applicationId = server.appId, accountId = accountId) }

    private suspend fun fetchRaw(fetch: suspend () -> WargamingResponse<Map<String, Any>>): Result<String> {
        return try {
            val response = fetch()
            if (response.status == "ok" && response.data != null) {
                val dataJson = JSONObject()
                response.data.forEach { (key, value) ->
                    dataJson.put(key, JSONObject.wrap(value))
                }
                val wrapper = JSONObject()
                wrapper.put("data", dataJson)
                Result.success(dataJson.toString())
            } else {
                Result.failure(Exception(response.error?.message ?: "Captain profile fetch failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
