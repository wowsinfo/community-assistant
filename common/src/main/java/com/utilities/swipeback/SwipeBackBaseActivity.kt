package com.utilities.swipeback

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.half.common.R
import com.utilities.views.SwipeBackLayout

/**
 * Created by slai4 on 4/17/2016.
 */

open class SwipeBackBaseActivity : AppCompatActivity(), ISwipeBack {
    private var mHelper: SwipeBackActivityHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHelper = SwipeBackActivityHelper(this, R.layout.swipe_back_layout)
        mHelper!!.onActivityCreate()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mHelper!!.onPostCreate()
    }

    // TODO: double check this one
    override fun <T : View?> findViewById(@IdRes id: Int): T? {
        val v = super.findViewById<T>(id)
        if (v == null && mHelper != null) return mHelper!!.findViewById(id) as T?
        return v
    }

    override val swipeBackLayout: SwipeBackLayout?
        get() = mHelper!!.swipeBackLayout

    override fun setSwipeBackEnable(enable: Boolean) {
        swipeBackLayout!!.setEnableGesture(enable)
    }

    override fun scrollToFinishActivity() {
        SwipeBackUtils.convertActivityToTranslucent(this)
        swipeBackLayout!!.scrollToFinishActivity()
    }
}