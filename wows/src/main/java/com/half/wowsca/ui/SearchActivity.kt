package com.half.wowsca.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import com.half.wowsca.managers.CompareManager
import com.half.wowsca.model.Captain
import com.half.wowsca.ui.compare.CompareActivity
import com.half.wowsca.ui.search.SearchScreen
import com.half.wowsca.ui.search.SearchViewModel
import com.half.wowsca.ui.theme.AppTheme

@AndroidEntryPoint
class SearchActivity : CABaseActivity() {

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                SearchScreen(
                    viewModel = viewModel,
                    onCompareClick = {
                        if (CompareManager.size() > 1) {
                            startActivity(Intent(this, CompareActivity::class.java))
                        }
                    },
                    onCaptainClick = { captain ->
                        com.half.wowsca.managers.CaptainManager.saveTempStoredCaptain(this, captain)
                        val i = Intent(this, com.half.wowsca.ui.viewcaptain.ViewCaptainActivity::class.java)
                        i.putExtra(com.half.wowsca.ui.viewcaptain.ViewCaptainActivity.EXTRA_ID, captain.id)
                        i.putExtra(com.half.wowsca.ui.viewcaptain.ViewCaptainActivity.EXTRA_SERVER, captain.server?.name)
                        startActivity(i)
                    }
                )
            }
        }
    }
}
