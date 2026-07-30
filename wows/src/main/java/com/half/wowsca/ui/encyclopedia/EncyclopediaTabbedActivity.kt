package com.half.wowsca.ui.encyclopedia

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.half.wowsca.ui.CABaseActivity
import com.half.wowsca.ui.theme.AppTheme

@AndroidEntryPoint
class EncyclopediaTabbedActivity : CABaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                EncyclopediaScreen(
                    context = applicationContext,
                    onShipClick = { ship ->
                        val i = Intent(this, ShipProfileActivity::class.java)
                        i.putExtra(SHIP_ID, ship.shipId)
                        startActivity(i)
                    },
                    onCompareClick = { /* TODO */ }
                )
            }
        }
    }

    companion object {
        const val SHIP_ID: String = "shipid"
    }
}
