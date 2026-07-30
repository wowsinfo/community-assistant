package com.half.wowsca.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.half.wowsca.ui.main.MainScreen
import com.half.wowsca.ui.theme.AppTheme
import com.half.wowsca.ui.viewcaptain.ViewCaptainActivity

@AndroidEntryPoint
class MainActivity : CABaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MainScreen(
                    context = applicationContext,
                    onSearchClick = {
                        startActivity(Intent(this, SearchActivity::class.java))
                    },
                    onSettingsClick = {
                        startActivity(Intent(this, SettingActivity::class.java))
                    },
                    onResourcesClick = {
                        val i = Intent(this, ResourcesActivity::class.java)
                        startActivity(i)
                    },
                    onCaptainClick = { captain ->
                        val i = Intent(this, ViewCaptainActivity::class.java)
                        startActivity(i)
                    }
                )
            }
        }
    }
}
