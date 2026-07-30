package com.half.wowsca.data.repository

import com.half.wowsca.data.api.WargamingApiService
import com.half.wowsca.data.api.WargamingResponse
import com.half.wowsca.model.enums.Server
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CaptainProfileRepository @Inject constructor(
    private val api: WargamingApiService,
) {
    suspend fun getAccountInfo(accountId: Long, server: Server): Result<String> {
        val base = "https://api.worldofwarships${server.suffix}"
        return fetchRaw { api.getAccountInfo(url = "$base/wows/account/info/", applicationId = server.appId, accountId = accountId) }
    }

    suspend fun getShipStats(accountId: Long, server: Server): Result<String> {
        val base = "https://api.worldofwarships${server.suffix}"
        return fetchRaw { api.getShipStats(url = "$base/wows/ships/stats/", applicationId = server.appId, accountId = accountId) }
    }

    suspend fun getAchievements(accountId: Long, server: Server): Result<String> {
        val base = "https://api.worldofwarships${server.suffix}"
        return fetchRaw { api.getAccountAchievements(url = "$base/wows/account/achievements/", applicationId = server.appId, accountId = accountId) }
    }

    suspend fun getSeasons(accountId: Long, server: Server): Result<String> {
        val base = "https://api.worldofwarships${server.suffix}"
        return fetchRaw { api.getSeasonsAccountInfo(url = "$base/wows/seasons/accountinfo/", applicationId = server.appId, accountId = accountId) }
    }

    private suspend fun fetchRaw(fetch: suspend () -> WargamingResponse<Map<String, Any>>): Result<String> {
        return try {
            val response = fetch()
            if (response.status == "ok" && response.data != null) {
                val dataJson = JSONObject()
                response.data.forEach { (key, value) ->
                    dataJson.put(key, JSONObject.wrap(value))
                }
                Result.success(dataJson.toString())
            } else {
                Result.failure(Exception(response.error?.message ?: "Captain profile fetch failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
