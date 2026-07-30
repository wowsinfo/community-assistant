package com.half.wowsca.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import com.half.wowsca.CAApp
import com.half.wowsca.ui.encyclopedia.EncyclopediaViewModel
import com.half.wowsca.ui.settings.SettingsScreen
import com.half.wowsca.ui.theme.AppTheme

@AndroidEntryPoint
class SettingActivity : CABaseActivity() {

    private val encyclopediaViewModel: EncyclopediaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                SettingsScreen(
                    encyclopediaViewModel = encyclopediaViewModel,
                    onRefreshData = {
                        val server = CAApp.getServerType(applicationContext)
                        encyclopediaViewModel.loadEncyclopedia(applicationContext, server)
                    },
                    onAboutClick = { /* TODO */ }
                )
            }
        }
    }
}
