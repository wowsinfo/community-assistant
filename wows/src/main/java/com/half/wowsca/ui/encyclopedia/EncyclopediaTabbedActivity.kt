package com.half.wowsca.ui.encyclopedia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.isOceanTheme
import com.half.wowsca.R
import com.half.wowsca.managers.CompareManager.clearShips
import com.half.wowsca.managers.CompareManager.getSHIPS
import com.half.wowsca.managers.InfoManager
import com.half.wowsca.model.ShipCompareEvent
import com.half.wowsca.ui.CABaseActivity
import com.half.wowsca.ui.adapter.pager.ShipopediaPager
import com.half.wowsca.ui.compare.ShipCompareActivity
import com.utilities.views.SlidingTabLayout
import com.utilities.views.SwipeBackLayout
import org.greenrobot.eventbus.Subscribe

/**
 * Created by slai4 on 10/31/2015.
 */
class EncyclopediaTabbedActivity : CABaseActivity() {
    private var mViewPager: ViewPager? = null
    private var pagerTabs: SlidingTabLayout? = null
    private var pager: ShipopediaPager? = null

    private var bClear: View? = null
    private var bCompare: View? = null

    private var aTabs: View? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encyclopedia)
        bindView()
    }

    private fun bindView() {
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar?
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        mViewPager = findViewById<View>(R.id.encyclopedia_pager) as ViewPager?
        pager = ShipopediaPager(supportFragmentManager)
        val iconResourceArray = arrayOf(
            R.drawable.ic_encyclopedia,
            R.drawable.ic_stats, R.drawable.ic_captain_skills,
            R.drawable.ic_flags, R.drawable.ic_upgrade
        )
        pagerTabs = findViewById<View>(R.id.encyclopedia_pager_tab) as SlidingTabLayout?
        pagerTabs!!.iconResourceArray = iconResourceArray
        pagerTabs!!.setIconTintColor(0)

        var indicatorColor = R.color.selected_tab_color
        if (!isOceanTheme(applicationContext)) indicatorColor = R.color.material_primary
        title = ""

        aTabs = findViewById<View>(R.id.encyclopedia_tabs)
        bClear = findViewById<View>(R.id.encyclopedia_clear)
        bCompare = findViewById<View>(R.id.encyclopedia_compare)

        pagerTabs!!.setSelectedIndicatorColors(
            ContextCompat.getColor(
                applicationContext,
                indicatorColor
            )
        )
        pagerTabs!!.setDistributeEvenly(true)
        pagerTabs!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        mViewPager!!.adapter = pager
        pagerTabs!!.setViewPager(mViewPager)

        swipeBackLayout!!.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT)
    }

    override fun onResume() {
        super.onResume()
        eventBus.register(this)
        toggleTopArea()
        setUpButtons()
    }

    override fun onPause() {
        super.onPause()
        eventBus.unregister(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!getSHIPS()!!.isEmpty()) {
            clearItems()
        } else super.onBackPressed()
    }

    @Subscribe
    fun onShipCompare(event: ShipCompareEvent?) {
        toggleTopArea()
    }

    private fun setUpButtons() {
        bCompare!!.setOnClickListener {
            if (getSHIPS()!!.size > 1) {
                val ctx: Context = this@EncyclopediaTabbedActivity
                val builder = AlertDialog.Builder(ctx)
                builder.setTitle("Compare these ships?")

                val info = InfoManager().getShipInfo(applicationContext)
                val sb = StringBuilder()
                for (id in getSHIPS()!!) {
                    val name = info[id]!!.name
                    sb.append(name)
                    sb.append("\n")
                }
                builder.setMessage(sb.toString())

                builder.setPositiveButton(R.string.compare) { dialog, which ->
                    clearShips(false)
                    val i = Intent(applicationContext, ShipCompareActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                    dialog.dismiss()
                }

                builder.setNeutralButton(R.string.no) { dialog, which -> dialog.dismiss() }

                builder.setNegativeButton(R.string.clear_list) { dialog, which ->
                    clearItems()
                    dialog.dismiss()
                }
                builder.show()
            } else {
                Toast.makeText(
                    applicationContext,
                    R.string.toast_not_enough_ships_to_compare,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        bClear!!.setOnClickListener { clearItems() }
    }

    private fun clearItems() {
        clearShips(true)
        val event = ShipCompareEvent(0)
        event.isCleared = true
        eventBus.post(event)
    }

    private fun toggleTopArea() {
        if (!getSHIPS()!!.isEmpty()) {
            pagerTabs!!.visibility = View.GONE
            bClear!!.visibility = View.VISIBLE
            bCompare!!.visibility = View.VISIBLE
        } else {
            pagerTabs!!.visibility = View.VISIBLE
            bClear!!.visibility = View.GONE
            bCompare!!.visibility = View.GONE
        }
    }
}
