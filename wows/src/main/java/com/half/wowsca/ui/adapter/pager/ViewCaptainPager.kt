package com.half.wowsca.ui.adapter.pager

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.half.wowsca.ui.viewcaptain.tabs.CaptainAchievementsFragment
import com.half.wowsca.ui.viewcaptain.tabs.CaptainFragment
import com.half.wowsca.ui.viewcaptain.tabs.CaptainGraphsFragment
import com.half.wowsca.ui.viewcaptain.tabs.CaptainRankedFragment
import com.half.wowsca.ui.viewcaptain.tabs.CaptainShipsFragment
import com.half.wowsca.ui.viewcaptain.tabs.CaptainTopShipInfoFragment

/**
 * Created by slai4 on 9/15/2015.
 */
class ViewCaptainPager(fm: FragmentManager?) : FragmentStatePagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        var f: Fragment? = null
        when (position) {
            0 -> f = CaptainFragment()
            1 -> f = CaptainGraphsFragment()
            2 -> f = CaptainTopShipInfoFragment()
            3 -> f = CaptainRankedFragment()
            4 -> f = CaptainAchievementsFragment()
            5 -> f = CaptainShipsFragment()
        }
        return f!!
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title = ""
        when (position) {
            0 -> title = "Overall"
            1 -> title = "Top Ship Info"
            2 -> title = "Details"
            3 -> title = "Ranked & Leaderboards"
            4 -> title = "Medals"
            5 -> title = "Ships"
        }
        return title
    }

    override fun getCount(): Int {
        return 6
    }

    override fun saveState(): Parcelable? {
        return null
    }
}
