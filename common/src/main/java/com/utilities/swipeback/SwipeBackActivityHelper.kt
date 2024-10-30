package com.utilities.swipeback

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.utilities.views.SwipeBackLayout
import com.utilities.views.SwipeBackLayout.SwipeListener

/**
 * Created by slai4 on 4/17/2016.
 */
class SwipeBackActivityHelper(private val mActivity: Activity, private val resource: Int) {
    var swipeBackLayout: SwipeBackLayout? = null
        private set

    @Suppress("deprecation")
    fun onActivityCreate() {
        mActivity.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mActivity.window.decorView.setBackgroundDrawable(null)
        swipeBackLayout = LayoutInflater.from(mActivity).inflate(
            resource, null
        ) as SwipeBackLayout
        swipeBackLayout!!.addSwipeListener(object : SwipeListener {
            override fun onScrollStateChange(state: Int, scrollPercent: Float) {
            }

            override fun onEdgeTouch(edgeFlag: Int) {
                SwipeBackUtils.convertActivityToTranslucent(mActivity)
            }

            override fun onScrollOverThreshold() {
            }
        })
    }

    fun onPostCreate() {
        swipeBackLayout!!.attachToActivity(mActivity)
    }

    fun findViewById(id: Int): View? {
        if (swipeBackLayout != null) {
            return swipeBackLayout!!.findViewById(id)
        }
        return null
    }
}
