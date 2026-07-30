package com.half.wowsca.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import com.half.wowsca.ui.resources.ResourcesScreen
import com.half.wowsca.ui.resources.ServerInfoViewModel
import com.half.wowsca.ui.resources.TwitchViewModel
import com.half.wowsca.ui.theme.AppTheme

@AndroidEntryPoint
class ResourcesActivity : CABaseActivity() {

    private val serverInfoViewModel: ServerInfoViewModel by viewModels()
    private val twitchViewModel: TwitchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                ResourcesScreen(
                    serverInfoViewModel = serverInfoViewModel,
                    twitchViewModel = twitchViewModel,
                    onUrlClick = { url ->
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    }
                )
            }
        }
    }
}
