package com.half.wowsca.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import com.half.wowsca.CAApp
import com.half.wowsca.model.enums.Server
import com.half.wowsca.ui.encyclopedia.EncyclopediaViewModel
import com.half.wowsca.ui.splash.SplashScreen
import com.half.wowsca.ui.theme.AppTheme

@AndroidEntryPoint
class SplashActivity : CABaseActivity() {

    private val encyclopediaViewModel: EncyclopediaViewModel by viewModels()
    private var goToNext = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                SplashScreen(
                    encyclopediaViewModel = encyclopediaViewModel,
                    onContinue = { goToNext() }
                )
            }
        }
        encyclopediaViewModel.loadEncyclopedia(applicationContext, CAApp.getServerType(applicationContext))
    }

    private fun goToNext() {
        if (!goToNext) {
            goToNext = true
            val i = android.content.Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}
