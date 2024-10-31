package com.half.wowsca.ui.adapter.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.half.wowsca.ui.encyclopedia.tabs.CaptainSkillsFragment
import com.half.wowsca.ui.encyclopedia.tabs.EncyclopediaShipsFragment
import com.half.wowsca.ui.encyclopedia.tabs.FlagsFragment
import com.half.wowsca.ui.encyclopedia.tabs.GraphsStatsFragment
import com.half.wowsca.ui.encyclopedia.tabs.UpgradesFragment

/**
 * Created by slai4 on 12/1/2015.
 */
class ShipopediaPager(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        var f: Fragment? = null
        when (position) {
            0 -> f = EncyclopediaShipsFragment()
            1 -> f = GraphsStatsFragment()
            2 -> f = CaptainSkillsFragment()
            3 -> f = FlagsFragment()
            4 -> f = UpgradesFragment()
        }
        return f!!
    }

    override fun getPageTitle(position: Int): CharSequence {
        var title = ""
        when (position) {
            0 -> title = "Encyclopedia"
            1 -> title = "Ship Stats"
            2 -> title = "Captain Skills"
            3 -> title = "Flags"
            4 -> title = "Upgrades"
        }
        return title
    }

    override fun getCount(): Int {
        return 5
    }
}