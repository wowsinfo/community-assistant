package com.half.wowsca.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.half.wowsca.CAApp
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.half.wowsca.ui.encyclopedia.EncyclopediaViewModel
import com.half.wowsca.ui.splash.SplashScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.half.wowsca.CAApp.Companion.getServerType
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.R
import com.half.wowsca.alerts.Alert.generalNoInternetDialogAlert
import com.half.wowsca.model.enums.ShortcutRoutes
import com.half.wowsca.model.queries.InfoQuery
import com.half.wowsca.model.result.InfoResult
import com.squareup.picasso.Picasso
import com.utilities.Utils.hasInternetConnection
import com.utilities.preferences.Prefs

/**
 * Created by slai4 on 10/31/2015.
 */
@AndroidEntryPoint
class SplashActivity : CABaseActivity() {

    private val encyclopediaViewModel: EncyclopediaViewModel by viewModels()
    private var progressBar: View? = null
    private var button: View? = null
    private var iv: ImageView? = null

    private var callNext = false
    private var grabbingInfo = false
    private var goToNext = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (savedInstanceState != null) {
            callNext = savedInstanceState.getBoolean(CALL_NEXT_DONE)
            grabbingInfo = savedInstanceState.getBoolean(GRABBING_INFO)
        }
        bindView()
        if (intent != null && !TextUtils.isEmpty(intent.action)) {
            val action = intent.action
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
        progressBar = findViewById<View>(R.id.splash_progress)
        button = findViewById<View>(R.id.splash_button)
        iv = findViewById<View>(R.id.imageView) as ImageView?
        Picasso.get().load(R.drawable.web_hi_res_512).into(iv)

        swipeBackLayout!!.setEnableGesture(false)
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    override fun onPause() {
        super.onPause()
        // eventBus removed
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            iv!!.setImageBitmap(null)
        } catch (e: Exception) {
        }
    }

    private fun initView() {
        val hasAllInfo = infoManager!!.isInfoThere(applicationContext)
        val connected = hasInternetConnection(applicationContext)
        if (connected) {
            progressBar!!.visibility = View.VISIBLE
            button!!.visibility = View.GONE
            if (!callNext && hasAllInfo) {
                val r = Runnable {
                    infoManager!!.load(
                        applicationContext
                    )
                }
                r.run()
                val h = Handler()
                h.postDelayed({
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
            progressBar!!.visibility = View.GONE
            button!!.visibility = View.VISIBLE
        }
        button!!.setOnClickListener {
            button!!.visibility = View.GONE
            progressBar!!.visibility = View.VISIBLE
            initView()
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
            encyclopediaViewModel.loadEncyclopedia(applicationContext, getServerType(applicationContext))
        }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(CALL_NEXT_DONE, callNext)
        outState.putBoolean(GRABBING_INFO, grabbingInfo)
    }

        private fun observeEncyclopedia() {
        lifecycleScope.launch {
            encyclopediaViewModel.isLoading.collect { loading ->
                progressBar!!.visibility = if (loading) View.VISIBLE else View.GONE
            }
        }
        lifecycleScope.launch {
            encyclopediaViewModel.isLoaded.collect { loaded ->
                if (loaded) {
                    grabbingInfo = false
                    goToNext()
                }
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        goToNext = false
    }

    companion object {
        const val GRABBING_INFO: String = "grabbingInfo"
        const val CALL_NEXT_DONE: String = "callNextDone"
    }
}
