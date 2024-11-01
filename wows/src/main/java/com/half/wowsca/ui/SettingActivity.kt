package com.half.wowsca.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.getAppLanguage
import com.half.wowsca.CAApp.Companion.getServerLanguage
import com.half.wowsca.CAApp.Companion.getServerType
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.CAApp.Companion.isColorblind
import com.half.wowsca.CAApp.Companion.isDarkTheme
import com.half.wowsca.CAApp.Companion.relaunchApplication
import com.half.wowsca.CAApp.Companion.setAppLanguage
import com.half.wowsca.CAApp.Companion.setColorblindMode
import com.half.wowsca.CAApp.Companion.setServerLanguage
import com.half.wowsca.CAApp.Companion.setServerType
import com.half.wowsca.R
import com.half.wowsca.alerts.Alert.createGeneralAlert
import com.half.wowsca.backend.GetNeededInfoTask
import com.half.wowsca.managers.InfoManager.Companion.purge
import com.half.wowsca.managers.StorageManager.clearDownloadedPlayers
import com.half.wowsca.managers.StorageManager.getShipsStatsMax
import com.half.wowsca.managers.StorageManager.getStatsMax
import com.half.wowsca.managers.StorageManager.setShipsStatsMax
import com.half.wowsca.managers.StorageManager.setStatsMax
import com.half.wowsca.model.enums.Server
import com.half.wowsca.model.queries.InfoQuery
import com.half.wowsca.model.result.InfoResult
import com.utilities.preferences.Prefs
import com.utilities.views.SwipeBackLayout
import org.greenrobot.eventbus.Subscribe
import java.util.Locale

class SettingActivity : CABaseActivity() {
    private val languages = arrayOf(
        "en",
        "ru",
        "pl",
        "de",
        "fr",
        "es",
        "zh-cn",
        "tr",
        "cs",
        "th",
        "vi",
        "ja",
        "zh-tw",
        "pt-br",
        "es-mx"
    )
    private val languagesShow = arrayOf(
        "English",
        "Русский",
        "Polski",
        "Deutsch",
        "Français",
        "Español",
        "简体中文",
        "Türkçe",
        "Čeština",
        "ไทย",
        "Tiếng Việt",
        "日本語",
        "繁體中文",
        "Português do Brasil",
        "Español (México)"
    )
    private val appLangauges = arrayOf("en", "ru", "de", "es", "hr", "nl")
    private val appLangaugesShow =
        arrayOf("English", "Русский", "Deutsch", "Español", "Magyar", "Nederlands")
    private val saveNumChoices = arrayOf("5", "10", "15", "20", "25")
    private val saveNumbers = intArrayOf(5, 10, 15, 20, 25)
    private val themes = arrayOf("dynamic", "ocean", "dark")
    private var aColorblind: View? = null
    private var cbColorblind: CheckBox? = null
    private var aCompare: View? = null
    private var cbCompare: CheckBox? = null
    private var aNoArp: View? = null
    private var cbNoArp: CheckBox? = null
    private var aLogin: View? = null
    private var cbLogin: CheckBox? = null
    private var aAdLaunch: View? = null
    private var cbAdLaunch: CheckBox? = null
    private var aClearPlayer: View? = null
    private var aVersion: View? = null
    private var aRefreshInfo: View? = null
    private var tvVersionText: TextView? = null
    private var aReview: View? = null
    private var aEmail: View? = null
    private var aTwitter: View? = null
    private var aWordPress: View? = null
    private var aWoTCom: View? = null
    private var sServerLanguage: Spinner? = null
    private var sTheme: Spinner? = null
    private var sAppLanguage: Spinner? = null
    private var sServer: Spinner? = null
    private var sCaptainSaves: Spinner? = null
    private var sShipSaves: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        bindView()
    }

    private fun bindView() {
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar?
        mToolbar?.setTitleTextColor(Color.WHITE)
        setSupportActionBar(mToolbar)

        setTitle(R.string.settings)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        aColorblind = findViewById<View>(R.id.settings_colorblind_area)
        cbColorblind = findViewById<View>(R.id.settings_colorblind_checkbox) as CheckBox?

        aCompare = findViewById<View>(R.id.settings_compare_area)
        cbCompare = findViewById<View>(R.id.settings_compare_checkbox) as CheckBox?

        aNoArp = findViewById<View>(R.id.settings_arp_area)
        cbNoArp = findViewById<View>(R.id.settings_arp_checkbox) as CheckBox?

        aLogin = findViewById<View>(R.id.settings_login_area)
        cbLogin = findViewById<View>(R.id.settings_login_checkbox) as CheckBox?

        aVersion = findViewById<View>(R.id.settings_version_area)

        aClearPlayer = findViewById<View>(R.id.settings_clear_players_area)

        aRefreshInfo = findViewById<View>(R.id.settings_clear_saved_data)

        aWordPress = findViewById<View>(R.id.settings_wordpress)

        aAdLaunch = findViewById<View>(R.id.settings_ad_launch_area)
        cbAdLaunch = findViewById<View>(R.id.settings_ad_launch_checkbox) as CheckBox?

        tvVersionText = findViewById<View>(R.id.settings_verison) as TextView?

        aReview = findViewById<View>(R.id.settings_review)
        aEmail = findViewById<View>(R.id.settings_contact)
        aTwitter = findViewById<View>(R.id.settings_twitter)
        aWoTCom = findViewById<View>(R.id.settings_wot)

        sServerLanguage = findViewById<View>(R.id.settings_server_spinner) as Spinner?
        sTheme = findViewById<View>(R.id.settings_theme_spinner) as Spinner?
        sAppLanguage = findViewById<View>(R.id.settings_language_spinner) as Spinner?
        sServer = findViewById<View>(R.id.settings_stats_server_spinner) as Spinner?

        sCaptainSaves = findViewById<View>(R.id.settings_stats_captain_saves_spinner) as Spinner?
        sShipSaves = findViewById<View>(R.id.settings_stats_ships_saves_spinner) as Spinner?

        swipeBackLayout!!.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT)
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
        initCheckBoxes()
        initOnClickVersionRefresh()
        initLinks()
        initServerLangauge()
        initTheme()
        initServer()
        initSaveOptions()
        initAppLangauge()
    }

    private fun initSaveOptions() {
        val adapter =
            ArrayAdapter(applicationContext, R.layout.ca_spinner_item_trans, saveNumChoices)
        adapter.setDropDownViewResource(if (!isDarkTheme(sTheme!!.context)) R.layout.ca_spinner_item else R.layout.ca_spinner_item_dark)
        sCaptainSaves!!.adapter = adapter
        var defaultSelected = 0
        val numOfSaves = getStatsMax(applicationContext)
        for (i in saveNumbers.indices) {
            if (saveNumbers[i] == numOfSaves) {
                defaultSelected = i
                break
            }
        }

        sCaptainSaves!!.setSelection(defaultSelected)

        sCaptainSaves!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val current = getStatsMax(
                    applicationContext
                )
                val selected = saveNumbers[position]
                if (current != selected) {
                    if (current > selected) {
                        val builder = AlertDialog.Builder(this@SettingActivity)
                        builder.setIcon(R.drawable.launcher_icon)
                        builder.setTitle(getString(R.string.settings_stats_number_dialog_title))
                        builder.setMessage(getString(R.string.settings_stats_number_dialog_message))
                        builder.setPositiveButton(getString(R.string.settings_stats_number_dialog_positive)) { dialog, which ->
                            setStatsMax(applicationContext, selected)
                            aClearPlayer!!.callOnClick()
                        }
                        builder.setNegativeButton(R.string.dismiss) { dialog, which ->
                            var defaultSelected = 0
                            val numOfSaves = getStatsMax(
                                applicationContext
                            )
                            for (i in saveNumbers.indices) {
                                if (saveNumbers[i] == numOfSaves) {
                                    defaultSelected = i
                                    break
                                }
                            }

                            sCaptainSaves!!.setSelection(defaultSelected)
                            dialog.dismiss()
                        }
                        builder.show()
                    } else {
                        setStatsMax(applicationContext, selected)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val adapter2 =
            ArrayAdapter(applicationContext, R.layout.ca_spinner_item_trans, saveNumChoices)
        adapter2.setDropDownViewResource(if (!isDarkTheme(sTheme!!.context)) R.layout.ca_spinner_item else R.layout.ca_spinner_item_dark)
        sShipSaves!!.adapter = adapter2
        var defaultSelected2 = 0
        val numOfSaves2 = getShipsStatsMax(
            applicationContext
        )
        for (i in saveNumbers.indices) {
            if (saveNumbers[i] == numOfSaves2) {
                defaultSelected2 = i
                break
            }
        }
        sShipSaves!!.setSelection(defaultSelected2)
        sShipSaves!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val current = getShipsStatsMax(
                    applicationContext
                )
                val selected = saveNumbers[position]
                if (current != selected) {
                    if (current > selected) {
                        val builder = AlertDialog.Builder(this@SettingActivity)
                        builder.setIcon(R.drawable.launcher_icon)
                        builder.setTitle(getString(R.string.settings_stats_number_dialog_title))
                        builder.setMessage(getString(R.string.settings_stats_number_dialog_message))
                        builder.setPositiveButton(getString(R.string.settings_stats_number_dialog_positive)) { dialog, which ->
                            setShipsStatsMax(
                                applicationContext, selected
                            )
                            aClearPlayer!!.callOnClick()
                        }
                        builder.setNegativeButton(R.string.dismiss) { dialog, which ->
                            var defaultSelected2 = 0
                            val numOfSaves2 = getShipsStatsMax(
                                applicationContext
                            )
                            for (i in saveNumbers.indices) {
                                if (saveNumbers[i] == numOfSaves2) {
                                    defaultSelected2 = i
                                    break
                                }
                            }
                            sShipSaves!!.setSelection(defaultSelected2)
                            dialog.dismiss()
                        }
                        builder.show()
                    } else {
                        setShipsStatsMax(applicationContext, selected)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun initServer() {
        val servers: MutableList<String> = ArrayList()
        val current = getServerType(applicationContext)
        servers.add(current.toString().uppercase(Locale.getDefault()))
        for (s in Server.entries) {
            if (current.ordinal != s.ordinal) {
                servers.add(s.toString().uppercase(Locale.getDefault()))
            }
        }
        val adapter = ArrayAdapter(applicationContext, R.layout.ca_spinner_item_trans, servers)
        adapter.setDropDownViewResource(if (!isDarkTheme(sServer!!.context)) R.layout.ca_spinner_item else R.layout.ca_spinner_item_dark)
        sServer!!.adapter = adapter
        sServer!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val servers: MutableList<Server> = ArrayList()
                val current = getServerType(applicationContext)
                servers.add(current)
                for (s in Server.entries) {
                    if (current.ordinal != s.ordinal) {
                        servers.add(s)
                    }
                }
                val server = servers[position]
                if (server != current) {
                    setServerType(applicationContext, server)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun initOnClickVersionRefresh() {
        aVersion!!.setOnClickListener {
            val ctx: Context = this@SettingActivity
            createGeneralAlert(
                ctx,
                getString(R.string.update_notes_title),
                getString(R.string.patch_notes) + getString(R.string.update_notes_achieve),
                "Dismiss"
            )
        }

        aClearPlayer!!.setOnClickListener {
            clearDownloadedPlayers(applicationContext)
            Toast.makeText(
                applicationContext,
                R.string.player_stored_data_deleted,
                Toast.LENGTH_SHORT
            ).show()
        }

        aRefreshInfo!!.setOnClickListener { v ->
            infoManager
            purge(v.context)
            val query = InfoQuery()
            query.server = getServerType(applicationContext)
            val task = GetNeededInfoTask()
            task.setCtx(applicationContext)
            task.execute(query)
            Toast.makeText(applicationContext, R.string.purging_refresh, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initCheckBoxes() {
        val isColorblind = isColorblind(applicationContext)
        val prefs = Prefs(applicationContext)
        val showCompare = prefs.getBoolean(SHOW_COMPARE, true)
        val noArp = prefs.getBoolean(NO_ARP, false)
        val login = prefs.getBoolean(LOGIN_USER, false)
        val showAdLaunch = prefs.getBoolean(AD_LAUNCH, false)
        cbAdLaunch!!.isChecked = showAdLaunch
        aAdLaunch!!.setOnClickListener {
            val prefs = Prefs(applicationContext)
            var showAd = prefs.getBoolean(AD_LAUNCH, false)
            showAd = !showAd
            cbAdLaunch!!.isChecked = showAd
            prefs.setBoolean(AD_LAUNCH, showAd)
        }
        cbColorblind!!.isChecked = isColorblind
        aColorblind!!.setOnClickListener {
            var isColorblind = isColorblind(applicationContext)
            isColorblind = !isColorblind
            cbColorblind!!.isChecked = isColorblind
            setColorblindMode(applicationContext, isColorblind)
        }
        cbCompare!!.isChecked = showCompare
        aCompare!!.setOnClickListener {
            val prefs = Prefs(applicationContext)
            var showCompare = prefs.getBoolean(SHOW_COMPARE, true)
            showCompare = !showCompare
            cbCompare!!.isChecked = showCompare
            prefs.setBoolean(SHOW_COMPARE, showCompare)
        }

        cbNoArp!!.isChecked = noArp
        aNoArp!!.setOnClickListener {
            val prefs = Prefs(applicationContext)
            var showCompare = prefs.getBoolean(NO_ARP, true)
            showCompare = !showCompare
            cbNoArp!!.isChecked = showCompare
            prefs.setBoolean(NO_ARP, showCompare)
        }

        cbLogin!!.isChecked = login
        aLogin!!.setOnClickListener {
            val prefs = Prefs(applicationContext)
            var loginUser = prefs.getBoolean(LOGIN_USER, true)
            loginUser = !loginUser
            cbLogin!!.isChecked = loginUser
            prefs.setBoolean(LOGIN_USER, loginUser)
        }
    }

    private fun initTheme() {
        val themesList = resources.getStringArray(R.array.themes)
        val prefs = Prefs(applicationContext)
        val current = prefs.getString(THEME_CHOICE, "dynamic")
        val position = themes.indexOf(current)

        val adapter = ArrayAdapter(applicationContext, R.layout.ca_spinner_item_trans, themesList)
        adapter.setDropDownViewResource(if (!isDarkTheme(sTheme!!.context)) R.layout.ca_spinner_item else R.layout.ca_spinner_item_dark)
        sTheme!!.adapter = adapter

        sTheme!!.setSelection(position)

        sTheme!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val prefs = Prefs(applicationContext)
                val current = prefs.getString(THEME_CHOICE, "dynamic")
                val selected = themes[position]
                if (current != selected) {
                    prefs.setString(THEME_CHOICE, selected)
                    //restart app
                    relaunchApplication(this@SettingActivity)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun initLinks() {
        aWordPress!!.setOnClickListener {
            val url = "https://communityassistant.wordpress.com/"
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse(url))
            startActivity(i)
        }
        try {
            tvVersionText!!.text = packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
        }
        aReview!!.setOnClickListener {
            val url = "https://play.google.com/store/apps/details?id=com.half.wowsca"
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse(url))
            startActivity(i)
        }
        aEmail!!.setOnClickListener {
            val url = "mailto:wotcommunityassistant@gmail.com"
            val i = Intent(Intent.ACTION_SENDTO)
            i.setData(Uri.parse(url))
            startActivity(i)
        }
        aTwitter!!.setOnClickListener {
            val url = "https://twitter.com/slai47"
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse(url))
            startActivity(i)
        }

        aWoTCom!!.setOnClickListener {
            val url = "https://play.google.com/store/apps/details?id=com.cp.assist"
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse(url))
            startActivity(i)
        }
    }

    private fun initServerLangauge() {
        val adapter =
            ArrayAdapter(applicationContext, R.layout.ca_spinner_item_trans, languagesShow)
        adapter.setDropDownViewResource(if (!isDarkTheme(sTheme!!.context)) R.layout.ca_spinner_item else R.layout.ca_spinner_item_dark)
        sServerLanguage!!.adapter = adapter
        var defaultSelected = 0
        val current = getServerLanguage(applicationContext)
        for (i in languages.indices) {
            if (languages[i] == current) {
                defaultSelected = i
                break
            }
        }

        sServerLanguage!!.setSelection(defaultSelected)

        sServerLanguage!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val current = getServerLanguage(applicationContext)
                val selected = languages[position]
                if (current != selected) {
                    setServerLanguage(applicationContext, selected)
                    val t = Toast.makeText(
                        applicationContext,
                        R.string.settings_update_to_new_language,
                        Toast.LENGTH_LONG
                    )
                    t.setGravity(Gravity.CENTER, 0, 0)
                    t.show()
                    aRefreshInfo!!.callOnClick()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun initAppLangauge() {
        val adapter =
            ArrayAdapter(applicationContext, R.layout.ca_spinner_item_trans, appLangaugesShow)
        adapter.setDropDownViewResource(if (!isDarkTheme(sTheme!!.context)) R.layout.ca_spinner_item else R.layout.ca_spinner_item_dark)
        sAppLanguage!!.adapter = adapter
        var defaultSelected = 0
        val current = getAppLanguage(applicationContext)
        for (i in appLangauges.indices) {
            if (appLangauges[i] == current) {
                defaultSelected = i
                break
            }
        }

        sAppLanguage!!.setSelection(defaultSelected)

        sAppLanguage!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val current = getAppLanguage(applicationContext)
                val selected = appLangauges[position]
                if (current != selected) {
                    setAppLanguage(applicationContext, selected)
                    relaunchApplication(this@SettingActivity)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }


    @Subscribe
    fun onInfoRecieved(result: InfoResult?) {
        aRefreshInfo!!.post {
            Toast.makeText(
                applicationContext,
                R.string.purge_refresh_done,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val SHOW_COMPARE: String = "showCompare"
        const val NO_ARP: String = "noArp"
        const val THEME_CHOICE: String = "themeChoice"
        const val LOGIN_USER: String = "login_user"
        const val AD_LAUNCH: String = "ad_launch"
    }
}
