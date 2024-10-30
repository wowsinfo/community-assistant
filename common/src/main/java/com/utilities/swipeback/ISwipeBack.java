package com.utilities.swipeback;

import com.utilities.views.SwipeBackLayout;

/**
 * Created by slai4 on 4/17/2016.
 */
public interface ISwipeBack {
    /**
     * @return the SwipeBackLayout associated with this activity.
     */
    SwipeBackLayout getSwipeBackLayout();

    void setSwipeBackEnable(boolean enable);

    /**
     * Scroll out contentView and finish the activity
     */
    void scrollToFinishActivity();

}
