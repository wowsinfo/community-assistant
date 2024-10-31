package com.half.wowsca.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 * Created by slai4 on 10/1/2015.
 */
class SquareRelativeLayout : RelativeLayout {
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}
