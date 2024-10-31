package com.half.wowsca.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.half.wowsca.R
import com.half.wowsca.managers.CARatingManager
import com.utilities.views.SwipeBackLayout

class InformationActivity : CABaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)

        //set up action bar
        val bar = findViewById<View>(R.id.toolbar) as Toolbar?
        setSupportActionBar(bar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        swipeBackLayout!!.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT)
    }

    override fun onResume() {
        super.onResume()
        val individual = findViewById<View>(R.id.info_per_ship) as TextView?
        individual!!.text = getString(
            R.string.ca_rating_explanation,
            Math.round(CARatingManager.DAMAGE_COEF * 100).toString() + "%",
            Math.round(CARatingManager.KILLS_COEF * 100).toString() + "%",
            Math.round(CARatingManager.WR_COEF * 100).toString() + "%"
        )

        val overall = findViewById<View>(R.id.info_overall) as TextView?
        overall!!.text = getString(R.string.ca_rating_overall_explanation)

        val reasons = findViewById<View>(R.id.info_reasons) as TextView?
        reasons!!.text = getString(
            R.string.ca_rating_reason,
            Math.round(CARatingManager.DAMAGE_COEF * 100).toString() + "%",
            Math.round(CARatingManager.KILLS_COEF * 100).toString() + "%",
            Math.round(CARatingManager.WR_COEF * 100).toString() + "%"
        )
    }
}
