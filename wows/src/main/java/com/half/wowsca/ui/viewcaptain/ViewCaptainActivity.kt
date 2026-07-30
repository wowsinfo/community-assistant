package com.half.wowsca.ui.viewcaptain

import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.half.wowsca.managers.CaptainManager
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
        val captain = server?.let { s ->
            // Check temp storage first (from search result)
            var cap = if (CaptainManager.fromSearch(applicationContext, s, id)) {
                CaptainManager.getTEMP(applicationContext)
            } else null
            // Fall back to saved captains
            if (cap == null) {
                val idStr = CaptainManager.createCapIdStr(s, id)
                cap = CaptainManager.getCaptains(applicationContext)?.get(idStr)
            }
            cap
        }

        setContent {
            AppTheme {
                ViewCaptainScreen(
                    captain = captain,
                    onBack = { finish() }
                )
            }
        }
    }

    companion object {
        const val EXTRA_ID: String = "captainId"
        const val EXTRA_SERVER: String = "server"
    }
}
