package com.half.wowsca.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.getServerType
import com.half.wowsca.R
import com.half.wowsca.backend.GetServerInfo
import com.half.wowsca.backend.GetTwitchInfo
import com.half.wowsca.model.ServerInfo
import com.half.wowsca.model.TwitchObj
import com.half.wowsca.model.enums.Server
import com.half.wowsca.model.enums.TwitchStatus
import com.half.wowsca.model.result.ServerResult
import com.half.wowsca.ui.UIUtils.setUpCard
import com.half.wowsca.ui.adapter.TwitchAdapter
import com.utilities.logging.Dlog.wtf
import com.utilities.preferences.Prefs
import com.utilities.views.SwipeBackLayout
import org.greenrobot.eventbus.Subscribe
import java.util.Locale
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Created by slai4 on 11/29/2015.
 */
class ResourcesActivity : CABaseActivity() {
    private var type: String? = null

    //donation area
    private var aDonation: View? = null

    //    private WebView webView;
    private var bPaypal: Button? = null
    private var bViewAd: Button? = null
    private var viewAd = false
    private var adProgress: View? = null

    //servsers
    private var aServers: View? = null
    private var serverProgress: View? = null
    private var llServerContainer: LinearLayout? = null
    private var tvWoTServerNumber: TextView? = null
    private var tvWoWsServerNumber: TextView? = null

    //twitch
    private var aTwitch: View? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: TwitchAdapter? = null
    private var layoutManager: GridLayoutManager? = null
    private var twitchProgress: View? = null

    //websites
    private var aWebsites: View? = null
    private var aShipComrade: View? = null
    private var aWowsSite: View? = null
    private var aReddit: View? = null
    private var aDRMB: View? = null
    private var aAP: View? = null
    private var aWoWsReplays: View? = null

    private var ivShipComrade: ImageView? = null
    private var ivWoWsIcon: ImageView? = null
    private var ivReddit: ImageView? = null
    private var ivDRMB: ImageView? = null
    private var ivAP: ImageView? = null
    private var ivWoWsReplays: ImageView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resources)
        if (savedInstanceState != null) {
            type = savedInstanceState.getString(EXTRA_TYPE)
            viewAd = false
        } else {
            type = intent.getStringExtra(EXTRA_TYPE)
            viewAd = intent.getBooleanExtra(EXTRA_VIEW_AD, false)
        }
        bindView()
    }

    private fun bindView() {
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar?
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        if (!TextUtils.isEmpty(type)) {
            if (type == EXTRA_WEBSITES_TOOLS) {
                title = getString(R.string.resources_websites)
            } else if (type == EXTRA_DONATE) {
                title = getString(R.string.resources_support)
            } else if (type == EXTRA_TWITCH) {
                title = getString(R.string.action_twitch)
            } else if (type == EXTRA_SERVERS) {
                title = getString(R.string.resources_server_info)
            }
        }
        aDonation = findViewById<View>(R.id.resources_donation_area)
        //        webView = (WebView) findViewById(R.id.resources_webview);
        bPaypal = findViewById<View>(R.id.resources_donation_paypal) as Button?
        bViewAd = findViewById<View>(R.id.resources_view_add) as Button?
        adProgress = findViewById<View>(R.id.resources_view_ad_progress)

        aServers = findViewById<View>(R.id.resources_server_area)
        llServerContainer = findViewById<View>(R.id.resources_server_container) as LinearLayout?
        serverProgress = findViewById<View>(R.id.resources_server_progress)
        tvWoTServerNumber = findViewById<View>(R.id.resources_server_wot_numbers) as TextView?
        tvWoWsServerNumber = findViewById<View>(R.id.resources_server_wows_numbers) as TextView?

        aTwitch = findViewById<View>(R.id.resources_twitch_area)
        recyclerView = findViewById<View>(R.id.resources_twitch_list) as RecyclerView?
        twitchProgress = findViewById<View>(R.id.resources_twitch_progress)

        aWebsites = findViewById<View>(R.id.resources_website_area)
        aShipComrade = findViewById<View>(R.id.resources_website_ship_comrade)
        aWowsSite = findViewById<View>(R.id.resources_website_wows)
        aReddit = findViewById<View>(R.id.resources_website_reddit)
        aDRMB = findViewById<View>(R.id.resources_website_drmb)
        aAP = findViewById<View>(R.id.resources_website_ap)
        aWoWsReplays = findViewById<View>(R.id.resources_website_wowsreplays)

        ivShipComrade = findViewById<View>(R.id.resources_website_ship_comrade_icon) as ImageView?
        ivWoWsIcon = findViewById<View>(R.id.resources_website_website_icon) as ImageView?
        ivReddit = findViewById<View>(R.id.resources_website_reddit_icon) as ImageView?
        ivDRMB = findViewById<View>(R.id.resources_website_drmb_icon) as ImageView?
        ivAP = findViewById<View>(R.id.resources_website_ap_icon) as ImageView?
        ivWoWsReplays = findViewById<View>(R.id.resources_website_wowsreplays_icon) as ImageView?

        setUpCard(aShipComrade!!, 0)
        setUpCard(aWowsSite!!, 0)
        setUpCard(aReddit!!, 0)
        setUpCard(aDRMB!!, 0)
        setUpCard(aAP!!, 0)
        setUpCard(aWoWsReplays!!, 0)

        swipeBackLayout!!.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_TYPE, type)
    }

    override fun onResume() {
        super.onResume()
        eventBus.register(this)
        initView()
    }

    override fun onPause() {
        super.onPause()
        eventBus.unregister(this)
    }

    private fun initView() {
        if (!TextUtils.isEmpty(type)) {
            if (type == EXTRA_DONATE) {
                setUpDonationArea()
            } else if (type == EXTRA_SERVERS) {
                setUpServerInfo()
            } else if (type == EXTRA_TWITCH) {
                setUpTwitch()
            } else if (type == EXTRA_WEBSITES_TOOLS) {
                setUpWebsites()
            }
        }
    }

    private fun setUpDonationArea() {
        aDonation!!.visibility = View.VISIBLE
        bViewAd!!.setOnClickListener {
            requestNewInterstitial()
            //                Prefs prefs = new Prefs(getApplicationContext());
//                prefs.setBoolean("hasDonated", true);
        }
        bPaypal!!.setOnClickListener {
            val prefs = Prefs(applicationContext)
            prefs.setBoolean("hasDonated2", true)
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse("https://patreon.com/slai47"))
            startActivity(i)
        }

        //        webView.setWebChromeClient(new WebChromeClient());
//        webView.setWebViewClient(new MyWebViewClient());
//        webView.setBackgroundColor(Color.TRANSPARENT);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setSupportZoom(false);
//        webView.loadUrl("file:///android_asset/donation.html");
        if (viewAd) {
            requestNewInterstitial()
        }
    }

    private fun setUpServerInfo() {
        aServers!!.visibility = View.VISIBLE
        if (serverResult != null) {
            serverProgress!!.visibility = View.GONE
            llServerContainer!!.visibility = View.VISIBLE
            llServerContainer!!.removeAllViews()

            var totalWot = 0
            var totalWoWs = 0

            for (serInfo in serverResult!!.wotNumbers) {
                totalWot += serInfo.players
            }

            for (serInfo in serverResult!!.wowsNumbers) {
                totalWoWs += serInfo.players
            }

            tvWoTServerNumber!!.text = "" + totalWot

            tvWoWsServerNumber!!.text = "" + totalWoWs

            val s = getServerType(applicationContext)

            createServer(s)

            for (server in Server.entries) {
                if (server.ordinal != s.ordinal) {
                    createServer(server)
                }
            }
        } else {
            val info = GetServerInfo(applicationContext)
            info.execute("")
            serverProgress!!.visibility = View.VISIBLE
            llServerContainer!!.visibility = View.GONE
        }
    }

    private fun setUpTwitch() {
        aTwitch!!.visibility = View.VISIBLE
        if (streamers != null) {
            if (adapter == null) {
                layoutManager = GridLayoutManager(
                    applicationContext,
                    resources.getInteger(R.integer.twitch_cols)
                )
                layoutManager!!.orientation = GridLayoutManager.VERTICAL
                adapter = TwitchAdapter()
                recyclerView!!.layoutManager = layoutManager
                addYoutubeOnlyStar()
                adapter!!.twitchObjs = streamers
                adapter!!.ctx = applicationContext
                recyclerView!!.adapter = adapter
            } else {
                adapter!!.twitchObjs = streamers
                adapter!!.sort()
                adapter!!.notifyDataSetChanged()
            }
            twitchProgress!!.visibility = View.GONE
        } else {
            twitchProgress!!.visibility = View.VISIBLE
            val array = arrayOf(
                "iChaseGaming",
                "Mejash",
                "nozoupforyou",
                "Aerroon",
                "BaronVonGamez",
                "wda_punisher",
                "wargaming",
                "iEarlGrey",
                "Flamuu",
                "clydethamonkey",
                "crysantos",
                "kamisamurai",
                "notser"
            )
            val number_of_cores = Runtime.getRuntime().availableProcessors()
            val mWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue()
            val executor = ThreadPoolExecutor(
                number_of_cores,
                number_of_cores,
                60,
                TimeUnit.SECONDS,
                mWorkQueue
            )
            for (name in array) {
                val info = GetTwitchInfo()
                info.executeOnExecutor(executor, name)
            }
            streamers = ArrayList()
        }
    }

    private fun addYoutubeOnlyStar() {
        val obj = TwitchObj("Jammin411")
        obj.isLive = TwitchStatus.YOUTUBE
        streamers!!.add(obj)
    }

    private fun setUpWebsites() {
        aWebsites!!.visibility = View.VISIBLE
        val listener = View.OnClickListener { v ->
            val url = v.tag as String
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse(url))
            startActivity(i)
        }

        aWowsSite!!.tag = "http://www.worldofwarships" + getServerType(applicationContext).suffix
        aShipComrade!!.tag = "http://www.shipcomrade.com"
        aReddit!!.tag = "http://www.reddit.com/r/worldofwarships"
        aDRMB!!.tag = "http://www.dontrevivemebro.com"
        aAP!!.tag = "http://thearmoredpatrol.com/"
        aWoWsReplays!!.tag = "http://wowreplays.com/"

        aWowsSite!!.setOnClickListener(listener)
        aShipComrade!!.setOnClickListener(listener)
        aReddit!!.setOnClickListener(listener)
        aDRMB!!.setOnClickListener(listener)
        aAP!!.setOnClickListener(listener)
        aWoWsReplays!!.setOnClickListener(listener)

        ivWoWsIcon!!.setImageResource(R.drawable.ic_wows_logo)
        ivShipComrade!!.setImageResource(R.drawable.ic_ship_comrade)
        ivReddit!!.setImageResource(R.drawable.ic_reddit)
        ivDRMB!!.setImageResource(R.drawable.twitch)
        ivAP!!.setImageResource(R.drawable.ic_armored_patrol)
        ivWoWsReplays!!.setImageResource(R.drawable.ic_wowsreplays)
    }

    private fun requestNewInterstitial() {
    }

    private fun createServer(s: Server) {
        val serverInfo = LayoutInflater.from(applicationContext)
            .inflate(R.layout.list_server_info, llServerContainer, false)

        val serverName = serverInfo.findViewById<TextView>(R.id.server_info_name)

        serverName.text = s.toString().uppercase(Locale.getDefault())

        val wotContainer = serverInfo.findViewById<LinearLayout>(R.id.server_info_container_1)
        val wowsContainer = serverInfo.findViewById<LinearLayout>(R.id.server_info_container_2)

        val currentServerWoT: MutableList<ServerInfo> = ArrayList()
        val currentServerWoWs: MutableList<ServerInfo> = ArrayList()

        var totalWot = 0
        var totalWoWs = 0

        for (serInfo in serverResult!!.wotNumbers) {
            if (serInfo.server!!.ordinal == s.ordinal) {
                currentServerWoT.add(serInfo)
                totalWot += serInfo.players
            }
        }

        for (serInfo in serverResult!!.wowsNumbers) {
            if (serInfo.server!!.ordinal == s.ordinal) {
                currentServerWoWs.add(serInfo)
                totalWoWs += serInfo.players
            }
        }

        val layoutId = R.layout.list_server
        val serverWoTTitle =
            LayoutInflater.from(applicationContext).inflate(layoutId, wotContainer, false)
        val tvwot = serverWoTTitle.findViewById<TextView>(R.id.list_server_text)
        tvwot.text = getString(R.string.resources_wot_total_c) + totalWot


        val serverWowsTitle =
            LayoutInflater.from(applicationContext).inflate(layoutId, wotContainer, false)
        val tvwows = serverWowsTitle.findViewById<TextView>(R.id.list_server_text)
        tvwows.text = getString(R.string.resources_wows_total_c) + totalWoWs

        wotContainer.addView(serverWoTTitle)
        wowsContainer.addView(serverWowsTitle)

        for (info in currentServerWoT) {
            val server =
                LayoutInflater.from(applicationContext).inflate(layoutId, wotContainer, false)
            val text = server.findViewById<TextView>(R.id.list_server_text)
            text.text = info.name + " - " + info.players
            wotContainer.addView(server)
        }

        for (info in currentServerWoWs) {
            val server =
                LayoutInflater.from(applicationContext).inflate(layoutId, wowsContainer, false)
            val text = server.findViewById<TextView>(R.id.list_server_text)
            text.text = info.name + " - " + info.players
            wowsContainer.addView(server)
        }
        llServerContainer!!.addView(serverInfo)
    }

    @Subscribe
    fun onTwitchReceived(obj: TwitchObj) {
        runOnUiThread {
            streamers!!.add(obj)
            initView()
        }
    }


    @Subscribe
    fun onRecieveServers(result: ServerResult?) {
        if (result != null) {
            serverResult = result
            runOnUiThread { initView() }
        } else {
            serverResult = ServerResult()
            runOnUiThread {
                Toast.makeText(applicationContext, R.string.resources_error, Toast.LENGTH_SHORT)
                    .show()
                initView()
            }
        }
    }

    @Subscribe
    fun urlSent(url: String) {
        wtf("urlSent", "url = $url")
        val i = Intent(Intent.ACTION_VIEW)
        i.setData(Uri.parse(url))
        startActivity(i)
    }

    protected inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (!TextUtils.isEmpty(url)) {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(i)
                return true
            }
            return false
        }
    }

    companion object {
        const val EXTRA_TYPE: String = "type"
        const val EXTRA_VIEW_AD: String = "view_ad"

        const val EXTRA_WEBSITES_TOOLS: String = "tools"
        const val EXTRA_DONATE: String = "donate"
        const val EXTRA_TWITCH: String = "twitch"
        const val EXTRA_SERVERS: String = "servers"

        var serverResult: ServerResult? = null

        var streamers: MutableList<TwitchObj>? = null
    }
}