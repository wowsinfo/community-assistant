package com.half.wowsca.data.repository

import com.half.wowsca.data.api.WargamingApiService
import com.half.wowsca.model.enums.Server
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShipEncyclopediaRepository @Inject constructor(
    private val api: WargamingApiService,
) {
    /**
     * Fetches ship profile data (modules, stats) from the encyclopedia.
     * Returns the raw JSON string for compatibility with existing [com.half.wowsca.model.ShipInformation] parsing.
     */
    suspend fun getShipProfile(shipId: Long, server: Server, language: String = "en"): Result<String> {
        return try {
            val baseUrl = "https://api.worldofwarships${server.suffix}"
            val response = api.getShipProfile(
                url = "$baseUrl/wows/encyclopedia/shipprofile/",
                applicationId = server.appId,
                shipId = shipId,
                language = language,
            )
            if (response.status == "ok" && response.data != null) {
                // Return the raw JSON for existing ShipInformation parser compatibility
                val dataJson = JSONObject()
                response.data.forEach { (key, profile) ->
                    val profileJson = JSONObject()
                    profile.modules?.let { modules ->
                        val modulesJson = JSONObject()
                        modules.forEach { (k, v) -> modulesJson.put(k, v) }
                        profileJson.put("modules", modulesJson)
                    }
                    profileJson.put("ship_id", profile.ship_id ?: shipId)
                    dataJson.put(key, profileJson)
                }
                Result.success(dataJson.toString())
            } else {
                Result.failure(Exception(response.error?.message ?: "Failed to fetch ship profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
