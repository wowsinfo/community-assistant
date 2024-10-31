package com.half.wowsca.ui.compare

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.isOceanTheme
import com.half.wowsca.R
import com.half.wowsca.managers.CompareManager
import com.half.wowsca.managers.CompareManager.addShipInfo
import com.half.wowsca.managers.CompareManager.checkForDone
import com.half.wowsca.managers.CompareManager.getSHIPS
import com.half.wowsca.managers.CompareManager.searchShips
import com.half.wowsca.managers.CompareManager.shipInformation
import com.half.wowsca.model.ProgressEvent
import com.half.wowsca.model.result.ShipResult
import com.half.wowsca.ui.CABaseActivity
import com.half.wowsca.ui.adapter.pager.ShipComparePager
import com.utilities.logging.Dlog.d
import com.utilities.views.SlidingTabLayout
import org.greenrobot.eventbus.Subscribe

/**
 * Created by slai47 on 3/5/2017.
 */
class ShipCompareActivity : CABaseActivity() {
    /**
     * this holds a pager of a ShipCompareDifFragment and the ship profiles
     */
    private var pagerTabs: SlidingTabLayout? = null
    private var mViewPager: ViewPager? = null
    private var pager: ShipComparePager? = null

    private var progress: View? = null

    private var lastUpdated: ShipResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compare_ships)
        bindView()
    }

    private fun bindView() {
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar?
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setTitle(R.string.ship_compare_title)

        progress = findViewById<View>(R.id.activity_compare_ships_progress)
        pagerTabs = findViewById<View>(R.id.ship_compare_pager_tab) as SlidingTabLayout?
        mViewPager = findViewById<View>(R.id.ship_compare_tabbed_pager) as ViewPager?
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
        if (getSHIPS()!!.size == 0) {
            finish()
        } else {
            d("initView", "getSHIPS = " + getSHIPS()!!.size + " si = " + shipInformation!!.size())
            if (getSHIPS()!!.size != shipInformation!!.size()) {
                if (!CompareManager.GRABBING_INFO) {
                    grabInfo()
                } else {
                }
            } else {
                CompareManager.GRABBING_INFO = false
                progress!!.visibility = View.GONE
                setupView()
            }
        }
    }

    private fun setupView() {
        if (pager == null) {
            var indicatorColor = R.color.selected_tab_color
            if (!isOceanTheme(applicationContext)) indicatorColor = R.color.top_background

            val iconResourceArray = arrayOf(
                R.drawable.ic_ship,
                R.drawable.ic_stats
            )
            pagerTabs!!.iconResourceArray = iconResourceArray
            pagerTabs!!.setIconTintColor(0)

            pagerTabs!!.setSelectedIndicatorColors(
                ContextCompat.getColor(
                    applicationContext,
                    indicatorColor
                )
            )
            pagerTabs!!.setDistributeEvenly(true)
            pagerTabs!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            pager = ShipComparePager(supportFragmentManager)
            mViewPager!!.adapter = pager
            pagerTabs!!.setViewPager(mViewPager)
        }
        if (lastUpdated != null) {
            eventBus.post(lastUpdated!!.shipId)
            lastUpdated = null
        }
    }

    private fun grabInfo() {
        progress!!.visibility = View.VISIBLE
        searchShips(applicationContext)
    }

    @Subscribe
    fun onShipRecieveInfo(result: ShipResult) {
        d("onShipReceiveInfo", "result = $result")
        if (result.shipInfo != null && getSHIPS()!!.contains(result.shipId)) {
            addShipInfo(result.shipId, result.shipInfo)
            checkForDone()
            lastUpdated = result
            runOnUiThread { initView() }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_refresh, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        } else if (item.itemId == R.id.action_refresh) {
            //refresh all fragments
            //send out calls again
        }
        return super.onOptionsItemSelected(item)
    }

    @Subscribe
    fun onRefresh(event: ProgressEvent) {
        progress!!.visibility =
            if (event.isRefreshing) View.VISIBLE else View.GONE
    }
}