package com.utilities.swipeback

import com.utilities.views.SwipeBackLayout

/**
 * Created by slai4 on 4/17/2016.
 */
interface ISwipeBack {
    /**
     * @return the SwipeBackLayout associated with this activity.
     */
    val swipeBackLayout: SwipeBackLayout?

    fun setSwipeBackEnable(enable: Boolean)

    /**
     * Scroll out contentView and finish the activity
     */
    fun scrollToFinishActivity()
}
