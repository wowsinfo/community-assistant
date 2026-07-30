package com.half.wowsca.ui.encyclopedia

import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.half.wowsca.ui.CABaseActivity
import com.half.wowsca.ui.theme.AppTheme

@AndroidEntryPoint
class ShipProfileActivity : CABaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val shipId = intent.getLongExtra(SHIP_ID, 0L)

        setContent {
            AppTheme {
                ShipProfileScreen(
                    context = applicationContext,
                    shipId = shipId,
                    onBack = { finish() }
                )
            }
        }
    }

    companion object {
        const val SHIP_ID: String = "shipid"
    }
}
