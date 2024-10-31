package com.half.wowsca.ui

import android.view.View
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.R
import com.half.wowsca.model.RefreshEvent

/**
 * Created by slai4 on 4/19/2016.
 */
open class CAFragment : Fragment() {
    @JvmField
    protected var mSwipeRefreshLayout: SwipeRefreshLayout? = null

    protected fun bindSwipe(view: View) {
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh)
    }

    protected fun initSwipeLayout() {
        if (mSwipeRefreshLayout != null) mSwipeRefreshLayout!!.setOnRefreshListener {
            eventBus.post(
                RefreshEvent(true)
            )
        }
    }

    protected fun refreshing(refreshing: Boolean) {
        if (mSwipeRefreshLayout != null) mSwipeRefreshLayout!!.isRefreshing = refreshing
    }
}