package com.half.wowsca.ui.viewcaptain

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import com.half.wowsca.backend.GetCaptainTask
import com.half.wowsca.model.queries.CaptainQuery
import com.half.wowsca.managers.CaptainManager
import com.half.wowsca.model.AuthInfo
import com.half.wowsca.model.Captain
import com.half.wowsca.model.enums.Server
import com.half.wowsca.ui.CABaseActivity
import com.half.wowsca.ui.theme.AppTheme

@AndroidEntryPoint
class ViewCaptainActivity : CABaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val id = intent.getLongExtra(EXTRA_ID, 0L)
        val serverName = intent.getStringExtra(EXTRA_SERVER)
        val server = serverName?.let { Server.valueOf(it) }
        val captainName = intent.getStringExtra(EXTRA_NAME)
        val mutableCaptain = androidx.compose.runtime.mutableStateOf<Captain?>(null)

        // Try to get captain from local storage first
        val initialCaptain = server?.let { s ->
            var cap = if (CaptainManager.fromSearch(applicationContext, s, id)) {
                CaptainManager.getTEMP(applicationContext)
            } else null
            if (cap == null || cap.ships == null) {
                val idStr = CaptainManager.createCapIdStr(s, id)
                cap = CaptainManager.getCaptains(applicationContext)?.get(idStr)
            }
            cap
        }
        mutableCaptain.value = initialCaptain

        // If captain has no ships data, fetch full profile from API
        if (initialCaptain == null || initialCaptain.ships == null) {
            val query = CaptainQuery()
            query.id = id
            query.name = captainName ?: initialCaptain?.name ?: ""
            if (server != null) query.server = server

            val task = GetCaptainTask()
            task.ctx = applicationContext
            task.onResult = { result ->
                // Use the fully loaded captain from API result
                if (result.captain != null) {
                    mutableCaptain.value = result.captain
                }
            }
            task.execute(query)
        }

        setContent {
            val captain by mutableCaptain
            AppTheme {
                if (captain == null && initialCaptain == null) {
                    androidx.compose.foundation.layout.Box(
                        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator()
                        androidx.compose.foundation.layout.Spacer(
                            modifier = androidx.compose.ui.Modifier.height(16.dp)
                        )
                        androidx.compose.material3.Text("Loading captain data...")
                    }
                } else {
                    ViewCaptainScreen(
                        captain = captain ?: initialCaptain,
                        onBack = { finish() }
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_ID: String = "captainId"
        const val EXTRA_NAME: String = "name"
        const val EXTRA_SERVER: String = "server"
    }
}
