package com.half.wowsca.data.repository

import com.half.wowsca.data.api.CaptainDto
import com.half.wowsca.data.api.WargamingApiService
import com.half.wowsca.model.Captain
import com.half.wowsca.model.enums.Server
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CaptainRepository @Inject constructor(
    private val api: WargamingApiService,
) {
    /**
     * Searches for players by name on the given server.
     */
    suspend fun searchPlayers(search: String, server: Server): Result<List<Captain>> {
        return try {
            val response = api.searchPlayers(
                applicationId = server.appId,
                search = search,
            )
            if (response.status == "ok" && response.data != null) {
                Result.success(response.data.map { it.toCaptain(server) })
            } else {
                Result.failure(Exception(response.error?.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun CaptainDto.toCaptain(server: Server): Captain {
        return Captain().apply {
            id = account_id
            name = nickname
            this.server = server
        }
    }
}
