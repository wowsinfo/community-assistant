package com.utilities.views

import android.R
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.SparseArray
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.utilities.views.SlidingTabLayout.TabColorizer

/*
* Copyright 2014 Google Inc. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


/**
 * To be used with ViewPager to provide a tab indicator component which give constant feedback as to
 * the user's scroll progress.
 *
 *
 * To use the component, simply add it to your view hierarchy. Then in your
 * [android.app.Activity] or [Fragment] call
 * [.setViewPager] providing it the ViewPager this layout is being used for.
 *
 *
 * The colors can be customized in two ways. The first and simplest is to provide an array of colors
 * via [.setSelectedIndicatorColors]. The
 * alternative is via the  interface which provides you complete control over
 * which color is used for any individual position.
 *
 *
 * The views used as tabs can be customized by calling [.setCustomTabView],
 * providing the layout ID of your custom layout.
 */
class SlidingTextTabLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : HorizontalScrollView(context, attrs, defStyle) {
    private val mTitleOffset: Int
    private val mContentDescriptions = SparseArray<String?>()
    private val mTabStrip: SlidingTabStrip
    var iconResourceArray: Array<Int> = arrayOf()
    private var mTabViewLayoutId = 0
    private var mTabViewTextViewId = 0
    private var mDistributeEvenly = false
    private var textColor = 0
    private var tintColor = 0
    private var mViewPager: ViewPager? = null
    private var mViewPagerPageChangeListener: OnPageChangeListener? = null

    init {
        // Disable the Scroll Bar
        isHorizontalScrollBarEnabled = false
        // Make sure that the Tab Strips fills this View
        isFillViewport = true

        mTitleOffset = (TITLE_OFFSET_DIPS * resources.displayMetrics.density).toInt()

        mTabStrip = SlidingTabStrip(context)
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    /**
     *
     *
     * If you only require simple custmisation then you can use
     * [.setSelectedIndicatorColors] to achieve
     * similar effects.
     */
    fun setCustomTabColorizer(tabColorizer: TabColorizer?) {
        mTabStrip.setCustomTabColorizer(tabColorizer)
    }

    fun setDistributeEvenly(distributeEvenly: Boolean) {
        mDistributeEvenly = distributeEvenly
    }

    /**
     * Sets the colors to be used for indicating the selected tab. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    fun setSelectedIndicatorColors(vararg colors: Int) {
        mTabStrip.setSelectedIndicatorColors(*colors)
    }

    /**
     * Set the [ViewPager.OnPageChangeListener]. When using [SlidingTextTabLayout] you are
     * required to set any [ViewPager.OnPageChangeListener] through this method. This is so
     * that the layout can update it's scroll position correctly.
     *
     * @see ViewPager.setOnPageChangeListener
     */
    fun setOnPageChangeListener(listener: OnPageChangeListener?) {
        mViewPagerPageChangeListener = listener
    }

    /**
     * Set the custom layout to be inflated for the tab views.
     *
     * @param layoutResId Layout id to be inflated
     * @param textViewId  id of the [TextView] in the inflated view
     */
    fun setCustomTabView(layoutResId: Int, textViewId: Int) {
        mTabViewLayoutId = layoutResId
        mTabViewTextViewId = textViewId
    }

    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    fun setViewPager(viewPager: ViewPager?) {
        mTabStrip.removeAllViews()

        mViewPager = viewPager
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(InternalViewPagerListener())
            populateTabStrip()
        }
    }

    /**
     * Create a default view to be used for tabs. This is called if a custom tab view is not set via
     * [.setCustomTabView].
     */
    protected fun createDefaultTabView(context: Context?): TextView {
        val textView = TextView(context)
        textView.gravity = Gravity.CENTER
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP.toFloat())
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val outValue = TypedValue()
        getContext().theme.resolveAttribute(
            R.attr.selectableItemBackground,
            outValue, true
        )
        textView.setBackgroundResource(outValue.resourceId)
        textView.isAllCaps = true

        textView.setTextColor(textColor)

        val padding = (TAB_VIEW_PADDING_DIPS * resources.displayMetrics.density).toInt()
        textView.setPadding(padding, padding, padding, padding)

        return textView
    }

    private fun populateTabStrip() {
        val adapter = mViewPager!!.adapter
        val tabClickListener: OnClickListener = TabClickListener()

        for (i in 0 until adapter!!.count) {
            var tabView: View? = null
            val tabImageView: ImageView? = null

            //            if (mTabViewLayoutId != 0) {
            // If there is a custom tab view layout id set, try and inflate it
//            tabView = LayoutInflater.from(getContext()).inflate(R.layout.tab_layout, mTabStrip,
//                    false);
//            tabImageView = (ImageView) tabView.findViewById(R.id.tab_layout_icon);
//            }
            tabView = createDefaultTabView(context)

            //            if (tabImageView == null && TextView.class.isInstance(tabView)) {
//                tabImageView = (TextView) tabView;
//            }
//            tabImageView.setImageResource(getIconResourceArray()[i]);
//            if(tintColor != 0){
//                tabView.setColorFilter(tintColor, PorterDuff.Mode.MULTIPLY);
//            }
            if (mDistributeEvenly) {
                val lp = tabView.getLayoutParams() as LinearLayout.LayoutParams
                lp.width = 0
                lp.weight = 1f
            }

            (tabView as TextView?)!!.text = adapter.getPageTitle(i)
            tabView.setOnClickListener(tabClickListener)
            val desc = mContentDescriptions[i, null]
            if (desc != null) {
                tabView.setContentDescription(desc)
            }

            mTabStrip.addView(tabView)
            if (i == mViewPager!!.currentItem) {
                tabView.setSelected(true)
            }
        }
    }

    fun setContentDescription(i: Int, desc: String?) {
        mContentDescriptions.put(i, desc)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (mViewPager != null) {
            scrollToTab(mViewPager!!.currentItem, 0)
        }
    }

    private fun scrollToTab(tabIndex: Int, positionOffset: Int) {
        val tabStripChildCount = mTabStrip.childCount
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return
        }

        val selectedChild = mTabStrip.getChildAt(tabIndex)
        if (selectedChild != null) {
            var targetScrollX = selectedChild.left + positionOffset

            if (tabIndex > 0 || positionOffset > 0) {
                // If we're not at the first child and are mid-scroll, make sure we obey the offset
                targetScrollX -= mTitleOffset
            }

            scrollTo(targetScrollX, 0)
        }
    }

    fun setTextColor(textColor: Int) {
        this.textColor = textColor
    }

    fun setIconTintColor(tintColor: Int) {
        this.tintColor = tintColor
    }

    private inner class InternalViewPagerListener : OnPageChangeListener {
        private var mScrollState = 0

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            val tabStripChildCount = mTabStrip.childCount
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return
            }

            mTabStrip.onViewPagerPageChanged(position, positionOffset)

            val selectedTitle = mTabStrip.getChildAt(position)
            val extraOffset = if ((selectedTitle != null)
            ) (positionOffset * selectedTitle.width).toInt() else 0
            scrollToTab(position, extraOffset)

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener!!.onPageScrolled(
                    position, positionOffset,
                    positionOffsetPixels
                )
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            mScrollState = state

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener!!.onPageScrollStateChanged(state)
            }
        }

        override fun onPageSelected(position: Int) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f)
                scrollToTab(position, 0)
            }
            for (i in 0 until mTabStrip.childCount) {
                mTabStrip.getChildAt(i).isSelected = position == i
            }
            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener!!.onPageSelected(position)
            }
        }
    }

    private inner class TabClickListener : OnClickListener {
        override fun onClick(v: View) {
            for (i in 0 until mTabStrip.childCount) {
                if (v === mTabStrip.getChildAt(i)) {
                    mViewPager!!.currentItem = i
                    return
                }
            }
        }
    }

    companion object {
        private const val TITLE_OFFSET_DIPS = 24
        private const val TAB_VIEW_PADDING_DIPS = 16
        private const val TAB_VIEW_TEXT_SIZE_SP = 12
    }
}