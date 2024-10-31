package com.half.wowsca.ui

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.Toolbar
import com.half.wowsca.CAApp.Companion.getServerType
import com.half.wowsca.R
import com.half.wowsca.model.AuthInfo
import com.utilities.logging.Dlog.d
import com.utilities.preferences.Prefs
import com.utilities.views.SwipeBackLayout

/**
 * Created by slai4 on 5/1/2016.
 */
class AuthenticationActivity : CABaseActivity() {
    var webview: WebView? = null
    var progress: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        webview = findViewById<View>(R.id.webView) as WebView?

        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar?

        progress = findViewById<View>(R.id.progressBar)

        mToolbar!!.setTitleTextColor(Color.WHITE)
        setSupportActionBar(mToolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.login)

        swipeBackLayout!!.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT)
    }

    override fun onResume() {
        super.onResume()
        webview!!.webViewClient = MyCustomWebClient()
        webview!!.webChromeClient = MyCustonChromeClient()

        webview!!.settings.javaScriptEnabled = true
        webview!!.settings.javaScriptCanOpenWindowsAutomatically = true


        val server = getServerType(applicationContext)
        val url = webview!!.url
        d("AUTH", "url = $url")
        if (TextUtils.isEmpty(url)) {
            webview!!.loadUrl("https://api.worldoftanks" + server.suffix + "/wot/auth/login/?application_id=" + server.appId + "&redirect_uri=https%3A%2F%2Fna.wargaming.net%2Fdevelopers%2Fapi_explorer%2Fwot%2Fauth%2Flogin%2Fcomplete%2F")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_auth, menu)
        return true
    }

    private fun getParamObj(params: String): String {
        val paramList = params.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return paramList[1]
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        } else if (item.itemId == R.id.action_Login) {
            val prefs = Prefs(applicationContext)
            prefs.setBoolean(SettingActivity.LOGIN_USER, false)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class MyCustomWebClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            // Here put your code
            Log.d("My Webview", url)

            if (url.contains("complete/?&status=ok")) {
                val uri = Uri.parse(url)
                val info = AuthInfo()
                info.token = uri.getQueryParameter("access_token")
                info.account_id = uri.getQueryParameter("account_id")!!.toLong()
                info.expires = uri.getQueryParameter("expires_at")!!.toLong()
                info.username = uri.getQueryParameter("nickname")
                d("AuthURL", info.toString())
                //                String[] split = url.split("?");
//                if(split.length > 1){
//                    String params = split[1];
//                    Dlog.d("Auth", params);
//                    String[] pList = params.split("&");
//                    AuthInfo info = new AuthInfo();
//                    for(int i = 0; i < pList.length; i++){
//                        String param = pList[i];
//                        Dlog.d("AuthP", param);
//                        if(param.contains("access_token")){
//                            String token = getParamObj(param);
//                            info.setToken(token);
//                        } else if(param.contains("expires_at")){
//                            String token = getParamObj(param);
//                            info.setExpires(Long.parseLong(token));
//                        } else if(param.contains("account_id")){
//                            String token = getParamObj(param);
//                            info.setAccount_id(Long.parseLong(token));
//                        } else if(param.contains("username")){
//                            String token = getParamObj(param);
//                            info.setToken(token);
//                        }
//                    }
                info.save(applicationContext)
                finish()
                //                }
                return true
            }
            return false
        }
    }

    private inner class MyCustonChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            if (newProgress < 100) {
                progress!!.visibility = View.VISIBLE
            } else {
                progress!!.visibility = View.GONE
            }
        }
    }
}
