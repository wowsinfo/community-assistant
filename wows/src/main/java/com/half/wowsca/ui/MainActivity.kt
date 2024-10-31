package com.half.wowsca.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.half.wowsca.CAApp
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.getSelectedId
import com.half.wowsca.CAApp.Companion.setSelectedId
import com.half.wowsca.NumberVault
import com.half.wowsca.R
import com.half.wowsca.alerts.Alert.generalNoInternetDialogAlert
import com.half.wowsca.backend.GetCaptainTask
import com.half.wowsca.interfaces.ICaptain
import com.half.wowsca.managers.CaptainManager.createCapIdStr
import com.half.wowsca.managers.CaptainManager.getCaptains
import com.half.wowsca.managers.CaptainManager.removeCaptain
import com.half.wowsca.managers.CaptainManager.saveCaptain
import com.half.wowsca.managers.NavigationDrawerManager.createDrawer
import com.half.wowsca.managers.NavigationDrawerManager.getDrawerItemList
import com.half.wowsca.managers.StorageManager.savePlayerStats
import com.half.wowsca.model.AddRemoveEvent
import com.half.wowsca.model.AuthInfo
import com.half.wowsca.model.AuthInfo.Companion.getAuthInfo
import com.half.wowsca.model.Captain
import com.half.wowsca.model.CaptainReceivedEvent
import com.half.wowsca.model.ProgressEvent
import com.half.wowsca.model.RefreshEvent
import com.half.wowsca.model.ShipClickedEvent
import com.half.wowsca.model.drawer.DrawerChild
import com.half.wowsca.model.enums.DrawerType
import com.half.wowsca.model.enums.ShortcutRoutes
import com.half.wowsca.model.queries.CaptainQuery
import com.half.wowsca.model.result.CaptainResult
import com.half.wowsca.ui.ResourcesActivity
import com.half.wowsca.ui.SettingActivity
import com.half.wowsca.ui.UIUtils.createBookmarkingDialogIfNeeded
import com.half.wowsca.ui.UIUtils.createDonationDialog
import com.half.wowsca.ui.UIUtils.createFollowDialog
import com.half.wowsca.ui.UIUtils.createReviewDialog
import com.half.wowsca.ui.encyclopedia.EncyclopediaTabbedActivity
import com.half.wowsca.ui.viewcaptain.ShipFragment
import com.half.wowsca.ui.viewcaptain.ViewCaptainActivity
import com.half.wowsca.ui.viewcaptain.ViewCaptainTabbedFragment
import com.half.wowsca.ui.viewcaptain.tabs.CaptainShipsFragment
import com.mikepenz.materialdrawer.Drawer
import com.utilities.Utils.hasInternetConnection
import com.utilities.logging.Dlog.wtf
import com.utilities.preferences.Prefs
import org.greenrobot.eventbus.Subscribe

class MainActivity() : CABaseActivity(), ICaptain {
    private var selectedId: String? = null

    private var drawer: Drawer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindView()
        selectedId = getSelectedId(applicationContext)
    }

    private fun bindView() {
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar?
        mToolbar!!.setTitleTextColor(Color.WHITE)

        //        ivKarma = (ImageView) findViewById(R.id.toolbar_icon);
//        tvKarma = (TextView) findViewById(R.id.toolbar_text);
        setSupportActionBar(mToolbar)

        setUpDrawer()

        initBackStackListener()

        swipeBackLayout!!.setEnableGesture(false)
    }

    override fun onResume() {
        super.onResume()
        eventBus.register(this)
        invalidateOptionsMenu()
        initView()
        setUpDrawer()
        routeShortcuts()
        supportFragmentManager.addOnBackStackChangedListener((backStackListener)!!)
    }

    private fun routeShortcuts() {
        if (CAApp.ROUTING != null) {
            var i: Intent? = null
            when (CAApp.ROUTING) {
                ShortcutRoutes.ENCYCLOPEDIA -> i = Intent(
                    applicationContext, EncyclopediaTabbedActivity::class.java
                )

                ShortcutRoutes.SEARCH -> i = Intent(applicationContext, SearchActivity::class.java)
                ShortcutRoutes.TWITCH -> {
                    i = Intent(applicationContext, ResourcesActivity::class.java)
                    i.putExtra(ResourcesActivity.EXTRA_TYPE, ResourcesActivity.EXTRA_TWITCH)
                }

                ShortcutRoutes.AD_LAUNCH -> requestNewInterstitial()
                null -> TODO()
            }
            if (i != null) {
                startActivity(i)
            }
            CAApp.ROUTING = null
        }
    }

    private fun initView() {
        drawer!!.setSelection(-1, false)
        val pref = Prefs(applicationContext)
        var launchCount = pref.getInt(CAApp.LAUNCH_COUNT, 0)
        val currentFrag = supportFragmentManager.findFragmentById(R.id.container)
        selectedId = getSelectedId(applicationContext)
        val captains: Map<String?, Captain?>? = getCaptains(
            applicationContext
        )
        val captain = captains!![selectedId]
        if (captain != null) {
            //else if player show that fragment
            setCaptainTitle(captain)

            selectedId = createCapIdStr(captain.server, captain.id)
            var switchFragment = false
            if (currentFrag == null) {
                switchFragment = true
            } else if (currentFrag !is ViewCaptainTabbedFragment) {
                switchFragment = true
            }

            if (switchFragment) {
                try {
                    val fragment = ViewCaptainTabbedFragment()
                    val trans = supportFragmentManager.beginTransaction()
                    trans.replace(R.id.container, fragment).commit()
                } catch (e: IllegalStateException) {
                } catch (e: Exception) {
                }
            }
            if (captain.ships == null || FORCE_REFRESH) {
                eventBus.post(ProgressEvent(true))
                FORCE_REFRESH = false
                val connected = hasInternetConnection(this)
                if (connected) {
                    val useLogin =
                        Prefs(applicationContext).getBoolean(SettingActivity.LOGIN_USER, false)
                    if (useLogin) {
                        val info = getAuthInfo(applicationContext)
                        val sameUser = (captain.name == info.username)
                        if (sameUser && !info.isExpired) {
                            grabCaptain(captain, info)
                        } else {
                            // TODO: disabled for now
//                            val t = Intent(applicationContext, AuthenticationActivity::class.java)
//                            startActivity(t)
//                            FORCE_REFRESH = true
                        }
                    } else {
                        grabCaptain(captain, null)
                    }
                } else {
                    generalNoInternetDialogAlert(
                        this,
                        getString(R.string.no_internet_title),
                        getString(R.string.no_internet_message),
                        getString(R.string.no_internet_neutral_text)
                    )
                }
            } else {
//                setUpKarma(captain);
            }
        } else {
            //if no player selected, show default
            launchCount = showDefaultScreen(pref, launchCount)
        }
        showDialogs(launchCount)
    }

    private fun setCaptainTitle(captain: Captain) {
        val sb = StringBuilder()
        if (!TextUtils.isEmpty(captain.clanName)) {
            sb.append("[" + captain.clanName + "] ")
        }
        sb.append(captain.name)
        title = sb.toString()
    }

    private fun showDialogs(launchCount: Int) {
        val prefs = Prefs(this)
        val hasUserLearnedDrawer = prefs.getBoolean(HAS_LEARNED_ABOUT_DRAWER, false)
        if (hasUserLearnedDrawer) {
            if (launchCount > 2) {
                if (launchCount % 5 == 0 && !CAApp.HAS_SHOWN_FIRST_DIALOG) {
                    createDonationDialog(this)
                }

                if (launchCount % 8 == 0 && !CAApp.HAS_SHOWN_FIRST_DIALOG) {
                    createReviewDialog(this)
                }

                if (launchCount % 13 == 0 && !CAApp.HAS_SHOWN_FIRST_DIALOG) {
                    createFollowDialog(this)
                }
            }
        }
    }

    private fun showDefaultScreen(pref: Prefs, launchCount: Int): Int {
        var launchCount = launchCount
        title = getString(R.string.welcome)
        val fragment = DefaultFragment()
        try {
            val trans = supportFragmentManager.beginTransaction()
            trans.replace(R.id.container, fragment).commit()
        } catch (e: Exception) {
        }
        if (launchCount < 2) {
            launchCount++
            pref.setInt(CAApp.LAUNCH_COUNT, launchCount)
            val i = Intent(applicationContext, SearchActivity::class.java)
            startActivity(i)
        }
        return launchCount
    }

    private fun grabCaptain(captain: Captain, info: AuthInfo?) {
        val query = CaptainQuery()
        query.id = captain.id
        query.name = captain.name
        query.server = captain.server
        query.token = if (info != null) info.token else null

        val task = GetCaptainTask()
        task.ctx = applicationContext
        task.execute(query)
    }

    private fun setUpDrawer() {
        val prefs = Prefs(this)
        val hasUserLearnedDrawer = prefs.getBoolean(HAS_LEARNED_ABOUT_DRAWER, false)
        if (drawer == null) {
            drawer = createDrawer(
                this,
                mToolbar,
                Drawer.OnDrawerItemClickListener { view, position, drawerItem ->
                    if (drawerItem != null) {
                        val drawerObj: DrawerChild? = drawerItem.tag as DrawerChild
                        var i: Intent? = null
                        if (drawerObj != null) {
                            when (drawerObj.type) {
                                DrawerType.SEARCH -> i = Intent(
                                    applicationContext, SearchActivity::class.java
                                )

                                DrawerType.CAPTAIN -> {
                                    val server = drawerObj.server
                                    val captainName = drawerObj.title
                                    val captains: Map<String?, Captain?>? = getCaptains(
                                        applicationContext
                                    )
                                    val caps = captains!!.values
                                    for (captain: Captain? in caps) {
                                        if ((captain!!.name == captainName) && captain.server.ordinal == server!!.ordinal) {
                                            i = Intent(
                                                applicationContext,
                                                ViewCaptainActivity::class.java
                                            )
                                            i.putExtra(ViewCaptainActivity.EXTRA_ID, captain.id)
                                            i.putExtra(
                                                ViewCaptainActivity.EXTRA_SERVER,
                                                captain.server.toString()
                                            )
                                            i.putExtra(ViewCaptainActivity.EXTRA_NAME, captain.name)
                                            break
                                        }
                                    }
                                }

                                DrawerType.SHIPOPEDIA -> i = Intent(
                                    applicationContext,
                                    EncyclopediaTabbedActivity::class.java
                                )

                                DrawerType.WEBSITES_TOOLS -> {
                                    i = Intent(applicationContext, ResourcesActivity::class.java)
                                    i.putExtra(
                                        ResourcesActivity.EXTRA_TYPE,
                                        ResourcesActivity.EXTRA_WEBSITES_TOOLS
                                    )
                                }

                                DrawerType.TWITCH -> {
                                    i = Intent(applicationContext, ResourcesActivity::class.java)
                                    i.putExtra(
                                        ResourcesActivity.EXTRA_TYPE,
                                        ResourcesActivity.EXTRA_TWITCH
                                    )
                                }

                                DrawerType.DONATE -> {
                                    i = Intent(applicationContext, ResourcesActivity::class.java)
                                    i.putExtra(
                                        ResourcesActivity.EXTRA_TYPE,
                                        ResourcesActivity.EXTRA_DONATE
                                    )
                                }

                                DrawerType.SETTINGS -> i =
                                    Intent(applicationContext, SettingActivity::class.java)

                                DrawerType.SERVER -> {
                                    i = Intent(applicationContext, ResourcesActivity::class.java)
                                    i.putExtra(
                                        ResourcesActivity.EXTRA_TYPE,
                                        ResourcesActivity.EXTRA_SERVERS
                                    )
                                }

                                DrawerType.YOUTUBERS -> TODO()
                                null -> TODO()
                            }
                            if (i != null) {
                                CAApp.lastShipPos = 0
                                startActivity(i)
                                drawer!!.closeDrawer()
                            }
                        }
                    }
                    true
                }
            )
            if (!hasUserLearnedDrawer && !drawer!!.isDrawerOpen) {
                drawer!!.openDrawer()
                prefs.setBoolean(HAS_LEARNED_ABOUT_DRAWER, true)
            }
        } else {
            drawer!!.removeAllItems()
            drawer!!.addItems(*getDrawerItemList(applicationContext))
        }
    }


    override fun onPause() {
        super.onPause()
        eventBus.unregister(this)
        supportFragmentManager.removeOnBackStackChangedListener((backStackListener)!!)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (supportFragmentManager.findFragmentById(R.id.container) is ViewCaptainTabbedFragment) {
            menuInflater.inflate(R.menu.menu_main, menu)
            val selected_id = getSelectedId(applicationContext)
            if (selected_id != null) {
                menu.getItem(NumberVault.MENU_REFRESH).setVisible(true) // show refresh

                menu.getItem(NumberVault.MENU_SAVE)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
                menu.getItem(NumberVault.MENU_SAVE).setIcon(R.drawable.ic_action_trash)
                menu.getItem(NumberVault.MENU_SAVE).setTitle(R.string.delete_captain)

                menu.getItem(NumberVault.MENU_BOOKMARK).setVisible(true) // shows unbookmark
                menu.getItem(NumberVault.MENU_BOOKMARK).setTitle(R.string.menu_unfavorite)
                menu.getItem(NumberVault.MENU_BOOKMARK).setIcon(R.drawable.ic_drawer_favorite)

                menu.getItem(NumberVault.MENU_VIEW_AD).setVisible(true)
            } else {
                menu.getItem(NumberVault.MENU_SAVE).setVisible(false)
            }
            menu.getItem(NumberVault.MENU_CAPTAINS).setVisible(false)
        } else {
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_refresh) {
            FORCE_REFRESH = true
            eventBus.post(RefreshEvent(false))
            initView()
        } else if (id == R.id.action_bookmark) {
            setSelectedId(applicationContext, null)
            selectedId = null
            initView()
            invalidateOptionsMenu()
        } else if (id == R.id.action_save) {
            val captains: Map<String?, Captain?>? = getCaptains(
                applicationContext
            )
            val captain = captains!![selectedId]
            if (captain != null) {
                val event = AddRemoveEvent()
                event.captain = captain
                event.isRemove = true
                Toast.makeText(
                    applicationContext,
                    captain.name + " " + getString(R.string.list_clan_removed_message),
                    Toast.LENGTH_SHORT
                ).show()
                eventBus.post(event)
            }
        } else if (id == R.id.action_view_ad) {
            val i = Intent(applicationContext, ResourcesActivity::class.java)
            i.putExtra(ResourcesActivity.EXTRA_TYPE, ResourcesActivity.EXTRA_DONATE)
            i.putExtra(ResourcesActivity.EXTRA_VIEW_AD, true)
            startActivity(i)
        } else if (id == R.id.action_warships_today) {
            val captain2 = getCaptain(applicationContext)
            if (captain2 != null) {
                val url =
                    "http://" + captain2.server.warshipsToday + ".warships.today/player/" + captain2.id + "/" + captain2.name
                val i2 = Intent(Intent.ACTION_VIEW)
                i2.setData(Uri.parse(url))
                startActivity(i2)
            }
        } else {
            throw IllegalStateException("Unexpected value: $id")
        }

        return super.onOptionsItemSelected(item)
    }

    @Subscribe
    fun onRefresh(event: RefreshEvent) {
        if (event.isFromSwipe) {
            FORCE_REFRESH = true
            initView()
        }
    }

    @Subscribe
    fun onRecieveCaptain(result: CaptainResult?) {
        if (result != null) {
            mToolbar!!.post(object : Runnable {
                override fun run() {
                    val captain = result.captain
                    if (captain != null && !result.isHidden) {
                        wtf("ViewCaptain", "id = $selectedId captain = $captain")
                        if ((createCapIdStr(captain.server, captain.id) == selectedId)) {
                            val prefs = Prefs(applicationContext)
                            prefs.setString(CaptainShipsFragment.SAVED_SORT, "Battle")
                            saveCaptain(applicationContext, captain)
                            setCaptainTitle(captain)
                            savePlayerStats(mToolbar!!.context, captain)
                            eventBus.post(CaptainReceivedEvent())
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            if (result.isHidden) getString(R.string.player_private) else getString(R.string.failure_in_loading),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
    }

    override fun getCaptain(ctx: Context?): Captain? {
        return getCaptains(applicationContext)!![selectedId]
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentById(R.id.container) is ViewCaptainTabbedFragment) {
            if (!drawer!!.isDrawerOpen) {
                drawer!!.openDrawer()
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    @Subscribe
    fun onAddRemove(event: AddRemoveEvent) {
        if (!event.isRemove) {
            createBookmarkingDialogIfNeeded(this, (event.captain)!!)
            saveCaptain(applicationContext, getCaptain(applicationContext))
        } else {
            removeCaptain(
                applicationContext, createCapIdStr(
                    event.captain!!.server, event.captain!!.id
                )
            )
            setSelectedId(applicationContext, null)
            selectedId = null
            initView()
        }
        invalidateOptionsMenu()
        setUpDrawer()
    }

    @Subscribe
    fun showShip(ship: ShipClickedEvent?) {
        if (ship != null) {
            val shipFragment = ShipFragment()
            shipFragment.setId(ship.id)
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.anim_slide_in_left,
                    R.anim.anim_slide_out_left,
                    R.anim.anim_slide_in_right,
                    R.anim.anim_slide_out_right
                )
                .replace(R.id.container, shipFragment)
                .addToBackStack("ship")
                .commit()
        }
    }

    private fun requestNewInterstitial() {
    }

    companion object {
        val HAS_LEARNED_ABOUT_DRAWER: String = "has_learned_about_drawer"
    }
}
