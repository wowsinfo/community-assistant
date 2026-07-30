package com.half.wowsca.data.repository

import com.half.wowsca.data.api.WargamingApiService
import com.half.wowsca.data.api.WargamingResponse
import com.half.wowsca.model.enums.Server
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncyclopediaRepository @Inject constructor(
    private val api: WargamingApiService,
) {
    suspend fun getShips(server: Server, language: String = "en"): Result<String> =
        fetchRaw { api.getShips(applicationId = server.appId, language = language) }

    suspend fun getAchievements(server: Server, language: String = "en"): Result<String> =
        fetchRaw { api.getAchievements(applicationId = server.appId, language = language) }

    suspend fun getUpgrades(server: Server, language: String = "en"): Result<String> =
        fetchRaw { api.getConsumables(applicationId = server.appId, type = "Modernization", language = language) }

    suspend fun getCrewSkills(server: Server, language: String = "en"): Result<String> =
        fetchRaw { api.getCrewSkills(applicationId = server.appId, language = language) }

    suspend fun getFlags(server: Server, language: String = "en"): Result<String> =
        fetchRaw { api.getConsumables(applicationId = server.appId, type = "Flags", language = language) }

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
                Result.success(wrapper.toString())
            } else {
                Result.failure(Exception(response.error?.message ?: "Encyclopedia fetch failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
