package com.half.wowsca.ui.adapter.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.half.wowsca.ui.compare.ShipCompareDifFragment
import com.half.wowsca.ui.compare.ShipCompareGraphFragment
import com.half.wowsca.ui.compare.ShipModuleListFragment

/**
 * Created by slai47 on 3/12/2017.
 */
class ShipComparePager(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        var frag: Fragment? = null
        frag = if (position == 0) {
            ShipModuleListFragment()
        } else if (position == 1) {
            ShipCompareGraphFragment()
        } else {
            ShipCompareDifFragment()
        }
        return frag
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return null
    }
}