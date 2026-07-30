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
                    context = applicationContext
                )
            }
        }
    }
}
