package com.half.wowsca.ui.viewcaptain

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
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.getSelectedId
import com.half.wowsca.CAApp.Companion.getServerType
import com.half.wowsca.CAApp.Companion.setSelectedId
import com.half.wowsca.NumberVault
import com.half.wowsca.R
import com.half.wowsca.alerts.Alert.generalNoInternetDialogAlert
import com.half.wowsca.backend.GetCaptainTask
import com.half.wowsca.interfaces.ICaptain
import com.half.wowsca.managers.CaptainManager.createCapIdStr
import com.half.wowsca.managers.CaptainManager.deleteTemp
import com.half.wowsca.managers.CaptainManager.fromSearch
import com.half.wowsca.managers.CaptainManager.getCapIdStr
import com.half.wowsca.managers.CaptainManager.getCaptains
import com.half.wowsca.managers.CaptainManager.getTEMP
import com.half.wowsca.managers.CaptainManager.removeCaptain
import com.half.wowsca.managers.CaptainManager.saveCaptain
import com.half.wowsca.managers.CaptainManager.saveTempStoredCaptain
import com.half.wowsca.managers.StorageManager.savePlayerStats
import com.half.wowsca.model.AddRemoveCaptainEvent
import com.half.wowsca.model.AddRemoveEvent
import com.half.wowsca.model.Captain
import com.half.wowsca.model.CaptainReceivedEvent
import com.half.wowsca.model.RefreshEvent
import com.half.wowsca.model.ShipClickedEvent
import com.half.wowsca.model.enums.Server
import com.half.wowsca.model.queries.CaptainQuery
import com.half.wowsca.model.result.CaptainResult
import com.half.wowsca.ui.CABaseActivity
import com.half.wowsca.ui.UIUtils.createBookmarkingDialogIfNeeded
import com.half.wowsca.ui.UIUtils.createCaptainListViewMenu
import com.half.wowsca.ui.viewcaptain.tabs.CaptainShipsFragment
import com.utilities.Utils.hasInternetConnection
import com.utilities.logging.Dlog.wtf
import com.utilities.preferences.Prefs
import com.utilities.views.SwipeBackLayout
import org.greenrobot.eventbus.Subscribe

class ViewCaptainActivity : CABaseActivity(), ICaptain {
    private var id: Long = 0
    private var name: String? = null
    private var server: Server? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindView()
        if (savedInstanceState == null) {
            id = intent.getLongExtra(EXTRA_ID, 0)
            name = intent.getStringExtra(EXTRA_NAME)
            try {
                server = Server.valueOf((intent.getStringExtra(EXTRA_SERVER))!!)
            } catch (e: IllegalArgumentException) {
                server = getServerType(applicationContext)
            }
        } else {
            id = savedInstanceState.getLong(EXTRA_ID)
            name = savedInstanceState.getString(EXTRA_NAME)
            server = Server.valueOf((intent.getStringExtra(EXTRA_SERVER))!!)
        }
    }

    private fun bindView() {
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar?
        setSupportActionBar(mToolbar)
        mToolbar!!.setTitleTextColor(Color.WHITE)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        initBackStackListener()
        swipeBackLayout!!.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT)
    }

    override fun onResume() {
        super.onResume()
        eventBus.register(this)
        initView()
        supportFragmentManager.addOnBackStackChangedListener((backStackListener)!!)
    }

    override fun onPause() {
        super.onPause()
        eventBus.unregister(this)
        supportFragmentManager.removeOnBackStackChangedListener((backStackListener)!!)
    }

    private fun initView() {
        //place fragment in with variables

        val current = supportFragmentManager.findFragmentById(R.id.container)
        if (current == null) {
            val fragment = ViewCaptainTabbedFragment()
            val trans = supportFragmentManager.beginTransaction()
            trans.replace(R.id.container, fragment).commit()
        }
        var captain: Captain? = null
        if (fromSearch(applicationContext, (server)!!, id)) {
            captain = getTEMP(applicationContext)
        } else {
            captain = getCaptains(applicationContext)!![createCapIdStr(server, id)]
        }
        var search = false
        if (captain == null) {
            search = true
        } else if (captain.ships == null) {
            search = true
        }
        if (FORCE_REFRESH) search = true

        setTopTitle(captain)

        wtf(
            "ViewCaptain",
            "captain = " + (captain != null) + " search = " + search + " force = " + FORCE_REFRESH
        )
        val connected = hasInternetConnection(this)
        if (connected) {
            if (search) {
                FORCE_REFRESH = false
                val query = CaptainQuery()
                query.id = id
                query.name = name
                query.server = server
                val task = GetCaptainTask()
                task.ctx = applicationContext
                task.execute(query)
            } else {
            }
        } else {
            generalNoInternetDialogAlert(
                this,
                getString(R.string.no_internet_title),
                getString(R.string.no_internet_message),
                getString(R.string.no_internet_neutral_text)
            )
        }
    }

    private fun setTopTitle(captain: Captain?) {
        if (captain != null) {
            val sb = StringBuilder()
            if (!TextUtils.isEmpty(captain.clanName)) {
                sb.append("[" + captain.clanName + "] ")
            }
            sb.append(captain.name)
            title = sb.toString()
        } else {
            title = name
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(EXTRA_ID, id)
        outState.putString(EXTRA_NAME, name)
        outState.putString(EXTRA_SERVER, server.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (supportFragmentManager.findFragmentById(R.id.container) is ViewCaptainTabbedFragment) {
            menuInflater.inflate(R.menu.menu_main, menu)
            //            if (CaptainManager.getCaptains(getApplicationContext()).size() > 1) {
            menu.getItem(NumberVault.MENU_CAPTAINS).setVisible(false) // shows other captains
            //            }
            menu.getItem(NumberVault.MENU_REFRESH).setVisible(true) // shows refresh
            menu.getItem(NumberVault.MENU_VIEW_AD).setVisible(false)
            if ((createCapIdStr(server, id) == getSelectedId(applicationContext))) {
                menu.getItem(NumberVault.MENU_BOOKMARK).setVisible(true) // shows bookmark
                menu.getItem(NumberVault.MENU_BOOKMARK).setTitle(R.string.menu_unfavorite)
                menu.getItem(NumberVault.MENU_BOOKMARK).setIcon(R.drawable.ic_drawer_favorite)
            } else {
                menu.getItem(NumberVault.MENU_BOOKMARK).setVisible(true) // shows bookmark
                menu.getItem(NumberVault.MENU_BOOKMARK).setTitle(R.string.menu_favorite)
                menu.getItem(NumberVault.MENU_BOOKMARK).setIcon(R.drawable.ic_drawer_not_favorite)
            }
            if (getCaptains(applicationContext)!![createCapIdStr(server, id)] != null) {
                menu.getItem(NumberVault.MENU_SAVE).setIcon(R.drawable.ic_action_trash)
                menu.getItem(NumberVault.MENU_SAVE).setTitle(R.string.delete_captain)
            } else {
                menu.getItem(NumberVault.MENU_SAVE).setIcon(R.drawable.ic_action_save)
                menu.getItem(NumberVault.MENU_SAVE).setTitle(R.string.save_captain)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            onBackPressed()
        } else if (id == R.id.action_refresh) {
            if (fromSearch(applicationContext, (server)!!, id.toLong())) {
                deleteTemp(applicationContext)
            }
            FORCE_REFRESH = true
            eventBus.post(RefreshEvent(false))
            initView()
        } else if (id == R.id.action_captains) {
            createCaptainListViewMenu(this, createCapIdStr(server, this.id))
        } else if (id == R.id.action_bookmark) {
            val selectedId = getSelectedId(applicationContext)
            val event = AddRemoveCaptainEvent()
            val idString = this.id.toString()
            if (createCapIdStr(server, this.id) != selectedId) {
                if (getCaptains(applicationContext)!![idString] == null) {
                    saveCaptain(applicationContext, getCaptain(applicationContext))
                }
                event.isRemoved = false
                setSelectedId(applicationContext, createCapIdStr(server, this.id))
                FORCE_REFRESH = true
                invalidateOptionsMenu()
            } else {
                event.isRemoved = true
                removeCaptain(applicationContext, createCapIdStr(server, this.id))
                setSelectedId(applicationContext, null)
            }
            eventBus.post(event)
        } else if (id == R.id.action_save) {
            val captains: Map<String?, Captain?>? = getCaptains(
                applicationContext
            )
            val captain = getCaptain(applicationContext)
            if (captain != null) {
                val e = AddRemoveEvent()
                e.captain = captain
                if (captains!![getCapIdStr(captain)] == null) {
                    e.isRemove = false
                    Toast.makeText(
                        applicationContext,
                        captain.name + " " + getString(R.string.list_clan_added_message),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    e.isRemove = true
                    Toast.makeText(
                        applicationContext,
                        captain.name + " " + getString(R.string.list_clan_removed_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                eventBus.post(e)
            } else {
                Toast.makeText(applicationContext, R.string.crap_contact_me, Toast.LENGTH_SHORT)
                    .show()
            }
        } else if (id == R.id.action_warships_today) {
            val captain2 = getCaptain(applicationContext)
            if (captain2 != null) {
                val url =
                    "http://" + captain2.server.warshipsToday + ".warships.today/player/" + captain2.id + "/" + captain2.name
                val i2 = Intent(Intent.ACTION_VIEW)
                i2.setData(Uri.parse(url))
                startActivity(i2)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Subscribe
    fun onRefresh(event: RefreshEvent) {
        if (event.isFromSwipe) {
            if (fromSearch(applicationContext, (server)!!, id)) {
                deleteTemp(applicationContext)
            }
            FORCE_REFRESH = true
            initView()
        }
    }

    @Subscribe
    fun onRecieveCaptain(result: CaptainResult?) {
        if (result != null) {
            mToolbar!!.post(Runnable {
                val captain = result.captain
                if (captain != null && !result.isHidden) {
                    if ((getCapIdStr(captain) == createCapIdStr(server, id))) {
                        val prefs = Prefs(applicationContext)
                        prefs.setString(CaptainShipsFragment.SAVED_SORT, "Battle")
                        val captains: Map<String?, Captain?>? = getCaptains(
                            applicationContext
                        )
                        if (captains!![getCapIdStr(captain)] != null) {
                            saveCaptain(mToolbar!!.context, captain)
                            savePlayerStats(mToolbar!!.context, captain)
                        } else {
                            saveTempStoredCaptain(applicationContext, captain)
                        }
                        setTopTitle(captain)
                        eventBus.post(CaptainReceivedEvent())
                    } else {
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        if (result.isHidden) getString(R.string.player_private) else getString(R.string.failure_in_loading),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    override fun getCaptain(ctx: Context?): Captain? {
        var captain: Captain? = null
        if (fromSearch(applicationContext, (server)!!, id)) {
            captain = getTEMP(applicationContext)
        } else {
            captain = getCaptains(applicationContext)!![createCapIdStr(server, id)]
        }
        return captain
    }

    @Subscribe
    fun onAddRemove(event: AddRemoveEvent) {
        if (!event.isRemove) {
            createBookmarkingDialogIfNeeded(this, (event.captain)!!)
            saveCaptain(applicationContext, getCaptain(applicationContext))
        } else {
            removeCaptain(applicationContext, getCapIdStr(event.captain))
            val selectedId = getSelectedId(applicationContext)
            if ((getCapIdStr(event.captain) == selectedId)) {
                setSelectedId(applicationContext, null)
            }
            finish()
        }
        invalidateOptionsMenu()
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

    companion object {
        val EXTRA_ID: String = "id"
        val EXTRA_NAME: String = "name"
        val EXTRA_SERVER: String = "server"
    }
}