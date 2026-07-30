package com.half.wowsca.data.repository

import com.half.wowsca.data.api.ServerInfoApiService
import com.half.wowsca.model.ServerInfo
import com.half.wowsca.model.enums.Server
import com.half.wowsca.model.result.ServerResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerInfoRepository @Inject constructor(
    private val api: ServerInfoApiService,
) {
    /**
     * Fetches server info (online players) for all regions.
     */
    suspend fun getServerInfo(): Result<ServerResult> {
        return try {
            val result = ServerResult()
            for (server in Server.entries) {
                val response = api.getServerInfo(applicationId = server.appId)
                if (response.status == "ok" && response.data != null) {
                    response.data.wot?.forEach { dto ->
                        ServerInfo().apply {
                            name = dto.server
                            players = dto.players_online ?: 0
                            this.server = server
                        }.let { result.wotNumbers.add(it) }
                    }
                    response.data.wotb?.forEach { dto ->
                        ServerInfo().apply {
                            name = dto.server
                            players = dto.players_online ?: 0
                            this.server = server
                        }.let { result.wowsNumbers.add(it) }
                    }
                }
            }
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
