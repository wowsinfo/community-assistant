package com.half.wowsca.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import com.half.wowsca.CAApp.Companion.getAppLanguage
import com.half.wowsca.CAApp.Companion.isDarkTheme
import com.half.wowsca.CAApp.Companion.setTheme
import com.half.wowsca.R
import com.half.wowsca.ui.viewcaptain.ViewCaptainTabbedFragment
import com.utilities.swipeback.SwipeBackBaseActivity
import java.util.Locale

/**
 * Created by slai4 on 4/17/2016.
 */
open class CABaseActivity : SwipeBackBaseActivity() {
    @JvmField
    protected var mToolbar: Toolbar? = null

    @JvmField
    protected var backStackListener: FragmentManager.OnBackStackChangedListener? = null

    //    protected ImageView ivKarma;
    //    protected TextView tvKarma;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(this)
        val current = getAppLanguage(applicationContext)
        val myLocale = Locale(current)
        Locale.setDefault(myLocale)
        val config = Configuration()
        config.locale = myLocale
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )
    }

    override fun onResume() {
        super.onResume()
        if (mToolbar != null) if (isDarkTheme(applicationContext)) mToolbar!!.popupTheme =
            R.style.WoWSCAThemeToolbarDarkOverflow
    }

    protected fun initBackStackListener() {
        backStackListener = FragmentManager.OnBackStackChangedListener {
            invalidateOptionsMenu()
            val current = supportFragmentManager.findFragmentById(R.id.container)
            try {
                if (current is ViewCaptainTabbedFragment) {
                    current.fix()
                }
            } catch (e: Exception) {
            }
        }
    } //    public void setUpKarma(Captain captain){
    //        if(captain != null){
    //            int karma = captain.getDetails().getKarma();
    //            ivKarma.clearColorFilter();
    //            ivKarma.setImageBitmap(null);
    //            ivKarma.setVisibility(View.VISIBLE);
    //            int color = CAApp.isOceanTheme(getApplicationContext()) ? ContextCompat.getColor(getApplicationContext(), R.color.toolbar_text_color) : ContextCompat.getColor(getApplicationContext(), R.color.average_up);
    //            if(karma > 3){
    //                tvKarma.setTextColor(CAApp.isOceanTheme(getApplicationContext()) ? ContextCompat.getColor(getApplicationContext(), R.color.toolbar_text_color) : ContextCompat.getColor(getApplicationContext(), R.color.average_up));
    //                if(karma <= 12){
    //                    ivKarma.setImageResource(R.drawable.ic_thumbs_up);
    //                    ivKarma.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    //                } else if(karma <= 25){
    //                    ivKarma.setImageResource(R.drawable.ic_thumbs_up_2);
    //                    ivKarma.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    //                } else if(karma <= 50){
    //                    ivKarma.setImageResource(R.drawable.ic_heart);
    //                    ivKarma.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    //                } else {
    //                    ivKarma.setImageResource(R.drawable.ic_angel);
    //                    ivKarma.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    //                }
    //            } else if(karma < -3){
    //                tvKarma.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.average_down));
    //                if(karma >= -8){
    //                    ivKarma.setImageResource(R.drawable.ic_thumbs_down);
    //                } else if(karma >= -16){
    //                    ivKarma.setImageResource(R.drawable.ic_devil);
    //                } else {
    //                    ivKarma.setImageResource(R.drawable.ic_danger);
    //                }
    //            } else {
    //                ivKarma.setVisibility(View.GONE);
    //                tvKarma.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
    //            }
    //            if (karma != 0) {
    //                tvKarma.setVisibility(View.VISIBLE);
    //                tvKarma.setText((karma > 0 ? "+" : "") + karma);
    //            } else {
    //                tvKarma.setVisibility(View.GONE);
    //            }
    //        } else {
    //            ivKarma.setVisibility(View.GONE);
    //            tvKarma.setVisibility(View.GONE);
    //        }
    //    }

    companion object {
        var FORCE_REFRESH: Boolean = false
    }
}