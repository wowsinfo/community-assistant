package com.half.wowsca.ui.compare

import android.os.Bundle
import android.text.TextUtils
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.CAApp.Companion.isDarkTheme
import com.half.wowsca.R
import com.half.wowsca.managers.CompareManager.captainsHaveInfo
import com.half.wowsca.managers.CompareManager.getCaptains
import com.half.wowsca.managers.CompareManager.overrideCaptain
import com.half.wowsca.managers.CompareManager.search
import com.half.wowsca.managers.CompareManager.size
import com.half.wowsca.model.Captain
import com.half.wowsca.model.CaptainDetails
import com.half.wowsca.model.Ship
import com.half.wowsca.model.result.CaptainResult
import com.half.wowsca.ui.CABaseActivity
import com.half.wowsca.ui.SettingActivity
import com.utilities.Utils.defaultDecimalFormatter
import com.utilities.Utils.oneDepthDecimalFormatter
import com.utilities.preferences.Prefs
import com.utilities.views.SwipeBackLayout
import org.greenrobot.eventbus.Subscribe
import java.text.DecimalFormat

class CompareActivity : CABaseActivity() {
    private var container: LinearLayout? = null
    private var progressBar: View? = null
    private var topDragContainer: LinearLayout? = null

    private var tvErrorText: TextView? = null

    private var mScrollView: ScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compare)

        bindView()
    }

    private fun bindView() {
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar?
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        container = findViewById<View>(R.id.compare_container) as LinearLayout?
        progressBar = findViewById<View>(R.id.compare_progress)
        tvErrorText = findViewById<View>(R.id.compare_middle_text) as TextView?
        topDragContainer = findViewById<View>(R.id.compare_top_title_bar) as LinearLayout?
        mScrollView = findViewById<View>(R.id.compare_scroll) as ScrollView?

        if (isDarkTheme(applicationContext)) {
            topDragContainer!!.setBackgroundResource(R.color.material_action_bar_dark)
        }

        swipeBackLayout!!.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT)
    }

    override fun onResume() {
        super.onResume()
        eventBus.register(this)
        initView()
    }

    override fun onPause() {
        super.onPause()
        eventBus.unregister(this)
    }

    private fun initView() {
        if (size() > 1) {
            if (!captainsHaveInfo()) {
                container!!.removeAllViews()
                topDragContainer!!.removeAllViews()
                search(applicationContext)
                progressBar!!.visibility = View.VISIBLE
            } else {
                progressBar!!.visibility = View.GONE
                buildViews()
            }
        } else {
            progressBar!!.visibility = View.GONE
            topDragContainer!!.removeAllViews()
            container!!.removeAllViews()
        }
        mScrollView!!.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = mScrollView!!.scrollY //for verticalScrollView
            //DO SOMETHING WITH THE SCROLL COORDINATES
            if (scrollY > 80) {
                topDragContainer!!.visibility = View.VISIBLE
            } else {
                topDragContainer!!.visibility = View.GONE
            }
        }
    }

    @Subscribe
    fun onReceiver(result: CaptainResult?) {
        if (result != null) {
            if (result.captain != null) {
                val c = result.captain
                overrideCaptain(c)
                if (captainsHaveInfo()) {
                    progressBar!!.post {
                        progressBar!!.visibility = View.GONE
                        buildViews()
                    }
                }
            }
        }
    }

    private fun buildViews() {
        val size = size()
        if (size > 1) {
            var larger = false
            var layoutId = R.layout.list_compare_two
            if (size > 2) {
                layoutId = R.layout.list_compare_three
                larger = true
            }
            container!!.removeAllViews()

            val c1 = getCaptains()[0]
            val c2 = getCaptains()[1]
            var c3: Captain? = null
            if (larger) c3 = getCaptains()[2]

            build(
                layoutId,
                larger,
                "",
                c1.name,
                c2.name,
                (if (c3 != null) c3.name else ""),
                POS_WRONG
            )
            buildTitleDrag(
                layoutId,
                larger,
                "",
                c1.name,
                c2.name,
                (if (c3 != null) c3.name else ""),
                POS_WRONG
            )

            val details1 = c1.details
            val details2 = c2.details
            var details3: CaptainDetails? = null
            if (larger) details3 = c3!!.details

            showBattles(larger, layoutId, details1, details2, details3)
            showAverageDamage(larger, layoutId, details1, details2, details3)
            showWinRate(larger, layoutId, details1, details2, details3)
            showKD(larger, layoutId, details1, details2, details3)
            showSurvivalRate(larger, layoutId, details1, details2, details3)
            showPlanesPerGame(larger, layoutId, details1, details2, details3)

            showCARatingStats(larger, layoutId, details1, details2, details3)

            buildGraphs(
                layoutId,
                larger,
                c1.name,
                c2.name,
                (c3?.name),
                c1.ships,
                c2.ships,
                (c3?.ships)
            )

            showCapturesPerGame(larger, layoutId, details1, details2, details3)
            showDefenderPointsPerGame(larger, layoutId, details1, details2, details3)

            //            showKaram(larger, layoutId, details1, details2, details3);
            val prefs = Prefs(applicationContext)
            val showCompare = prefs.getBoolean(SettingActivity.SHOW_COMPARE, true)
            if (showCompare) {
                buildTitle(getString(R.string.compare_stats_average))
                showCEDamageStats(larger, layoutId, details1, details2, details3)
                showCEKillsStats(larger, layoutId, details1, details2, details3)
                showCEWRStats(larger, layoutId, details1, details2, details3)
                showCEPlanesStats(larger, layoutId, details1, details2, details3)
                //                showCESurvivalStats(larger, layoutId, details1, details2, details3);
//                showCESurWinsStats(larger, layoutId, details1, details2, details3);
            }

            buildTitle(getString(R.string.compareArmament))

            showMainBatteryStats(larger, layoutId, details1, details2, details3)
            showTorpBatteryStats(larger, layoutId, details1, details2, details3)
            showPlaneStats(larger, layoutId, details1, details2, details3)
            showOtherKillStats(larger, layoutId, details1, details2, details3)

            showMainBatteryHitRate(larger, layoutId, details1, details2, details3)
            showTorpHitRate(larger, layoutId, details1, details2, details3)

            createAchievements(larger, layoutId, c1, c2, c3)
        } else {
        }
    }

    private fun createAchievements(
        larger: Boolean,
        layoutId: Int,
        c1: Captain,
        c2: Captain,
        c3: Captain?
    ) {
        buildTitle(getString(R.string.achievements))

        val captain1Achi: MutableMap<String?, Int> = HashMap()
        val captain2Achi: MutableMap<String?, Int> = HashMap()
        val captain3Achi: MutableMap<String?, Int> = HashMap()
        for (achi in c1.achievements) {
            captain1Achi[achi.name] = achi.number
        }
        for (achi in c2.achievements) {
            captain2Achi[achi.name] = achi.number
        }
        if (c3 != null) for (achi in c3.achievements) {
            captain3Achi[achi.name] = achi.number
        }

        val achis = infoManager!!.getAchievements(applicationContext)
        achis.items?.values?.forEach { ach ->
            var c1Achi = captain1Achi[ach?.id]
            var c2Achi = captain2Achi[ach?.id]
            var c3Achi = captain3Achi[ach?.id]
            if (c1Achi == null) {
                c1Achi = 0
            }
            if (c2Achi == null) {
                c2Achi = 0
            }
            if (c3Achi == null) {
                c3Achi = 0
            }
            if (c1Achi > 0 || c2Achi > 0 || c3Achi > 0) {
                showAchievement(larger, layoutId, c1Achi, c2Achi, c3Achi, ach?.name)
            }
        }
    }

    private fun buildTitle(title: String) {
        val view = LayoutInflater.from(applicationContext)
            .inflate(R.layout.list_compare_title, container, false)

        val tvTitle = view.findViewById<TextView>(R.id.list_compare_title)

        tvTitle.text = title

        container!!.addView(view)
    }

    private fun showAchievement(
        larger: Boolean,
        layoutId: Int,
        c1: Int,
        c2: Int,
        c3: Int,
        name: String?
    ) {
        val highestPos = highest(c1, c2, c3)
        build(
            layoutId,
            larger,
            name,
            c1.toString() + "",
            c2.toString() + "",
            (if (larger) (c3.toString() + "") else ""),
            highestPos
        )
    }


    private fun showBattles(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        val battles1 = details1.battles
        val battles2 = details2.battles
        var battles3 = POS_WRONG
        if (larger) battles3 = details3!!.battles
        val highestPos = highest(battles1, battles2, battles3)
        if (battles3 == POS_WRONG) battles3 = 0
        build(
            layoutId,
            larger,
            getString(R.string.battles),
            battles1.toString() + "",
            battles2.toString() + "",
            (if (larger) (battles3.toString() + "") else ""),
            highestPos
        )
    }

    private fun showKaram(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails
    ) {
        val karma1 = details1.karma
        val karma2 = details2.karma
        var karma3 = POS_WRONG
        if (larger) karma3 = details3.karma
        val highestPos = highest(karma1, karma2, karma3)
        if (karma3 == POS_WRONG) karma3 = 0
        build(
            layoutId,
            larger,
            getString(R.string.karma),
            karma1.toString() + "",
            karma2.toString() + "",
            (if (larger) (karma3.toString() + "") else ""),
            highestPos
        )
    }

    private fun showMainBatteryStats(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        val num1 = details1.mainBattery.frags
        val num2 = details2.mainBattery.frags
        var num3 = POS_WRONG
        if (larger) num3 = details3!!.mainBattery.frags
        val highestPos = highest(num1, num2, num3)
        if (num3 == POS_WRONG) num3 = 0
        build(
            layoutId,
            larger,
            getString(R.string.main_battery_kills),
            num1.toString() + "",
            num2.toString() + "",
            (if (larger) (num3.toString() + "") else ""),
            highestPos
        )
    }

    private fun showCEDamageStats(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        val num1 = details1.getcDamage() - details1.expectedDamage
        val num2 = details2.getcDamage() - details2.expectedDamage
        var num3 = POS_WRONG.toFloat()
        if (larger) num3 = details3!!.getcDamage() - details3.expectedDamage
        val highestPos = highest(num1, num2, num3)
        if (num3 == POS_WRONG.toFloat()) num3 = 0f
        val formatter = DecimalFormat("#")
        build(
            layoutId,
            larger,
            getString(R.string.damage),
            formatter.format(num1.toDouble()),
            formatter.format(num2.toDouble()),
            (if (larger) (formatter.format(num3.toDouble())) else ""),
            highestPos
        )
    }

    private fun showCARatingStats(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        val num1 = details1.caRating
        val num2 = details2.caRating
        var num3 = POS_WRONG.toFloat()
        if (larger) num3 = details3!!.caRating
        val highestPos = highest(num1, num2, num3)
        if (num3 == POS_WRONG.toFloat()) num3 = 0f
        val formatter = DecimalFormat("#")
        build(
            layoutId,
            larger,
            getString(R.string.community_assistant_rating_shorter),
            formatter.format(num1.toDouble()),
            formatter.format(num2.toDouble()),
            (if (larger) (formatter.format(num3.toDouble())) else ""),
            highestPos
        )
    }

    private fun showCEKillsStats(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        val num1 = details1.getcKills() - details1.expectedKills
        val num2 = details2.getcKills() - details2.expectedKills
        var num3 = POS_WRONG.toFloat()
        if (larger) num3 = details3!!.getcKills() - details3.expectedKills
        val highestPos = highest(num1, num2, num3)
        if (num3 == POS_WRONG.toFloat()) num3 = 0f
        val formatter = DecimalFormat("#.#")
        build(
            layoutId,
            larger,
            getString(R.string.kills),
            formatter.format(num1.toDouble()),
            formatter.format(num2.toDouble()),
            (if (larger) (formatter.format(num3.toDouble())) else ""),
            highestPos
        )
    }

    private fun showCEWRStats(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        val num1 = details1.getcWinRate() - details1.expectedWinRate
        val num2 = details2.getcWinRate() - details2.expectedWinRate
        var num3 = POS_WRONG.toFloat()
        if (larger) num3 = details3!!.getcWinRate() - details3.expectedWinRate
        val highestPos = highest(num1, num2, num3)
        if (num3 == POS_WRONG.toFloat()) num3 = 0f
        val formatter = DecimalFormat("#.#%")
        build(
            layoutId,
            larger,
            getString(R.string.win_rate),
            formatter.format(num1.toDouble()),
            formatter.format(num2.toDouble()),
            (if (larger) (formatter.format(num3.toDouble())) else ""),
            highestPos
        )
    }

    private fun showCEPlanesStats(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        val num1 = details1.getcPlanes() - details1.expectedPlanes
        val num2 = details2.getcPlanes() - details2.expectedPlanes
        var num3 = POS_WRONG.toFloat()
        if (larger) num3 = details3!!.getcPlanes() - details3.expectedPlanes
        val highestPos = highest(num1, num2, num3)
        if (num3 == POS_WRONG.toFloat()) num3 = 0f
        val formatter = DecimalFormat("#.#")
        build(
            layoutId,
            larger,
            getString(R.string.planes_downed),
            formatter.format(num1.toDouble()),
            formatter.format(num2.toDouble()),
            (if (larger) (formatter.format(num3.toDouble())) else ""),
            highestPos
        )
    }

    //    private void showCESurvivalStats(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
    //        float num1 = details1.getcSurvival() - details1.getExpectedSurvival();
    //        float num2 = details2.getcSurvival() - details2.getExpectedSurvival();
    //        float num3 = POS_WRONG;
    //        if (larger)
    //            num3 = details3.getcSurvival() - details3.getExpectedSurvival();
    //        int highestPos = highest(num1, num2, num3);
    //        if (num3 == POS_WRONG)
    //            num3 = 0;
    //        DecimalFormat formatter = new DecimalFormat("#.#%");
    //        build(layoutId, larger, getString(R.string.survival_rate), formatter.format(num1) + "", formatter.format(num2) + "", (larger ? (formatter.format(num3) + "") : ""), highestPos);
    //    }
    //    private void showCESurWinsStats(boolean larger, int layoutId, CaptainDetails details1, CaptainDetails details2, CaptainDetails details3) {
    //        float num1 = details1.getcSurWins() - details1.getExpectedSurWins();
    //        float num2 = details2.getcSurWins() - details2.getExpectedSurWins();
    //        float num3 = POS_WRONG;
    //        if (larger)
    //            num3 = details3.getcSurWins() - details3.getExpectedSurWins();
    //        int highestPos = highest(num1, num2, num3);
    //        if (num3 == POS_WRONG)
    //            num3 = 0;
    //        DecimalFormat formatter = new DecimalFormat("#.#%");
    //        build(layoutId, larger, getString(R.string.survived_wins), formatter.format(num1) + "", formatter.format(num2) + "", (larger ? (formatter.format(num3) + "") : ""), highestPos);
    //    }
    private fun showTorpBatteryStats(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        val num1 = details1.torpedoes.frags
        val num2 = details2.torpedoes.frags
        var num3 = POS_WRONG
        if (larger) num3 = details3!!.torpedoes.frags
        val highestPos = highest(num1, num2, num3)
        if (num3 == POS_WRONG) num3 = 0
        build(
            layoutId,
            larger,
            getString(R.string.torpedoes_kills),
            num1.toString() + "",
            num2.toString() + "",
            (if (larger) (num3.toString() + "") else ""),
            highestPos
        )
    }

    private fun showPlaneStats(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        val battles1 = details1.aircraft.frags
        val battles2 = details2.aircraft.frags
        var battles3 = POS_WRONG
        if (larger) battles3 = details3!!.aircraft.frags
        val highestPos = highest(battles1, battles2, battles3)
        if (battles3 == POS_WRONG) battles3 = 0
        build(
            layoutId,
            larger,
            getString(R.string.aircraft_kills),
            battles1.toString() + "",
            battles2.toString() + "",
            (if (larger) (battles3.toString() + "") else ""),
            highestPos
        )
    }

    private fun showOtherKillStats(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        val battles1 =
            details1.frags - details1.aircraft.frags - details1.torpedoes.frags - details1.mainBattery.frags
        val battles2 =
            details2.frags - details2.aircraft.frags - details2.torpedoes.frags - details2.mainBattery.frags
        var battles3 = POS_WRONG
        if (larger) battles3 =
            details3!!.frags - details3.aircraft.frags - details3.torpedoes.frags - details3.mainBattery.frags
        val highestPos = highest(battles1, battles2, battles3)
        if (battles3 == POS_WRONG) battles3 = 0
        build(
            layoutId,
            larger,
            getString(R.string.other_kills),
            battles1.toString() + "",
            battles2.toString() + "",
            (if (larger) (battles3.toString() + "") else ""),
            highestPos
        )
    }

    private fun showMainBatteryHitRate(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        val shots1 = details1.mainBattery.shots
        val shots2 = details2.mainBattery.shots
        var shots3 = 0
        if (larger) shots3 = details3!!.mainBattery.shots

        var wn1 = 0f
        var wn2 = 0f
        var wn3 = POS_WRONG.toFloat()
        if (shots1 > 0) {
            wn1 = (details1.mainBattery.hits.toFloat() / shots1) * 100
        }
        if (shots2 > 0) {
            wn2 = (details2.mainBattery.hits.toFloat() / shots2) * 100
        }
        if (shots3 > 0) {
            wn3 = (details3!!.mainBattery.hits.toFloat() / shots3) * 100
        }

        val highestPos = highest(wn1, wn2, wn3)
        if (wn3 == POS_WRONG.toFloat()) wn3 = 0f
        val formatter = oneDepthDecimalFormatter

        build(
            layoutId,
            larger,
            getString(R.string.main_battery_hit_per),
            formatter.format(wn1.toDouble()),
            formatter.format(wn2.toDouble()),
            (if (larger) (formatter.format(wn3.toDouble())) else ""),
            highestPos
        )
    }

    private fun showTorpHitRate(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        val shots1 = details1.torpedoes.shots
        val shots2 = details2.torpedoes.shots
        var shots3 = 0
        if (larger) shots3 = details3!!.torpedoes.shots

        var wn1 = 0f
        var wn2 = 0f
        var wn3 = POS_WRONG.toFloat()
        if (shots1 > 0) {
            wn1 = (details1.torpedoes.hits.toFloat() / shots1) * 100
        }
        if (shots2 > 0) {
            wn2 = (details2.torpedoes.hits.toFloat() / shots2) * 100
        }
        if (shots3 > 0) {
            wn3 = (details3!!.torpedoes.hits.toFloat() / shots3) * 100
        }

        val highestPos = highest(wn1, wn2, wn3)

        val formatter = oneDepthDecimalFormatter
        if (wn3 == POS_WRONG.toFloat()) wn3 = 0f
        build(
            layoutId,
            larger,
            getString(R.string.torpedoes_hit_per),
            formatter.format(wn1.toDouble()),
            formatter.format(wn2.toDouble()),
            (if (larger) (formatter.format(wn3.toDouble())) else ""),
            highestPos
        )
    }

    private fun showAverageDamage(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        val battles1 = details1.battles
        val battles2 = details2.battles
        var battles3 = 0
        if (larger) battles3 = details3!!.battles

        var wn1 = 0
        var wn2 = 0
        var wn3 = POS_WRONG
        if (battles1 > 0) {
            wn1 = (details1.totalDamage / battles1).toInt()
        }
        if (battles2 > 0) {
            wn2 = (details2.totalDamage / battles2).toInt()
        }
        if (battles3 > 0) {
            wn3 = (details3!!.totalDamage / battles3).toInt()
        }

        val highestPos = highest(wn1, wn2, wn3)

        val formatter = defaultDecimalFormatter
        if (wn3 == POS_WRONG) wn3 = 0
        build(
            layoutId,
            larger,
            getString(R.string.average_damage),
            formatter.format(wn1.toLong()),
            formatter.format(wn2.toLong()),
            (if (larger) (formatter.format(wn3.toLong())) else ""),
            highestPos
        )
    }

    private fun showWinRate(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        val battles1 = details1.battles
        val battles2 = details2.battles
        var battles3 = 0
        if (larger) battles3 = details3!!.battles

        var wn1 = 0f
        var wn2 = 0f
        var wn3 = POS_WRONG.toFloat()
        if (battles1 > 0) {
            wn1 = (details1.wins.toFloat() / battles1) * 100
        }
        if (battles2 > 0) {
            wn2 = (details2.wins.toFloat() / battles2) * 100
        }
        if (battles3 > 0) {
            wn3 = (details3!!.wins.toFloat() / battles3) * 100
        }

        val highestPos = highest(wn1, wn2, wn3)

        val formatter = defaultDecimalFormatter
        if (wn3 == POS_WRONG.toFloat()) wn3 = 0f
        build(
            layoutId,
            larger,
            getString(R.string.win_rate),
            formatter.format(wn1.toDouble()),
            formatter.format(wn2.toDouble()),
            (if (larger) (formatter.format(wn3.toDouble())) else ""),
            highestPos
        )
    }

    private fun showSurvivalRate(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        val battles1 = details1.battles
        val battles2 = details2.battles
        var battles3 = 0
        if (larger) battles3 = details3!!.battles

        var wn1 = 0f
        var wn2 = 0f
        var wn3 = POS_WRONG.toFloat()
        if (battles1 > 0) {
            wn1 = (details1.survivedBattles.toFloat() / battles1) * 100
        }
        if (battles2 > 0) {
            wn2 = (details2.survivedBattles.toFloat() / battles2) * 100
        }
        if (battles3 > 0) {
            wn3 = (details3!!.survivedBattles.toFloat() / battles3) * 100
        }

        val highestPos = highest(wn1, wn2, wn3)

        val formatter = defaultDecimalFormatter
        if (wn3 == POS_WRONG.toFloat()) wn3 = 0f
        build(
            layoutId,
            larger,
            getString(R.string.survival_rate),
            formatter.format(wn1.toDouble()),
            formatter.format(wn2.toDouble()),
            (if (larger) (formatter.format(wn3.toDouble())) else ""),
            highestPos
        )
    }

    private fun showKD(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        var battles1 = details1.battles.toFloat()
        var battles2 = details2.battles.toFloat()
        var battles3 = 0f
        if (larger) battles3 = details3!!.battles.toFloat()


        if (battles1 != details1.survivedBattles.toFloat()) battles1 =
            battles1 - details1.survivedBattles

        if (battles2 != details2.survivedBattles.toFloat()) battles2 =
            battles2 - details2.survivedBattles

        if (larger && battles3 != details3!!.survivedBattles.toFloat()) battles3 =
            battles3 - details3.survivedBattles

        var kd1 = 0f
        var kd2 = 0f
        var kd3 = POS_WRONG.toFloat()
        if (battles1 > 0) {
            kd1 = (details1.frags.toFloat() / battles1)
        }
        if (battles2 > 0) {
            kd2 = (details2.frags.toFloat() / battles2)
        }
        if (battles3 > 0 && larger) {
            kd3 = (details3!!.frags.toFloat() / battles3)
        }

        val highestPos = highest(kd1, kd2, kd3)

        val formatter = defaultDecimalFormatter
        if (kd3 == POS_WRONG.toFloat()) kd3 = 0f
        build(
            layoutId,
            larger,
            getString(R.string.kills_game),
            formatter.format(kd1.toDouble()),
            formatter.format(kd2.toDouble()),
            (if (larger) (formatter.format(kd3.toDouble())) else ""),
            highestPos
        )
    }

    private fun showPlanesPerGame(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        val battles1 = details1.battles.toFloat()
        val battles2 = details2.battles.toFloat()
        var battles3 = 0f
        if (larger) battles3 = details3!!.battles.toFloat()

        var wn1 = 0f
        var wn2 = 0f
        var wn3 = POS_WRONG.toFloat()
        if (battles1 > 0) {
            wn1 = details1.planesKilled / battles1
        }
        if (battles2 > 0) {
            wn2 = details2.planesKilled / battles2
        }
        if (battles3 > 0) {
            wn3 = details3!!.planesKilled / battles3
        }

        val highestPos = highest(wn1, wn2, wn3)

        val formatter = oneDepthDecimalFormatter
        if (wn3 == POS_WRONG.toFloat()) wn3 = 0f
        build(
            layoutId,
            larger,
            getString(R.string.planes_downed_game),
            formatter.format(wn1.toDouble()),
            formatter.format(wn2.toDouble()),
            (if (larger) (formatter.format(wn3.toDouble())) else ""),
            highestPos
        )
    }

    private fun showCapturesPerGame(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        val battles1 = details1.battles.toFloat()
        val battles2 = details2.battles.toFloat()
        var battles3 = 0f
        if (larger) battles3 = details3!!.battles.toFloat()

        var wn1 = 0f
        var wn2 = 0f
        var wn3 = POS_WRONG.toFloat()
        if (battles1 > 0) {
            wn1 = details1.capturePoints / battles1
        }
        if (battles2 > 0) {
            wn2 = details2.capturePoints / battles2
        }
        if (battles3 > 0) {
            wn3 = details3!!.capturePoints / battles3
        }

        val highestPos = highest(wn1, wn2, wn3)

        val formatter = oneDepthDecimalFormatter
        if (wn3 == POS_WRONG.toFloat()) wn3 = 0f
        build(
            layoutId,
            larger,
            getString(R.string.caps_game),
            formatter.format(wn1.toDouble()),
            formatter.format(wn2.toDouble()),
            (if (larger) (formatter.format(wn3.toDouble())) else ""),
            highestPos
        )
    }

    private fun showDefenderPointsPerGame(
        larger: Boolean,
        layoutId: Int,
        details1: CaptainDetails,
        details2: CaptainDetails,
        details3: CaptainDetails?
    ) {
        val battles1 = details1.battles.toFloat()
        val battles2 = details2.battles.toFloat()
        var battles3 = 0f
        if (larger) battles3 = details3!!.battles.toFloat()

        var wn1 = 0f
        var wn2 = 0f
        var wn3 = POS_WRONG.toFloat()
        if (battles1 > 0) {
            wn1 = details1.droppedCapturePoints / battles1
        }
        if (battles2 > 0) {
            wn2 = details2.droppedCapturePoints / battles2
        }
        if (battles3 > 0) {
            wn3 = details3!!.droppedCapturePoints / battles3
        }

        val highestPos = highest(wn1, wn2, wn3)

        val formatter = oneDepthDecimalFormatter

        if (wn3 == POS_WRONG.toFloat()) wn3 = 0f

        build(
            layoutId,
            larger,
            getString(R.string.def_reset_game),
            formatter.format(wn1.toDouble()),
            formatter.format(wn2.toDouble()),
            (if (larger) (formatter.format(wn3.toDouble())) else ""),
            highestPos
        )
    }

    private fun build(
        layoutId: Int,
        larger: Boolean,
        titleStr: String?,
        oneStr: String,
        twoStr: String,
        threeStr: String,
        highest: Int
    ) {
        val view = LayoutInflater.from(applicationContext).inflate(layoutId, container, false)

        val title = view.findViewById<TextView>(R.id.compare_title)
        val one = view.findViewById<TextView>(R.id.compare_one)
        val two = view.findViewById<TextView>(R.id.compare_two)

        title.text = titleStr
        if (TextUtils.isEmpty(titleStr)) title.setText(R.string.captain)
        one.text = oneStr
        two.text = twoStr
        var three: TextView? = null
        if (larger) {
            three = view.findViewById(R.id.compare_three)
            three.text = threeStr
        }
        if (highest != POS_WRONG) colorCells(highest, R.drawable.compare_top_grid, one, two, three)

        container!!.addView(view)
    }

    private fun buildTitleDrag(
        layoutId: Int,
        larger: Boolean,
        titleStr: String,
        oneStr: String,
        twoStr: String,
        threeStr: String,
        highest: Int
    ) {
        val view = LayoutInflater.from(applicationContext).inflate(layoutId, container, false)

        val title = view.findViewById<TextView>(R.id.compare_title)
        val one = view.findViewById<TextView>(R.id.compare_one)
        val two = view.findViewById<TextView>(R.id.compare_two)

        title.visibility = View.INVISIBLE
        one.text = oneStr
        two.text = twoStr
        var three: TextView? = null
        if (larger) {
            three = view.findViewById(R.id.compare_three)
            three.text = threeStr
        }

        topDragContainer!!.addView(view)
    }

    private fun build(
        view: View,
        larger: Boolean,
        titleStr: String,
        oneStr: String,
        twoStr: String,
        threeStr: String,
        highest: Int
    ) {
        val title = view.findViewById<TextView>(R.id.compare_title)
        val one = view.findViewById<TextView>(R.id.compare_one)
        val two = view.findViewById<TextView>(R.id.compare_two)

        title.text = titleStr
        one.text = oneStr
        two.text = twoStr
        var three: TextView? = null
        if (larger) {
            three = view.findViewById(R.id.compare_three)
            three.text = threeStr
        }
        if (highest != POS_WRONG) colorCells(highest, R.drawable.compare_top_grid, one, two, three)
    }

    private fun buildGraphs(
        layoutId: Int,
        larger: Boolean,
        s: String,
        s1: String,
        s2: String?,
        details1: List<Ship>,
        details2: List<Ship>,
        details3: List<Ship>?
    ) {
        val damage = LayoutInflater.from(applicationContext)
            .inflate(R.layout.list_compare_bar_graph, container, false)

        val titledamage = damage.findViewById<TextView>(R.id.list_compare_graph_text)
        titledamage.setText(R.string.average_damage_per_tier)

        val chartDamage = damage.findViewById<BarChart>(R.id.list_compare_graph)

        val experience = LayoutInflater.from(applicationContext)
            .inflate(R.layout.list_compare_bar_graph, container, false)

        val titleExperience = experience.findViewById<TextView>(R.id.list_compare_graph_text)
        titleExperience.setText(R.string.average_experience_per_tier)

        val chartExperience = experience.findViewById<BarChart>(R.id.list_compare_graph)

        val view = LayoutInflater.from(applicationContext).inflate(layoutId, container, false)
        val title = view.findViewById<TextView>(R.id.compare_title)
        title.setText(R.string.average_tier)

        setUpCharts(
            view,
            larger,
            s,
            s1,
            s2,
            details1,
            details2,
            details3,
            chartDamage,
            chartExperience
        )
        container!!.addView(view)
        container!!.addView(damage)
        container!!.addView(experience)
    }

    private fun setUpCharts(
        view: View,
        larger: Boolean,
        s: String,
        s1: String,
        s2: String?,
        captain: List<Ship>,
        captain2: List<Ship>,
        captain3: List<Ship>?,
        averageDamage: BarChart,
        averageExperience: BarChart
    ) {
        val runnable: Runnable = object : Runnable {
            override fun run() {
                averageDamage.clear()
                averageExperience.clear()
                val cap1 = calculateStats(captain)
                val cap2 = calculateStats(captain2)
                var cap3: CaptainStatsCompareObject? = null
                if (captain3 != null) cap3 = calculateStats(captain3)
                setUpExpChart(
                    cap1.averages,
                    cap2.averages,
                    (cap3?.averages),
                    averageExperience,
                    s,
                    s1,
                    s2
                )
                setUpDamageChart(
                    cap1.avgDamages,
                    cap2.avgDamages,
                    (cap3?.avgDamages),
                    averageDamage,
                    s,
                    s1,
                    s2
                )
                val cap3Obj = cap3
                runOnUiThread {
                    val formatter = oneDepthDecimalFormatter
                    val highest = highest(
                        cap1.averageTier,
                        cap2.averageTier,
                        (cap3Obj?.averageTier ?: POS_WRONG.toFloat())
                    )
                    build(
                        view,
                        larger,
                        getString(R.string.average_tier),
                        formatter.format(cap1.averageTier.toDouble()),
                        formatter.format(cap2.averageTier.toDouble()),
                        (if ((cap3Obj != null)) formatter.format(cap3Obj.averageTier.toDouble()) else ""),
                        highest
                    )
                }
            }

            private fun calculateStats(capShips: List<Ship>): CaptainStatsCompareObject {
                val battleCounts = SparseArray<Int>()
                val experience = SparseArray<Long>()
                val damages = SparseArray<Long>()
                val shipsHolder = infoManager!!.getShipInfo(applicationContext)
                var averageTier = 0
                var totalBattles = 0
                for (s in capShips) {
                    val info = shipsHolder[s.shipId]
                    if (info != null) {
                        val tier = info.tier
                        val battleCount = battleCounts[tier]
                        if (battleCount != null) {
                            battleCounts.put(tier, battleCount + s.battles)
                        } else {
                            battleCounts.put(tier, s.battles)
                        }
                        val exp = experience[tier]
                        if (exp != null) {
                            experience.put(tier, exp + s.totalXP)
                        } else {
                            experience.put(tier, s.totalXP)
                        }
                        val damage = damages[tier]
                        if (damage != null) {
                            damages.put(tier, (damage + s.totalDamage).toLong())
                        } else {
                            damages.put(tier, s.totalDamage.toLong())
                        }
                        averageTier += tier * s.battles
                    }
                    totalBattles += s.battles
                }
                val averageTierNumber = averageTier.toFloat() / totalBattles.toFloat()
                val averages: MutableMap<Int, Long> = HashMap()
                val avgDamages: MutableMap<Int, Long> = HashMap()
                for (i in 1..10) {
                    val battleCount = battleCounts[i]
                    val exp = experience[i]
                    if (battleCount != null && exp != null && battleCount > 0) {
                        averages[i] = exp / battleCount
                    } else {
                        averages[i] = 0L
                    }
                    val damage = damages[i]
                    if (damage != null && battleCount!! > 0) {
                        avgDamages[i] = damage / battleCount
                    } else {
                        avgDamages[i] = 0L
                    }
                }
                val `object` = CaptainStatsCompareObject()
                `object`.averages = averages
                `object`.avgDamages = avgDamages
                `object`.averageTier = averageTierNumber
                return `object`
            }

            private fun setUpDamageChart(
                avgDamages: Map<Int, Long>?,
                averages2: Map<Int, Long>?,
                averages3: Map<Int, Long>?,
                chartAverageDamage: BarChart,
                s: String,
                s1: String,
                s2: String?
            ) {
                runOnUiThread {
                    val textColor = ContextCompat.getColor(
                        chartAverageDamage.context,
                        R.color.material_text_primary
                    )
                    val accentColor =
                        ContextCompat.getColor(chartAverageDamage.context, R.color.compare_first)
                    val accentColor2 =
                        ContextCompat.getColor(chartAverageDamage.context, R.color.compare_second)
                    val accentColor3 =
                        ContextCompat.getColor(chartAverageDamage.context, R.color.compare_three)
                    chartAverageDamage.setDrawBarShadow(false)
                    chartAverageDamage.setDrawValueAboveBar(false)
                    chartAverageDamage.setPinchZoom(false)
                    chartAverageDamage.isDoubleTapToZoomEnabled = false
                    chartAverageDamage.setDrawGridBackground(false)
                    chartAverageDamage.setDrawValueAboveBar(true)
                    chartAverageDamage.setTouchEnabled(false)

                    val xAxis = chartAverageDamage.xAxis
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.textColor = textColor
                    xAxis.setDrawGridLines(true)

                    val yAxis = chartAverageDamage.axisRight
                    yAxis.setLabelCount(6, false)
                    yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                    yAxis.textColor = textColor
                    yAxis.isEnabled = false

                    val yAxis2 = chartAverageDamage.axisLeft
                    yAxis2.setLabelCount(6, false)
                    yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                    yAxis2.textColor = textColor
                    yAxis2.valueFormatter = LargeValueFormatter()

                    val l = chartAverageDamage.legend
                    l.isEnabled = true
                    l.position = Legend.LegendPosition.BELOW_CHART_LEFT
                    l.textColor = ContextCompat.getColor(applicationContext, R.color.white)

                    val xVals = ArrayList<String>()
                    for (i in 1..10) xVals.add(i.toString() + "")

                    val yVals1 = ArrayList<BarEntry>()
                    val yVals2 = ArrayList<BarEntry>()
                    val yVals3 = ArrayList<BarEntry>()

                    val colorList: List<Int> = ArrayList()
                    for (i in 0..9) {
                        val dValue = avgDamages!![i + 1]!!.toDouble()
                        val value = dValue.toFloat()
                        val dValue2 = averages2!![i + 1]!!.toDouble()
                        val value2 = dValue2.toFloat()
                        yVals1.add(BarEntry(value, i))
                        yVals2.add(BarEntry(value2, i))
                        if (averages3 != null) {
                            val dValue3 = averages3[i + 1]!!.toDouble()
                            val value3 = dValue3.toFloat()
                            yVals3.add(BarEntry(value3, i))
                        }
                    }

                    val set1 = BarDataSet(yVals1, "")
                    set1.barSpacePercent = 20f
                    set1.color = accentColor
                    set1.label = s

                    val set2 = BarDataSet(yVals2, "")
                    set2.barSpacePercent = 20f
                    set2.color = accentColor2
                    set2.label = s1

                    var set3: BarDataSet? = null
                    if (averages3 != null) {
                        set3 = BarDataSet(yVals3, "")
                        set3.barSpacePercent = 20f
                        set3.color = accentColor3
                        set3.label = s2
                    }


                    val dataSets = ArrayList<IBarDataSet>()
                    dataSets.add(set1)
                    dataSets.add(set2)
                    if (set3 != null) dataSets.add(set3)

                    val data = BarData(xVals, dataSets)
                    data.setValueTextSize(10f)
                    data.setValueTextColor(textColor)
                    data.setValueFormatter(LargeValueFormatter())

                    chartAverageDamage.setDescription("")
                    chartAverageDamage.data = data
                    chartAverageDamage.requestLayout()
                }
            }

            private fun setUpExpChart(
                averages: Map<Int, Long>?,
                averages2: Map<Int, Long>?,
                averages3: Map<Int, Long>?,
                chartAverageExperience: BarChart,
                s: String,
                s1: String,
                s2: String?
            ) {
                runOnUiThread {
                    val textColor = ContextCompat.getColor(
                        chartAverageExperience.context,
                        R.color.material_text_primary
                    )
                    val accentColor = ContextCompat.getColor(
                        chartAverageExperience.context,
                        R.color.compare_first
                    )
                    val accentColor2 = ContextCompat.getColor(
                        chartAverageExperience.context,
                        R.color.compare_second
                    )
                    val accentColor3 = ContextCompat.getColor(
                        chartAverageExperience.context,
                        R.color.compare_three
                    )

                    chartAverageExperience.setDrawBarShadow(false)
                    chartAverageExperience.setDrawValueAboveBar(false)
                    chartAverageExperience.setPinchZoom(false)
                    chartAverageExperience.isDoubleTapToZoomEnabled = false
                    chartAverageExperience.setDrawGridBackground(false)
                    chartAverageExperience.setDrawValueAboveBar(true)
                    chartAverageExperience.setTouchEnabled(false)

                    val xAxis = chartAverageExperience.xAxis
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.textColor = textColor
                    xAxis.setDrawGridLines(true)

                    val yAxis = chartAverageExperience.axisRight
                    yAxis.setLabelCount(6, false)
                    yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                    yAxis.textColor = textColor
                    yAxis.isEnabled = false

                    val yAxis2 = chartAverageExperience.axisLeft
                    yAxis2.setLabelCount(6, false)
                    yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                    yAxis2.textColor = textColor
                    yAxis2.valueFormatter = LargeValueFormatter()

                    val l = chartAverageExperience.legend
                    l.isEnabled = true
                    l.position = Legend.LegendPosition.BELOW_CHART_LEFT
                    l.textColor = ContextCompat.getColor(applicationContext, R.color.white)

                    val xVals = ArrayList<String>()
                    for (i in 1..10) xVals.add(i.toString() + "")

                    val yVals1 = ArrayList<BarEntry>()
                    val yVals2 = ArrayList<BarEntry>()
                    val yVals3 = ArrayList<BarEntry>()
                    val colorList: List<Int> = ArrayList()
                    for (i in 0..9) {
                        val dValue = averages!![i + 1]!!.toDouble()
                        val value = dValue.toFloat()
                        val dValue2 = averages2!![i + 1]!!.toDouble()
                        val value2 = dValue2.toFloat()
                        yVals1.add(BarEntry(value, i))
                        yVals2.add(BarEntry(value2, i))
                        if (averages3 != null) {
                            val dValue3 = averages3[i + 1]!!.toDouble()
                            val value3 = dValue3.toFloat()
                            yVals3.add(BarEntry(value3, i))
                        }
                    }


                    val set1 = BarDataSet(yVals1, "")
                    set1.barSpacePercent = 20f
                    set1.color = accentColor
                    set1.label = s

                    val set2 = BarDataSet(yVals2, "")
                    set2.barSpacePercent = 20f
                    set2.color = accentColor2
                    set2.label = s1

                    var set3: BarDataSet? = null
                    if (averages3 != null) {
                        set3 = BarDataSet(yVals3, "")
                        set3!!.barSpacePercent = 20f
                        set3!!.color = accentColor3
                        set3!!.label = s2
                    }

                    val dataSets = ArrayList<IBarDataSet>()
                    dataSets.add(set1)
                    dataSets.add(set2)
                    if (set3 != null) dataSets.add(set3!!)

                    val data = BarData(xVals, dataSets)
                    data.setValueTextSize(10f)
                    data.setValueTextColor(textColor)
                    data.setValueFormatter(LargeValueFormatter())

                    chartAverageExperience.setDescription("")

                    chartAverageExperience.data = data
                    chartAverageExperience.requestLayout()
                }
            }
        }
        Thread(runnable).start()
    }

    private fun highest(one: Int, two: Int, three: Int): Int {
        var highestPos = POS_WRONG
        if (one == 0 && two == 0 && (three == 0 || three == POS_WRONG)) {
        } else {
            var highest = 0
            val array = intArrayOf(one, two, three)
            for (i in array.indices) {
                val current = array[i]
                if (current > highest) {
                    highest = current
                    highestPos = i
                }
            }
        }
        return highestPos
    }

    private fun highest(one: Float, two: Float, three: Float): Int {
        var highestPos = POS_WRONG
        if (one == 0f && two == 0f && (three == 0f || three == POS_WRONG.toFloat())) {
        } else {
            var highest = 0f
            val array = floatArrayOf(one, two, three)
            for (i in array.indices) {
                val current = array[i]
                if (current > highest) {
                    highest = current
                    highestPos = i
                }
            }
        }
        return highestPos
    }

    private fun highest(one: Long, two: Long, three: Long): Int {
        var highestPos = POS_WRONG
        if (one == 0L && two == 0L && (three == 0L || three == POS_WRONG.toLong())) {
        } else {
            var highest: Long = 0
            val array = longArrayOf(one, two, three)
            for (i in array.indices) {
                val current = array[i]
                if (current > highest && current != 0L) {
                    highest = current
                    highestPos = i
                }
            }
        }
        return highestPos
    }

    private fun colorCells(
        number: Int,
        background: Int,
        one: TextView,
        two: TextView,
        three: TextView?
    ) {
        if (number == 0) {
            one.setBackgroundResource(background)
        }
        if (number == 1) {
            two.setBackgroundResource(background)
        }
        if (three != null) {
            if (number == 2) {
                three.setBackgroundResource(background)
            }
        }
    }

    private inner class CaptainStatsCompareObject {
        var averages: Map<Int, Long>? = null
        var avgDamages: Map<Int, Long>? = null
        var averageTier: Float = 0f
    }

    companion object {
        const val POS_WRONG: Int = -1
    }
}