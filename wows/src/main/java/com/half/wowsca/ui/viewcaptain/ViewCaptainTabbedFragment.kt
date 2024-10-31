package com.half.wowsca.ui.viewcaptain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.half.wowsca.CAApp.Companion.isOceanTheme
import com.half.wowsca.R
import com.half.wowsca.ui.adapter.pager.ViewCaptainPager
import com.utilities.views.SlidingTabLayout

/**
 * A placeholder fragment containing a simple view.
 */
class ViewCaptainTabbedFragment : Fragment() {
    private var mViewPager: ViewPager? = null
    private lateinit var pagerTabs: SlidingTabLayout
    private var pager: ViewCaptainPager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_captain, container, false)
        bindView(view)
        return view
    }

    private fun bindView(view: View) {
        //set up pager and info
        mViewPager = view.findViewById(R.id.fragment_view_captain_tabbed_pager)
        pagerTabs = view.findViewById(R.id.fragment_view_captain_pager_tab)
        val iconResourceArray = arrayOf(
            R.drawable.ic_captain,
            R.drawable.ic_stats, R.drawable.ic_trophy, R.drawable.ic_star, R.drawable.ic_medal,
            R.drawable.ic_ship
        )
        pagerTabs.iconResourceArray = iconResourceArray
        pagerTabs.setIconTintColor(0)

        var indicatorColor = R.color.selected_tab_color
        if (!isOceanTheme(view.context)) indicatorColor = R.color.top_background

        pagerTabs.setSelectedIndicatorColors(ContextCompat.getColor(requireContext(), indicatorColor))
        pagerTabs.setDistributeEvenly(true)
        pagerTabs.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        pager = ViewCaptainPager(childFragmentManager)
        mViewPager?.setAdapter(pager)
        pagerTabs.setViewPager(mViewPager)
    }

    fun fix() {
        if (mViewPager != null) {
            val item = mViewPager!!.currentItem
            pager = ViewCaptainPager(childFragmentManager)
            mViewPager!!.adapter = pager
            mViewPager!!.currentItem = item
        }
    }
}
