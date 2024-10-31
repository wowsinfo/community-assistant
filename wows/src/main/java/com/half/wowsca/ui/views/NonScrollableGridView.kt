package com.half.wowsca.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.GridView

/**
 * Created by Harrison on 6/20/2015.
 */

/**
 * Created by hf on 6/8/15.
 */
class NonScrollableGridView(context: Context?, attrs: AttributeSet?) : GridView(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Do not use the highest two bits of Integer.MAX_VALUE because they are
        // reserved for the MeasureSpec mode
        val heightSpec = MeasureSpec.makeMeasureSpec(Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, heightSpec)
        layoutParams.height = measuredHeight
    }
}
