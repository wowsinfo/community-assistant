package com.half.wowsca.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.ComposeView
import com.half.wowsca.CAApp
import com.half.wowsca.CAApp.Companion.getServerType
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.R
import com.half.wowsca.alerts.Alert.generalNoInternetDialogAlert
import com.half.wowsca.backend.GetNeededInfoTask
import com.half.wowsca.model.enums.ShortcutRoutes
import com.half.wowsca.model.queries.InfoQuery
import com.utilities.Utils.hasInternetConnection
import com.utilities.preferences.Prefs

/**
 * Created by slai4 on 10/31/2015.
 */
class SplashActivity : CABaseActivity() {
    private var callNext = false
    private var grabbingInfo = false
    private var goToNext = false
    private var isLoading = true
    private var showRetryButton = false
    private lateinit var composeView: ComposeView
    private val mainHandler = Handler(Looper.getMainLooper())
    private val infoPoll = object : Runnable {
        override fun run() {
            if (!grabbingInfo || isFinishing || isDestroyed) {
                return
            }
            val manager = infoManager
            if (manager?.isInfoThere(applicationContext) == true) {
                grabbingInfo = false
                isLoading = false
                showRetryButton = false
                render()
                goToNext()
                return
            }
            mainHandler.postDelayed(this, INFO_POLL_INTERVAL_MILLIS)
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        composeView = ComposeView(this)
        setContentView(composeView)
        if (savedInstanceState != null) {
            callNext = savedInstanceState.getBoolean(CALL_NEXT_DONE)
            grabbingInfo = savedInstanceState.getBoolean(GRABBING_INFO)
            isLoading = savedInstanceState.getBoolean(IS_LOADING, true)
            showRetryButton = savedInstanceState.getBoolean(SHOW_RETRY_BUTTON, false)
        }
        render()
        bindView()
        val action = intent?.action
        if (!action.isNullOrEmpty()) {
            if (action == "com.half.wowsca.VIEW_SHIPOPEDIA") {
                CAApp.ROUTING = ShortcutRoutes.ENCYCLOPEDIA
            } else if (action == "com.half.wowsca.VIEW_TWITCH") {
                CAApp.ROUTING = ShortcutRoutes.TWITCH
            } else if (action == "com.half.wowsca.VIEW_SEARCH") {
                CAApp.ROUTING = ShortcutRoutes.SEARCH
            } else {
                val prefs = Prefs(applicationContext)
                if (prefs.getBoolean(SettingActivity.AD_LAUNCH, false)) CAApp.ROUTING =
                    ShortcutRoutes.AD_LAUNCH
                else CAApp.ROUTING = null
            }
        } else {
            CAApp.ROUTING = null
        }
    }

    private fun bindView() {
        swipeBackLayout?.setEnableGesture(false)
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacks(infoPoll)
    }

    private fun initView() {
        val manager = infoManager ?: return
        val hasAllInfo = manager.isInfoThere(applicationContext)
        val connected = hasInternetConnection(applicationContext)
        if (connected) {
            isLoading = true
            showRetryButton = false
            render()
            if (!callNext && hasAllInfo) {
                val r = Runnable {
                    manager.load(
                        applicationContext
                    )
                }
                r.run()
                mainHandler.postDelayed({
                    if (goToNext) {
                        goToNext()
                    }
                }, 2000)
                goToNext = true
                callNext = true
            } else if (!grabbingInfo) {
                info
            }
        } else {
            generalNoInternetDialogAlert(
                this,
                getString(R.string.no_internet_title),
                getString(R.string.no_internet_message),
                getString(R.string.no_internet_neutral_text)
            )
            isLoading = false
            showRetryButton = true
            render()
        }
    }

    private fun goToNext() {
        val i = Intent(applicationContext, MainActivity::class.java)
        startActivity(i)
        finish()
    }

    private val info: Unit
        get() {
            neededInfo
        }

    private val neededInfo: Unit
        get() {
            grabbingInfo = true
            val query = InfoQuery()
            query.server = getServerType(applicationContext)
            val task = GetNeededInfoTask()
            task.setCtx(applicationContext)
            task.execute(query)
            mainHandler.removeCallbacks(infoPoll)
            mainHandler.postDelayed(infoPoll, INFO_POLL_INTERVAL_MILLIS)
        }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(CALL_NEXT_DONE, callNext)
        outState.putBoolean(GRABBING_INFO, grabbingInfo)
        outState.putBoolean(IS_LOADING, isLoading)
        outState.putBoolean(SHOW_RETRY_BUTTON, showRetryButton)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goToNext = false
        mainHandler.removeCallbacks(infoPoll)
    }

    private fun render() {
        composeView.setContent {
            MaterialTheme {
                SplashScreen(
                    isLoading = isLoading,
                    showRetryButton = showRetryButton,
                    onRetry = {
                        initView()
                    }
                )
            }
        }
    }

    companion object {
        const val INFO_POLL_INTERVAL_MILLIS = 500L
        const val GRABBING_INFO: String = "grabbingInfo"
        const val CALL_NEXT_DONE: String = "callNextDone"
        const val IS_LOADING: String = "isLoading"
        const val SHOW_RETRY_BUTTON: String = "showRetryButton"
    }
}

@Composable
private fun SplashScreen(
    isLoading: Boolean,
    showRetryButton: Boolean,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.web_hi_res_512),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(32.dp))
        }
        if (!isLoading && showRetryButton) {
            Button(onClick = onRetry) {
                Text(text = stringResource(R.string.refresh))
            }
        }
    }
}
