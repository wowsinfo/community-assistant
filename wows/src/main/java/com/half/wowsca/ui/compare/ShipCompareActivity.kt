package com.half.wowsca.ui.compare

import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.half.wowsca.ui.CABaseActivity
import com.half.wowsca.ui.theme.AppTheme

@AndroidEntryPoint
class ShipCompareActivity : CABaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                ShipCompareScreen(
                    onBack = { finish() }
                )
            }
        }
    }
}
