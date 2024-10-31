package com.half.wowsca.ui.viewcaptain.tabs

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.formatter.YAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.github.mikephil.charting.utils.ViewPortHandler
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.getTextColor
import com.half.wowsca.CAApp.Companion.getTheme
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.CAApp.Companion.isColorblind
import com.half.wowsca.R
import com.half.wowsca.backend.GetCaptainTask
import com.half.wowsca.interfaces.ICaptain
import com.half.wowsca.managers.CARatingManager
import com.half.wowsca.managers.CaptainManager.createCapIdStr
import com.half.wowsca.managers.CaptainManager.fromSearch
import com.half.wowsca.managers.CaptainManager.getCapIdStr
import com.half.wowsca.managers.StorageManager.getPlayerStats
import com.half.wowsca.model.Captain
import com.half.wowsca.model.CaptainDetails
import com.half.wowsca.model.CaptainReceivedEvent
import com.half.wowsca.model.CaptainSavedEvent
import com.half.wowsca.model.ProgressEvent
import com.half.wowsca.model.RefreshEvent
import com.half.wowsca.model.Statistics
import com.half.wowsca.model.enums.AverageType
import com.half.wowsca.model.listModels.ListAverages
import com.half.wowsca.ui.CAFragment
import com.half.wowsca.ui.InformationActivity
import com.half.wowsca.ui.SettingActivity
import com.half.wowsca.ui.UIUtils.createOtherStatsArea
import com.half.wowsca.ui.UIUtils.getNationText
import com.half.wowsca.ui.UIUtils.setUpCard
import com.half.wowsca.ui.adapter.AveragesAdapter
import com.half.wowsca.ui.views.NonScrollableGridView
import com.half.wowsca.ui.views.RadarMarkerView
import com.utilities.Utils.defaultDecimalFormatter
import com.utilities.Utils.getDayMonthYearFormatter
import com.utilities.Utils.oneDepthDecimalFormatter
import com.utilities.logging.Dlog.d
import com.utilities.preferences.Prefs
import org.greenrobot.eventbus.Subscribe
import java.text.DecimalFormat
import java.util.Calendar
import java.util.Collections
import kotlin.math.abs

/**
 * Created by slai4 on 9/15/2015.
 */
class CaptainFragment : CAFragment() {
    private var aBattles: View? = null
    private var aWinRate: View? = null
    private var aAverageExp: View? = null
    private var aAverageDamage: View? = null
    private var aKillDeath: View? = null

    private var tvBattles: TextView? = null
    private var tvWinRate: TextView? = null
    private var tvAverageExp: TextView? = null
    private var tvAverageDamage: TextView? = null
    private var tvKillDeath: TextView? = null

    private var tvGenXP: TextView? = null
    private var tvGenDamage: TextView? = null
    private var tvGenPlanesKilled: TextView? = null
    private var tvGenCapture: TextView? = null
    private var tvGenDropped: TextView? = null
    private var tvGenProfileLevel: TextView? = null

    private var topicalArea: View? = null
    private var topicalText: TextView? = null
    private var topicalChart: LineChart? = null
    private var tiersChart: BarChart? = null
    private var tvTierAverage: TextView? = null
    private var chartProgress: View? = null
    private var tvTopicalDescription: TextView? = null

    private var chartGamePerType: HorizontalBarChart? = null
    private var chartGamePerNation: PieChart? = null

    private var pbDistanceTraveled: ProgressBar? = null
    private var tvDistanceTraveled: TextView? = null
    private var tvDistanceTotal: TextView? = null

    //    private View captainAddView;
    //    private CheckBox captainCheckBox;
    private var tvTotalPlanes: TextView? = null
    private var tvTotalCaptures: TextView? = null
    private var tvTotalDefReset: TextView? = null

    private var tvWins: TextView? = null
    private var tvLosses: TextView? = null
    private var tvDraws: TextView? = null
    private var tvSurvivalRate: TextView? = null
    private var tvSurvivedWins: TextView? = null

    private var tvMainBatteryAcc: TextView? = null
    private var tvTorpAcc: TextView? = null

    private var lastBattleTime: TextView? = null
    private var createdOnTime: TextView? = null

    private var formatter: MyFormatter? = null
    private var yFormatter: MyYFormatter? = null

    private var tvCARating: TextView? = null
    private var tvCADiff: TextView? = null

    private var tvSpottingDamage: TextView? = null
    private var tvArgoDamage: TextView? = null
    private var tvBuildingDamage: TextView? = null
    private var tvArgoTorpDamage: TextView? = null
    private var tvSuppressionCount: TextView? = null
    private var tvSpottingCount: TextView? = null

    private var aPrivateArea: View? = null
    private var tvPrivateGold: TextView? = null
    private var tvPrivateCredits: TextView? = null
    private var tvPrivateFreeExp: TextView? = null
    private var tvPrivateSlots: TextView? = null
    private var tvPrivateBattleTime: TextView? = null
    private var tvPrivatePremiumExpiresOn: TextView? = null

    private var aAverage: View? = null

    private var chartAverages: RadarChart? = null
    private var gAverages: NonScrollableGridView? = null
    private var averagesAdapter: AveragesAdapter? = null

    private var aOtherStats: LinearLayout? = null

    private var chartGamemodes: PieChart? = null

    private var chartWRModes: BarChart? = null

    private var chartSurvivalRate: BarChart? = null

    private var chartAvgDmg: HorizontalBarChart? = null

    private var tvGameModeTitle: View? = null

    private var aCARatingArea: View? = null

    private var chartCAContribution: BarChart? = null
    private var chartCARatingPerTier: BarChart? = null
    private var ivBreakdown: ImageView? = null
    private var aBreakdown: View? = null
    private var aBreakdownCharts: View? = null

    private val swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_captain, container, false)
        bindView(view)
        return view
    }

    private fun bindView(view: View) {
        aAverageDamage = view.findViewById(R.id.captain_damage_area)
        aKillDeath = view.findViewById(R.id.captain_kills_area)
        aAverageExp = view.findViewById(R.id.captain_experience_area)
        aWinRate = view.findViewById(R.id.captain_winning_area)
        aBattles = view.findViewById(R.id.captain_battles_area)

        tvBattles = view.findViewById(R.id.captain_battles)
        tvWinRate = view.findViewById(R.id.captain_win_rate)
        tvAverageExp = view.findViewById(R.id.captain_avg_exp)
        tvAverageDamage = view.findViewById(R.id.captain_avg_dmg)
        tvKillDeath = view.findViewById(R.id.captain_k_d)

        //        if(CAApp.isLightTheme(view.getContext())){
//            ((ImageView) view.findViewById(R.id.captain_battles_iv)).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
//            ((ImageView) view.findViewById(R.id.captain_win_rate_iv)).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
//            ((ImageView) view.findViewById(R.id.captain_avg_exp_iv)).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
//            ((ImageView) view.findViewById(R.id.captain_damage_iv)).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
//            ((ImageView) view.findViewById(R.id.captain_k_d_iv)).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
//        }
        tvGenXP = view.findViewById(R.id.captain_general_total_xp)
        tvGenDamage = view.findViewById(R.id.captain_general_total_damage)
        tvGenPlanesKilled = view.findViewById(R.id.captain_general_planes_killed)
        tvGenCapture = view.findViewById(R.id.captain_general_capture_points)
        tvGenDropped = view.findViewById(R.id.captain_general_defender_points)
        tvGenProfileLevel = view.findViewById(R.id.captain_general_profile_level)

        chartProgress = view.findViewById(R.id.captain_graphs_progress)

        topicalArea = view.findViewById(R.id.captain_graph_topical_area)
        topicalText = view.findViewById(R.id.captain_graph_topical_text)
        topicalChart = view.findViewById(R.id.captain_graph_topical_line)
        tvTierAverage = view.findViewById(R.id.captain_graph_tier_average)
        tiersChart = view.findViewById(R.id.captain_graphs_tier)
        tvTopicalDescription = view.findViewById(R.id.captain_graph_description)

        chartGamePerType = view.findViewById(R.id.captain_graphs_games_per_type)
        chartGamePerNation = view.findViewById(R.id.captain_graphs_games_per_nation)

        pbDistanceTraveled = view.findViewById(R.id.captain_distance_traveled_progress)
        tvDistanceTraveled = view.findViewById(R.id.captain_distance_traveled)
        tvDistanceTotal = view.findViewById(R.id.captain_distance_traveled_text)

        //        captainAddView = view.findViewById(R.id.captain_checkbox_area);
//        captainCheckBox = (CheckBox) view.findViewById(R.id.captain_checkbox);
        tvWins = view.findViewById(R.id.captain_general_wins)
        tvLosses = view.findViewById(R.id.captain_general_losses)
        tvDraws = view.findViewById(R.id.captain_general_draws)

        tvMainBatteryAcc = view.findViewById(R.id.captain_general_main_accuracy)
        tvTorpAcc = view.findViewById(R.id.captain_general_torp_accuracy)

        tvTotalCaptures = view.findViewById(R.id.captain_general_total_captures)
        tvTotalDefReset = view.findViewById(R.id.captain_general_total_def_points)
        tvTotalPlanes = view.findViewById(R.id.captain_general_total_planes)

        lastBattleTime = view.findViewById(R.id.captain_general_last_battle)
        createdOnTime = view.findViewById(R.id.captain_general_created_date)

        tvSurvivalRate = view.findViewById(R.id.captain_general_survival_rate)
        tvSurvivedWins = view.findViewById(R.id.captain_general_survived_wins)

        tvCARating = view.findViewById(R.id.averages_car)
        tvCADiff = view.findViewById(R.id.averages_car_dif)

        aAverage = view.findViewById(R.id.averages_grid_area)

        gAverages = view.findViewById(R.id.averages_grid)
        chartAverages = view.findViewById(R.id.averages_chart)

        aPrivateArea = view.findViewById(R.id.captain_private_area)

        chartGamemodes = view.findViewById(R.id.captain_graphs_games_per_mode)
        chartWRModes = view.findViewById(R.id.captain_graphs_win_rate_per_mode)
        chartAvgDmg = view.findViewById(R.id.captain_graphs_avg_dmg_per_mode)
        chartSurvivalRate = view.findViewById(R.id.captain_graphs_survival_rate_per_mode)

        tvGameModeTitle = view.findViewById(R.id.captain_game_mode_title)

        tvPrivateGold = view.findViewById(R.id.captain_private_gold)
        tvPrivateCredits = view.findViewById(R.id.captain_private_credits)
        tvPrivateBattleTime = view.findViewById(R.id.captain_private_battle_time)
        tvPrivateFreeExp = view.findViewById(R.id.captain_private_free_exp)
        tvPrivatePremiumExpiresOn = view.findViewById(R.id.captain_private_premium)
        tvPrivateSlots = view.findViewById(R.id.captain_private_slots)

        tvSpottingDamage = view.findViewById(R.id.captain_general_total_spotting)
        tvArgoDamage = view.findViewById(R.id.captain_general_total_argo)
        tvBuildingDamage = view.findViewById(R.id.captain_general_total_building)
        tvArgoTorpDamage = view.findViewById(R.id.captain_general_total_torp_argo)

        tvSuppressionCount = view.findViewById(R.id.captain_general_total_supressions)
        tvSpottingCount = view.findViewById(R.id.captain_general_total_spots)

        aCARatingArea = view.findViewById(R.id.averages_ca_rating_top_area)

        aOtherStats = view.findViewById(R.id.captain_statistics_area)

        chartCAContribution = view.findViewById(R.id.averages_contribution_chart)
        chartCARatingPerTier = view.findViewById(R.id.averages_ca_per_tier_chart)
        ivBreakdown = view.findViewById(R.id.averages_ca_rating_breakdown)
        aBreakdown = view.findViewById(R.id.ca_rating_breakdown_area)
        aBreakdownCharts = view.findViewById(R.id.averages_contribution_chart_area)

        bindSwipe(view)
        initSwipeLayout()

        setUpCard(view, R.id.captain_general_area)
        setUpCard(view, R.id.captain_private_area)
    }

    override fun onResume() {
        super.onResume()
        eventBus.register(this)
        initView()
    }

    private fun initView() {
        var captain: Captain? = null
        try {
            captain = (activity as ICaptain?)!!.getCaptain(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (captain != null && captain.details != null) {
            refreshing(false)
            val details = captain.details
            val battles = details.battles.toFloat()

            //dates
            val df = getDayMonthYearFormatter(requireActivity())
            val lastBattle = Calendar.getInstance()
            lastBattle.timeInMillis = captain.details.lastBattleTime * 1000
            lastBattleTime!!.text = df.format(lastBattle.time)

            val createdOn = Calendar.getInstance()
            createdOn.timeInMillis = captain.details.createdAt * 1000
            createdOnTime!!.text = df.format(createdOn.time)

            tvGenProfileLevel!!.text = "" + details.tierLevel

            if (battles > 0) {
                val bigNumFormatter = DecimalFormat("###,###,###")
                tvBattles!!.text = defaultDecimalFormatter.format(battles.toDouble())

                val avgDamage = (details.totalDamage / battles).toInt()
                tvAverageDamage!!.text = "" + avgDamage

                val avgExp = (details.totalXP / battles).toInt()
                tvAverageExp!!.text = avgExp.toString() + ""
                var kdBattles = battles
                if (kdBattles != details.survivedBattles.toFloat()) {
                    kdBattles = battles - details.survivedBattles
                }
                val kd = details.frags.toFloat() / kdBattles
                tvKillDeath!!.text = defaultDecimalFormatter.format(kd.toDouble())

                val winRate = (details.wins.toFloat() / battles) * 100
                tvWinRate!!.text = defaultDecimalFormatter.format(winRate.toDouble()) + "%"

                tvGenXP!!.text = bigNumFormatter.format(details.totalXP)
                var totalDamage = details.totalDamage
                if (totalDamage > 1000000) {
                    totalDamage = totalDamage / 1000000
                    tvGenDamage!!.text =
                        defaultDecimalFormatter.format(totalDamage) + getString(R.string.million)
                } else {
                    tvGenDamage!!.text = "" + totalDamage
                }

                tvGenDropped!!.text =
                    defaultDecimalFormatter.format((details.droppedCapturePoints.toFloat() / battles).toDouble())
                tvGenCapture!!.text =
                    defaultDecimalFormatter.format((details.capturePoints.toFloat() / battles).toDouble())
                tvGenPlanesKilled!!.text =
                    defaultDecimalFormatter.format((details.planesKilled.toFloat() / battles).toDouble())

                tvTotalPlanes!!.text = bigNumFormatter.format(details.planesKilled.toLong())
                tvTotalCaptures!!.text = bigNumFormatter.format(details.capturePoints.toLong())
                tvTotalDefReset!!.text =
                    bigNumFormatter.format(details.droppedCapturePoints.toLong())

                tvTierAverage!!.text =
                    getString(R.string.average_tier) + ": " + defaultDecimalFormatter.format(details.averageTier.toDouble())

                if (details.mainBattery.shots > 0) tvMainBatteryAcc!!.text =
                    oneDepthDecimalFormatter.format((details.mainBattery.hits / details.mainBattery.shots.toFloat() * 100f).toDouble()) + "%"
                if (details.torpedoes.shots > 0) tvTorpAcc!!.text =
                    oneDepthDecimalFormatter.format((details.torpedoes.hits / details.torpedoes.shots.toFloat() * 100f).toDouble()) + "%"

                tvWins!!.text = "" + details.wins
                tvLosses!!.text = "" + details.losses
                tvDraws!!.text = "" + details.draws

                tvSurvivalRate!!.text =
                    oneDepthDecimalFormatter.format(((details.survivedBattles.toFloat() / battles) * 100).toDouble()) + "%"
                tvSurvivedWins!!.text =
                    oneDepthDecimalFormatter.format(((details.survivedWins.toFloat() / battles) * 100).toDouble()) + "%"

                d("CaptainFragment", "buildings = " + details.buildingDamage)
                var argoDamage = "" + details.totalArgoDamage
                if (details.totalArgoDamage > 1000000) {
                    argoDamage =
                        defaultDecimalFormatter.format(details.totalArgoDamage / 1000000) + getString(
                            R.string.million
                        )
                }
                tvArgoDamage!!.text = argoDamage

                var argoTorpDamage = "" + details.torpArgoDamage
                if (details.totalArgoDamage > 1000000) {
                    argoTorpDamage =
                        defaultDecimalFormatter.format(details.totalArgoDamage / 1000000) + getString(
                            R.string.million
                        )
                }
                tvArgoTorpDamage!!.text = argoTorpDamage

                var buildingDamage = "" + details.buildingDamage
                if (details.buildingDamage > 1000000) {
                    buildingDamage =
                        defaultDecimalFormatter.format(details.buildingDamage / 1000000) + getString(
                            R.string.million
                        )
                }
                tvBuildingDamage!!.text = buildingDamage

                var scoutingDamage = "" + details.scoutingDamage
                if (details.scoutingDamage > 1000000) {
                    scoutingDamage =
                        defaultDecimalFormatter.format(details.scoutingDamage / 1000000) + getString(
                            R.string.million
                        )
                }
                tvSpottingDamage!!.text = scoutingDamage

                tvSpottingCount!!.text = "" + details.spotted
                tvSuppressionCount!!.text = "" + details.suppressionCount

                val pref = Prefs(context)
                val hasSeenGraphDescription = pref.getBoolean(SEEN_TOPICAL_GRAPH_DESCRIPTION, false)
                if (!hasSeenGraphDescription) {
                    tvTopicalDescription!!.visibility = View.VISIBLE
                    pref.setBoolean(SEEN_TOPICAL_GRAPH_DESCRIPTION, true)
                }

                setUpCARatingExplantion()
                aCARatingArea!!.visibility = View.VISIBLE

                setupPrivateInformation(captain, bigNumFormatter)

                setUpTopicalArea(captain)

                setUpDistanceArea(details)

                setUpCharts(captain)

                setUpAverages(createCapIdStr(captain.server, captain.id), captain.details)

                setUpOtherStatistics(captain)
            } else {
                if (captain.ships == null) refreshing(true)
                tvBattles!!.text = "0"
                tvWinRate!!.text = "0.0%"
                tvAverageDamage!!.text = "0"
                tvAverageExp!!.text = "0"
                tvKillDeath!!.text = "0"
            }
        } else {
            refreshing(true)
        }
    }

    private fun setUpCARatingExplantion() {
        aAverage!!.isClickable = true
        aAverage!!.setOnClickListener {
            val builder = AlertDialog.Builder(
                requireActivity()
            )
            builder.setTitle(getString(R.string.ca_rating_dialog_title))
            builder.setMessage(
                getString(
                    R.string.ca_rating_tl_dr,
                    Math.round(CARatingManager.DAMAGE_COEF * 100).toString() + "%",
                    Math.round(CARatingManager.KILLS_COEF * 100).toString() + "%",
                    Math.round(CARatingManager.WR_COEF * 100).toString() + "%"
                )
            )
            builder.setPositiveButton(getString(R.string.learn_more)) { dialog, which -> //move to information on CA Rating
                val i = Intent(context, InformationActivity::class.java)
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
                dialog.dismiss()
            }
            builder.setNegativeButton(R.string.dismiss) { dialog, which -> dialog.dismiss() }
            builder.show()
        }
    }

    private fun setUpDistanceArea(details: CaptainDetails) {
        //distance traveled stuff
        val circumferance = 24902f // miles
        val traveled = details.distanceTraveled.toFloat()
        val timesAround = traveled / circumferance
        val progress = (((traveled % circumferance) / circumferance) * 100f).toInt()
        pbDistanceTraveled!!.progress = progress
        tvDistanceTraveled!!.text = defaultDecimalFormatter.format(timesAround.toDouble())
        chartProgress!!.visibility = View.VISIBLE
        val sb = StringBuilder()
        val kilos = details.distanceTraveled * 1.60934f //kilos
        val format = DecimalFormat("###,###,###")
        sb.append(format.format(kilos.toDouble()) + "km / ")
        sb.append(format.format(details.distanceTraveled.toLong()) + "m")
        tvDistanceTotal!!.text = sb.toString()
    }

    private fun setUpTopicalArea(captain: Captain) {
        if (!fromSearch(requireContext(), captain.server, captain.id)) {
            //set up line graph with the last clicked top section
            val onClick = View.OnClickListener { v ->
                val tag = v.tag as String
                val prefs = Prefs(v.context)
                prefs.setString(SELECTED_GRAPH, tag)
                //check background
                checkBackgrounds(tag)
                //setUpGraph
                setUpTopicalChart(tag)
            }
            aAverageDamage!!.setOnClickListener(onClick)
            aAverageDamage!!.tag = TAG_AVERAGE_DAMAGE
            aBattles!!.setOnClickListener(onClick)
            aBattles!!.tag = TAG_BATTLES
            aWinRate!!.setOnClickListener(onClick)
            aWinRate!!.tag = TAG_WIN_RATE
            aKillDeath!!.setOnClickListener(onClick)
            aKillDeath!!.tag = TAG_KILL_DEATH
            aAverageExp!!.setOnClickListener(onClick)
            aAverageExp!!.tag = TAG_AVERAGE_EXP

            val prefs = Prefs(context)
            var selectedGraph = prefs.getString(SELECTED_GRAPH, "")
            if (TextUtils.isEmpty(selectedGraph)) selectedGraph = TAG_AVERAGE_EXP
            checkBackgrounds(selectedGraph)
            setUpTopicalChart(selectedGraph)
            topicalArea!!.visibility = View.VISIBLE
        } else {
            topicalArea!!.visibility = View.GONE
        }
    }

    private fun setupPrivateInformation(captain: Captain, bigNumFormatter: DecimalFormat) {
        if (captain.information != null) {
            aPrivateArea!!.visibility = View.VISIBLE
            val info = captain.information
            val filledSlots = info.slots - info.emptySlots
            tvPrivateSlots!!.text = filledSlots.toString() + " / " + info.slots
            tvPrivateFreeExp!!.text = bigNumFormatter.format(info.freeExp.toLong())
            tvPrivateCredits!!.text = bigNumFormatter.format(info.credits)
            tvPrivateGold!!.text = bigNumFormatter.format(info.gold.toLong())

            val battleTimeS = info.battleTime

            var battleTimeM = (battleTimeS / 60).toInt()
            var battleTimeH = battleTimeM / 60
            var battleTimeD = battleTimeH / 24
            var battleTimeW = battleTimeD / 7
            val battleTimeY = battleTimeD / 365

            //            Dlog.d("CaptainFragment", "hours = " + battleTimeH + " s = " + battleTimeS);
            battleTimeW = battleTimeW % 52
            battleTimeD = battleTimeD % 7
            battleTimeH = battleTimeH % 24
            battleTimeM = battleTimeM % 60

            val sb = StringBuilder()
            if (battleTimeY > 0) {
                sb.append(battleTimeY.toString() + " " + (if (battleTimeY > 1) "years" else "year"))
                sb.append(" ")
            }
            if (battleTimeW > 0) {
                sb.append(battleTimeW.toString() + " " + (if (battleTimeW > 1) "weeks" else "week"))
                sb.append(" ")
            }
            if (battleTimeD > 0) {
                sb.append(battleTimeD.toString() + " " + (if (battleTimeD > 1) "days" else "day"))
                sb.append(" ")
            }
            if (battleTimeH > 0) {
                sb.append(battleTimeH.toString() + " " + (if (battleTimeH > 1) "hours" else "hour"))
                sb.append(" ")
            }
            if (battleTimeM > 0) {
                sb.append(battleTimeM.toString() + " " + (if (battleTimeM > 1) "minutes" else "minute"))
                sb.append(" ")
            }
            tvPrivateBattleTime!!.text = sb.toString()

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = info.premiumExpiresAt
            tvPrivatePremiumExpiresOn!!.text = getDayMonthYearFormatter(
                tvPrivatePremiumExpiresOn!!.context
            ).format(calendar.time)
        } else {
            aPrivateArea!!.visibility = View.GONE
        }
    }

    private fun checkBackgrounds(tag: String?) {
        if (tag == TAG_AVERAGE_EXP) {
            aAverageExp!!.setBackgroundResource(R.color.captain_top_background)
        } else {
            aAverageExp!!.setBackgroundResource(R.color.transparent)
        }
        if (tag == TAG_KILL_DEATH) {
            aKillDeath!!.setBackgroundResource(R.color.captain_top_background)
        } else {
            aKillDeath!!.setBackgroundResource(R.color.transparent)
        }
        if (tag == TAG_WIN_RATE) {
            aWinRate!!.setBackgroundResource(R.color.captain_top_background)
        } else {
            aWinRate!!.setBackgroundResource(R.color.transparent)
        }
        if (tag == TAG_AVERAGE_DAMAGE) {
            aAverageDamage!!.setBackgroundResource(R.color.captain_top_background)
        } else {
            aAverageDamage!!.setBackgroundResource(R.color.transparent)
        }
        if (tag == TAG_BATTLES) {
            aBattles!!.setBackgroundResource(R.color.captain_top_background)
        } else {
            aBattles!!.setBackgroundResource(R.color.transparent)
        }
    }

    private fun setUpTopicalChart(tag: String?) {
        val runnable = Runnable {
            try {
                var captain: Captain? = null
                try {
                    captain = (activity as ICaptain?)!!.getCaptain(context)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val details = getPlayerStats(
                    requireContext(), getCapIdStr(captain)
                )
                val nameValues: MutableList<String> = ArrayList()
                val numbers: MutableList<Float> = ArrayList()
                val reversedDetails = details.details
                Collections.reverse(reversedDetails)

                setUpCADiff(captain, reversedDetails)

                var strResId = R.string.captain_average_exp
                for (detail in reversedDetails) {
                    val battles = detail!!.battles.toFloat()
                    nameValues.add(battles.toInt().toString() + "")
                    if (tag == TAG_AVERAGE_EXP) {
                        val avgExp = detail.totalXP / battles
                        numbers.add(avgExp)
                    } else if (tag == TAG_AVERAGE_DAMAGE) {
                        strResId = R.string.captain_average_damage
                        val avgDamage = detail.totalDamage.toFloat() / battles
                        numbers.add(avgDamage)
                    } else if (tag == TAG_BATTLES) {
                        strResId = R.string.captain_battles
                        val survivalRate = (detail.survivedBattles / battles) * 100
                        numbers.add(survivalRate)
                    } else if (tag == TAG_KILL_DEATH) {
                        strResId = R.string.captain_kills_deaths
                        var deaths = (battles - detail.survivedBattles.toFloat())
                        if (deaths <= 1) {
                            deaths = 1f
                        }
                        val kd = detail.frags.toFloat() / deaths
                        numbers.add(kd)
                    } else if (tag == TAG_WIN_RATE) {
                        strResId = R.string.captain_winrate
                        val winRate = (detail.wins.toFloat() / battles) * 100
                        numbers.add(winRate)
                    }
                }
                val topicalStrRes = strResId
                val yVals: MutableList<Entry> = ArrayList()
                for (i in numbers.indices) {
                    yVals.add(Entry(numbers[i], i))
                }
                if (yVals.size > 0) {
                    requireActivity().runOnUiThread {
                        topicalText!!.setText(topicalStrRes)
                        topicalChart!!.clear()

                        val colorblind = isColorblind(topicalChart!!.context)
                        val textColor = getTextColor(topicalChart!!.context)
                        val accentColor = if (!colorblind) (if (getTheme(
                                topicalChart!!.context
                            ) == "ocean"
                        ) ContextCompat.getColor(
                            topicalChart!!.context, R.color.graph_line_color
                        ) else ContextCompat.getColor(
                            topicalChart!!.context, R.color.top_background
                        ))
                        else ContextCompat.getColor(topicalChart!!.context, R.color.white)

                        topicalChart!!.isDoubleTapToZoomEnabled = false
                        topicalChart!!.setPinchZoom(false)

                        topicalChart!!.setDescription("")

                        topicalChart!!.isDragEnabled = false
                        topicalChart!!.setScaleEnabled(false)
                        topicalChart!!.setDrawGridBackground(false)

                        topicalChart!!.legend.isEnabled = false

                        val xAxis = topicalChart!!.xAxis
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.textColor = textColor
                        xAxis.setDrawGridLines(true)

                        val yAxis = topicalChart!!.axisRight
                        yAxis.isEnabled = false

                        val yAxis2 = topicalChart!!.axisLeft
                        yAxis2.setLabelCount(6, true)
                        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                        yAxis2.textColor = textColor
                        if (yFormatter == null) yFormatter = MyYFormatter()
                        yFormatter!!.change(tag)
                        yAxis2.valueFormatter = yFormatter

                        val set = LineDataSet(yVals, "")

                        set.color = accentColor
                        set.lineWidth = 2f
                        set.circleRadius = 3f
                        set.fillColor = accentColor
                        set.setDrawValues(false)
                        set.setCircleColor(accentColor)

                        val dataSets: MutableList<ILineDataSet> = ArrayList()
                        dataSets.add(set)

                        val data = LineData(nameValues, dataSets)

                        if (formatter == null) formatter = MyFormatter()
                        formatter!!.change(tag)
                        data.setValueFormatter(formatter)

                        topicalChart!!.data = data
                        topicalChart!!.requestLayout()
                        chartProgress!!.visibility = View.GONE
                        topicalChart!!.animateX(750)
                    }
                } else {
                    topicalArea!!.visibility = View.VISIBLE
                    chartProgress!!.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        Thread(runnable).start()
        chartProgress!!.visibility = View.VISIBLE
    }

    private fun setUpCADiff(captain: Captain?, reversedDetails: List<CaptainDetails?>) {
        try {
            val last = reversedDetails[reversedDetails.size - 2]
            val currentRating = captain!!.details.caRating
            if (last != null) {
                tvCARating!!.post {
                    if (last.caRating != 0f) {
                        d("LastCARating", "last = " + last.caRating + " cur = " + currentRating)
                        val dif = currentRating - last.caRating
                        if (abs(dif.toDouble()) > 0) {
                            val sb = StringBuilder()
                            if (dif > 0) {
                                sb.append("+")
                            }
                            sb.append(oneDepthDecimalFormatter.format(dif.toDouble()))
                            tvCADiff!!.text = sb.toString()
                            var colorResId = R.color.average_down
                            if (dif > 0) {
                                colorResId = R.color.average_up
                            }
                            tvCADiff!!.setTextColor(
                                ContextCompat.getColor(
                                    tvCARating!!.context,
                                    colorResId
                                )
                            )
                            tvCADiff!!.visibility = View.VISIBLE
                        } else {
                            tvCADiff!!.visibility = View.GONE
                        }
                    } else {
                        tvCADiff!!.visibility = View.GONE
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun setUpAverages(captainId: String, captain: CaptainDetails) {
        val prefs = Prefs(chartAverages!!.context)
        val showCompare = prefs.getBoolean(SettingActivity.SHOW_COMPARE, true)
        if (showCompare) {
            aAverage!!.visibility = View.VISIBLE
            ivBreakdown!!.visibility = View.VISIBLE
            aBreakdown!!.setOnClickListener {
                if (aBreakdownCharts!!.visibility == View.GONE) {
                    aBreakdownCharts!!.visibility = View.VISIBLE
                    ivBreakdown!!.setImageResource(R.drawable.ic_collapse)
                } else {
                    aBreakdownCharts!!.visibility = View.GONE
                    ivBreakdown!!.setImageResource(R.drawable.ic_expand)
                }
            }
            aBreakdownCharts!!.setOnClickListener {
                aBreakdownCharts!!.visibility = View.GONE
                ivBreakdown!!.setImageResource(R.drawable.ic_expand)
            }
            if (aBreakdownCharts!!.visibility == View.GONE) {
                ivBreakdown!!.setImageResource(R.drawable.ic_expand)
            } else {
                ivBreakdown!!.setImageResource(R.drawable.ic_collapse)
            }
            val runnable: Runnable = object : Runnable {
                override fun run() {
                    setUpAveragesRadarChart()
                }

                private fun cleanTitleString(id: Int): String {
                    var str = getString(id)
                    if (str.length > 14) {
                        str = str.substring(0, 15).trim { it <= ' ' } + "..."
                    }
                    return str
                }

                private fun setUpAveragesRadarChart() {
                    try {
                        val isColorblind = isColorblind(chartAverages!!.context)
                        val color = if (!isColorblind) (if (getTheme(
                                topicalChart!!.context
                            ) == "ocean"
                        ) ContextCompat.getColor(
                            topicalChart!!.context, R.color.graph_line_color
                        ) else ContextCompat.getColor(
                            topicalChart!!.context, R.color.top_background
                        ))
                        else ContextCompat.getColor(topicalChart!!.context, R.color.white)
                        val textColor = getTextColor(chartAverages!!.context)
                        val webTextColor =
                            ContextCompat.getColor(chartAverages!!.context, R.color.web_text_color)

                        chartAverages!!.setDescription(getString(R.string.baseline))
                        chartAverages!!.setDescriptionColor(webTextColor)

                        chartAverages!!.webLineWidth = 1.0f
                        chartAverages!!.webAlpha = 200
                        chartAverages!!.webLineWidthInner = 0.75f
                        chartAverages!!.webColor =
                            ContextCompat.getColor(chartAverages!!.context, R.color.transparent)
                        chartAverages!!.webColorInner =
                            ContextCompat.getColor(chartAverages!!.context, R.color.transparent)

                        chartAverages!!.setTouchEnabled(false)
                        chartAverages!!.isClickable = false
                        chartAverages!!.isFocusableInTouchMode = false

                        val mv =
                            RadarMarkerView(chartAverages!!.context, R.layout.custom_marker_view)
                        chartAverages!!.markerView = mv

                        val xAxis = chartAverages!!.xAxis
                        xAxis.textSize = 9f
                        xAxis.textColor = textColor

                        val yAxis = chartAverages!!.yAxis
                        yAxis.setLabelCount(4, false)
                        yAxis.textSize = 9f
                        yAxis.textColor = webTextColor
                        yAxis.setAxisMinValue(0f)

                        val l = chartAverages!!.legend
                        l.isEnabled = false

                        val titles: MutableList<String> = ArrayList()
                        titles.add(cleanTitleString(R.string.damage))
                        titles.add(cleanTitleString(R.string.short_kills_game))
                        titles.add(cleanTitleString(R.string.short_win_rate))
                        titles.add(cleanTitleString(R.string.short_cap_points))

                        //                        titles.add(cleanTitleString(R.string.short_def_reset));
//                        titles.add(cleanTitleString(R.string.short_planes_game));
                        val yVals: MutableList<Entry> = ArrayList()

                        yVals.add(Entry((captain.getcDamage() / captain.expectedDamage) * 100, 0))
                        yVals.add(Entry((captain.getcKills() / captain.expectedKills) * 100, 1))
                        yVals.add(Entry((captain.getcWinRate() / captain.expectedWinRate) * 100, 2))
                        yVals.add(Entry((captain.getcPlanes() / captain.expectedPlanes) * 100, 3))

                        //                        yVals.add(new Entry((captain.getcCaptures() / captain.getExpectedCaptures()) * 100, 3));
//                        yVals.add(new Entry((captain.getcDefReset() / captain.getExpectedDefReset()) * 100, 4));
                        val set = RadarDataSet(yVals, "")
                        set.color = color
                        set.setDrawFilled(true)
                        set.lineWidth = 2f

                        val sets: MutableList<IRadarDataSet> = ArrayList()
                        sets.add(set)

                        val data = RadarData(titles, sets)
                        data.setValueTextColor(textColor)
                        data.setValueTextSize(8f)

                        chartAverages!!.skipWebLineCount = 2
                        chartAverages!!.data = data
                        chartAverages!!.invalidate()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            chartAverages!!.post(runnable)

            val caRating = captain.caRating
            tvCARating!!.text = oneDepthDecimalFormatter.format(caRating.toDouble())

            val averages: MutableList<ListAverages> = ArrayList()
            averages.add(
                ListAverages.create(
                    getString(R.string.damage),
                    captain.getcDamage(),
                    captain.expectedDamage,
                    AverageType.LARGE_NUMBER
                )
            )
            averages.add(
                ListAverages.create(
                    getString(R.string.kills_game),
                    captain.getcKills(),
                    captain.expectedKills,
                    AverageType.DEFAULT
                )
            )
            averages.add(
                ListAverages.create(
                    getString(R.string.win_rate),
                    captain.getcWinRate(),
                    captain.expectedWinRate,
                    AverageType.PERCENT
                )
            )

            averages.add(
                ListAverages.create(
                    getString(R.string.planes_downed_game),
                    captain.getcPlanes(),
                    captain.expectedPlanes,
                    AverageType.DEFAULT
                )
            )

            averagesAdapter = AveragesAdapter(context, R.layout.list_averages, averages)
            gAverages!!.adapter = averagesAdapter
        } else {
            aAverage!!.visibility = View.GONE
        }
    }

    private fun setUpCharts(captain: Captain) {
        val runnable = Runnable {
            try {
                val battleCounts = SparseArray<Int?>()
                val gamesPerType: MutableMap<String, Int> = HashMap()
                val gamesPerNation: MutableMap<String, Int> = HashMap()

                val ratingsPerTier = SparseArray<Float>()
                val shipsPerTier = SparseArray<Float>() // this is to average the ratings
                val battlePerTier = SparseArray<Float>()

                val shipsHolder = infoManager!!.getShipInfo(requireContext())
                for (s in captain.ships) {
                    val info = shipsHolder[s.shipId]
                    if (info != null) {
                        val tier = info.tier
                        val battleCount = battleCounts[tier]
                        if (battleCount != null) {
                            battleCounts.put(tier, battleCount + s.battles)
                        } else {
                            battleCounts.put(tier, s.battles)
                        }
                        val gameType = gamesPerType[info.type]
                        if (gameType != null) {
                            gamesPerType[info.type] = gameType + s.battles
                        } else {
                            gamesPerType[info.type] = s.battles
                        }
                        val nationType = gamesPerNation[info.nation]
                        if (nationType != null) {
                            gamesPerNation[info.nation] = nationType + s.battles
                        } else {
                            gamesPerNation[info.nation] = s.battles
                        }
                        GetCaptainTask.addTierNumber(ratingsPerTier, tier, s.caRating)
                        GetCaptainTask.addTierNumber(shipsPerTier, tier, 1f)
                        GetCaptainTask.addTierNumber(battlePerTier, tier, s.battles.toFloat())
                    }
                }
                val caPerTier: MutableMap<String, Float?> = HashMap()
                val caContribPerTier: MutableMap<String, Float?> = HashMap()
                for (i in 1..shipsPerTier.size()) {
                    val ratingTotal = ratingsPerTier[i]
                    val shipsTotal = shipsPerTier[i]
                    val battlesTotal = battlePerTier[i]

                    if (ratingTotal != null && shipsTotal != null && battlesTotal != null && battlesTotal > 0) {
                        // avgRating = total rating per tier / ship per tier
                        val tierRatingAverage = ratingTotal / shipsTotal
                        // percentageRatio = total battles per tier / total games
                        val tierRatio = battlesTotal / captain.details.battles
                        //                            Dlog.d("CARating", tierRatingAverage + " ratio = " + tierRatio + " tier = " + i + " ratio = " + (tierRatingAverage * tierRatio));
                        caPerTier[i.toString() + ""] = tierRatingAverage

                        caContribPerTier[i.toString() + ""] = tierRatingAverage * tierRatio
                    }
                }

                val modeStrings: MutableList<String> = ArrayList()
                val gamesPerMode: MutableMap<String, Int> = HashMap()
                val winRatePerMode: MutableMap<String, Float> = HashMap()
                val avgDamageMode: MutableMap<String, Float> = HashMap()
                val survivalRateMode: MutableMap<String, Float> = HashMap()
                val soloBattles = (captain.details.battles
                        - captain.pveDetails.battles
                        - captain.pvpDiv2Details.battles
                        - captain.pvpDiv3Details.battles
                        - captain.teamBattleDetails.battles)
                if (soloBattles > 0) {
                    val str = getString(R.string.solo_pvp)
                    modeStrings.add(str)
                    gamesPerMode[str] = soloBattles
                    val winRate = (captain.details.wins / captain.details.battles.toFloat()) * 100
                    winRatePerMode[str] = winRate
                    avgDamageMode[str] =
                        (captain.details.totalDamage / captain.details.battles.toFloat()).toFloat()
                    val survivedWins =
                        (captain.details.survivedWins / captain.details.battles.toFloat()) * 100
                    survivalRateMode[str] = survivedWins
                }
                if (captain.pveDetails.battles > 0) {
                    val str = getString(R.string.pve)
                    modeStrings.add(str)
                    gamesPerMode[str] = captain.pveDetails.battles
                    val winRate =
                        (captain.pveDetails.wins / captain.pveDetails.battles.toFloat()) * 100
                    winRatePerMode[str] = winRate
                    avgDamageMode[str] =
                        (captain.pveDetails.totalDamage / captain.pveDetails.battles.toFloat()).toFloat()
                    val survivedWins =
                        (captain.pveDetails.survivedWins / captain.pveDetails.battles.toFloat()) * 100
                    survivalRateMode[str] = survivedWins
                }
                if (captain.pvpDiv2Details.battles > 0) {
                    val str = getString(R.string.pvp_2_div)
                    modeStrings.add(str)
                    gamesPerMode[str] = captain.pvpDiv2Details.battles
                    val winRate =
                        (captain.pvpDiv2Details.wins / captain.pvpDiv2Details.battles.toFloat()) * 100
                    winRatePerMode[str] = winRate
                    avgDamageMode[str] =
                        (captain.pvpDiv2Details.totalDamage / captain.pvpDiv2Details.battles.toFloat()).toFloat()
                    val survivedWins =
                        (captain.pvpDiv2Details.survivedWins / captain.pvpDiv2Details.battles.toFloat()) * 100
                    survivalRateMode[str] = survivedWins
                }
                if (captain.pvpDiv3Details.battles > 0) {
                    val str = getString(R.string.pvp_3_div)
                    modeStrings.add(str)
                    gamesPerMode[str] = captain.pvpDiv3Details.battles
                    val winRate =
                        (captain.pvpDiv3Details.wins / captain.pvpDiv3Details.battles.toFloat()) * 100
                    winRatePerMode[str] = winRate
                    avgDamageMode[str] =
                        (captain.pvpDiv3Details.totalDamage / captain.pvpDiv3Details.battles.toFloat()).toFloat()
                    val survivedWins =
                        (captain.pvpDiv3Details.survivedWins / captain.pvpDiv3Details.battles.toFloat()) * 100
                    survivalRateMode[str] = survivedWins
                }
                if (captain.teamBattleDetails.battles > 0) {
                    val str = getString(R.string.team_battles)
                    modeStrings.add(str)
                    gamesPerMode[str] = captain.teamBattleDetails.battles
                    val winRate =
                        (captain.teamBattleDetails.wins / captain.teamBattleDetails.battles.toFloat()) * 100
                    winRatePerMode[str] = winRate
                    avgDamageMode[str] =
                        (captain.teamBattleDetails.totalDamage / captain.teamBattleDetails.battles.toFloat()).toFloat()
                    val survivedWins =
                        (captain.teamBattleDetails.survivedWins / captain.teamBattleDetails.battles.toFloat()) * 100
                    survivalRateMode[str] = survivedWins
                }
                if (captain.rankedSeasons != null) {
                    var ranked = 0
                    var wins = 0f
                    var damage = 0f
                    var survivedBattles = 0f
                    for (info in captain.rankedSeasons) {
                        try {
                            ranked += info.solo.battles
                            wins += info.solo.wins.toFloat()
                            damage += info.solo.damage.toFloat()
                            survivedBattles += info.solo.survived.toFloat()
                        } catch (e: Exception) {
                        }
                    }
                    if (ranked > 0) {
                        val rankedStr = getString(R.string.ranked)
                        modeStrings.add(rankedStr)
                        gamesPerMode[rankedStr] = ranked
                        val winRate = (wins / ranked.toFloat()) * 100
                        winRatePerMode[rankedStr] = winRate
                        avgDamageMode[rankedStr] = damage / ranked.toFloat()
                        val survivedWins = (survivedBattles / ranked.toFloat()) * 100
                        survivalRateMode[rankedStr] = survivedWins
                    }
                }

                requireActivity().runOnUiThread(object : Runnable {
                    override fun run() {
                        try {
                            setUpTiersChart()

                            setUpGamesTypeChart()

                            setUpGamesNationChart()

                            setUpGamesPerModeChart()

                            setUpWRPerModeChart()

                            setUpAvgDmgPerModeChart()

                            setUpSurvivalRatePerModeChart()

                            setUpCAChart(chartCAContribution, caContribPerTier)

                            setUpCAChart(chartCARatingPerTier, caPerTier)

                            chartProgress!!.visibility = View.GONE
                        } catch (e: Exception) {
                        }
                    }

                    private fun setUpCAChart(chart: BarChart?, map: Map<String, Float?>) {
                        if (map.size > 0) {
                            val textColor = getTextColor(chart!!.context)
                            val colorblind = isColorblind(chart.context)
                            val accentColor = if (!colorblind) (if (getTheme(
                                    topicalChart!!.context
                                ) == "ocean"
                            ) ContextCompat.getColor(
                                topicalChart!!.context, R.color.graph_line_color
                            ) else ContextCompat.getColor(
                                topicalChart!!.context, R.color.top_background
                            ))
                            else ContextCompat.getColor(topicalChart!!.context, R.color.white)
                            chart.setDrawBarShadow(false)
                            chart.setDrawValueAboveBar(false)
                            chart.setPinchZoom(false)
                            chart.isDoubleTapToZoomEnabled = false
                            chart.setDrawGridBackground(false)
                            chart.setDrawValueAboveBar(true)
                            chart.setTouchEnabled(false)

                            val xAxis = chart.xAxis
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            xAxis.textColor = textColor
                            xAxis.setDrawGridLines(true)

                            val yAxis = chart.axisRight
                            yAxis.setLabelCount(6, false)
                            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                            yAxis.textColor = textColor
                            yAxis.isEnabled = false

                            val yAxis2 = chart.axisLeft
                            yAxis2.setLabelCount(6, false)
                            yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                            yAxis2.textColor = textColor

                            val l = chart.legend
                            l.isEnabled = false
                            val xVals: MutableList<String> = ArrayList()
                            for (i in 1..10) xVals.add(i.toString() + "")

                            val yVals: MutableList<BarEntry> = ArrayList()
                            for (i in xVals.indices) {
                                if (map[xVals[i]] != null) yVals.add(BarEntry(map[xVals[i]]!!, i))
                                else yVals.add(BarEntry(0f, i))
                            }

                            val set1 = BarDataSet(yVals, "")
                            set1.color = accentColor
                            set1.barSpacePercent = 20f

                            val dataSets = ArrayList<IBarDataSet>()
                            dataSets.add(set1)

                            val data = BarData(xVals, dataSets)
                            data.setValueTextSize(10f)
                            data.setValueTextColor(textColor)
                            chart.setDescription("")
                            chart.data = data
                            chart.requestLayout()
                        } else {
                        }
                    }

                    private fun setUpGamesPerModeChart() {
                        if (gamesPerMode.size > 0) {
                            val textColor = getTextColor(chartGamemodes!!.context)
                            val colorblind = isColorblind(chartGamemodes!!.context)

                            chartGamemodes!!.isRotationEnabled = false

                            chartGamemodes!!.isDrawHoleEnabled = true
                            chartGamemodes!!.setHoleColor(R.color.transparent)
                            chartGamemodes!!.transparentCircleRadius = 50f
                            chartGamemodes!!.holeRadius = 50f

                            chartGamemodes!!.setDrawSliceText(false)

                            val l = chartGamemodes!!.legend
                            l.textColor = textColor
                            l.position = Legend.LegendPosition.LEFT_OF_CHART
                            l.form = Legend.LegendForm.CIRCLE

                            val xVals = ArrayList<String>()
                            xVals.addAll(modeStrings)

                            val yVals1 = ArrayList<Entry>()
                            for (i in xVals.indices) {
                                val dValue = gamesPerMode[xVals[i]]!!.toDouble()
                                val value = dValue.toFloat()
                                yVals1.add(Entry(value, i))
                            }

                            for (j in xVals.indices) {
                                val name = xVals[j]
                                xVals[j] = name
                            }

                            val colorList: MutableList<Int> = ArrayList()
                            colorList.add(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.solo_pvp_color
                                )
                            )
                            colorList.add(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.pve_color
                                )
                            )
                            colorList.add(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.average_up
                                )
                            )
                            colorList.add(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.div_pvp_3
                                )
                            )
                            colorList.add(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.div_pvp_2
                                )
                            )
                            colorList.add(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.ranked_color
                                )
                            )

                            val dataSet = PieDataSet(yVals1, "")

                            val dataSets = ArrayList<PieDataSet>()
                            dataSets.add(dataSet)

                            dataSet.colors = colorList

                            val data = PieData(xVals, dataSet)
                            data.setValueTextColor(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.black
                                )
                            )
                            data.setValueTextSize(14f)
                            chartGamemodes!!.setDescription("")
                            data.setValueFormatter(LargeValueFormatter())

                            chartGamemodes!!.highlightValues(null)

                            chartGamemodes!!.data = data
                            chartGamemodes!!.requestLayout()
                            tvGameModeTitle!!.visibility = View.VISIBLE
                        } else {
                            tvGameModeTitle!!.visibility = View.GONE
                        }
                    }

                    private fun setUpGamesNationChart() {
                        if (gamesPerNation.size > 1) {
                            val textColor = getTextColor(chartGamePerNation!!.context)
                            val colorblind = isColorblind(
                                chartGamePerNation!!.context
                            )

                            chartGamePerNation!!.isRotationEnabled = false

                            chartGamePerNation!!.isDrawHoleEnabled = true
                            chartGamePerNation!!.setHoleColor(R.color.transparent)
                            chartGamePerNation!!.transparentCircleRadius = 50f
                            chartGamePerNation!!.holeRadius = 50f

                            chartGamePerNation!!.setDrawSliceText(false)


                            val l = chartGamePerNation!!.legend
                            l.textColor = textColor
                            l.position = Legend.LegendPosition.LEFT_OF_CHART
                            l.form = Legend.LegendForm.CIRCLE

                            val xVals = ArrayList<String>()
                            val itea: Iterator<String> = gamesPerNation.keys.iterator()
                            val colorList: MutableList<Int> = ArrayList()
                            while (itea.hasNext()) {
                                val key = itea.next()
                                xVals.add(key)
                                if (key == "ussr") {
                                    colorList.add(Color.parseColor("#F44336")) // RED
                                } else if (key == "germany") {
                                    colorList.add(Color.parseColor("#9E9E9E")) // blackish
                                } else if (key == "usa") {
                                    colorList.add(Color.parseColor("#2196F3")) // Blue
                                } else if (key == "poland") {
                                    colorList.add(Color.parseColor("#FAFA00")) // yellow
                                } else if (key == "japan") {
                                    colorList.add(Color.parseColor("#4CAF50")) // Green
                                } else if (key == "uk") {
                                    colorList.add(Color.parseColor("#E1F5FE")) // whiteish blue
                                }
                            }
                            colorList.add(Color.parseColor("#AAE157"))
                            colorList.add(Color.parseColor("#FF9800"))
                            colorList.add(Color.parseColor("#22FFCB"))
                            colorList.add(Color.parseColor("#795548"))

                            val yVals1 = ArrayList<Entry>()
                            for (i in xVals.indices) {
                                val dValue = gamesPerNation[xVals[i]]!!.toDouble()
                                val value = dValue.toFloat()
                                yVals1.add(Entry(value, i))
                            }

                            for (j in xVals.indices) {
                                val name = xVals[j]
                                val newStr = getNationText(
                                    context!!, name
                                )
                                if (newStr != null) xVals[j] = newStr
                            }


                            val dataSet = PieDataSet(yVals1, "")

                            val dataSets = ArrayList<PieDataSet>()
                            dataSets.add(dataSet)

                            dataSet.colors = colorList

                            val data = PieData(xVals, dataSet)
                            data.setValueTextColor(
                                ContextCompat.getColor(
                                    chartGamePerNation!!.context,
                                    R.color.black
                                )
                            )
                            data.setValueTextSize(14f)
                            chartGamePerNation!!.setDescription("")
                            data.setValueFormatter(LargeValueFormatter())

                            chartGamePerNation!!.highlightValues(null)

                            chartGamePerNation!!.data = data
                            chartGamePerNation!!.requestLayout()
                        }
                    }

                    private fun setUpGamesTypeChart() {
                        if (gamesPerType.size > 0) {
                            val textColor = getTextColor(chartGamePerType!!.context)
                            val colorblind = isColorblind(
                                chartGamePerType!!.context
                            )

                            chartGamePerType!!.setDrawBarShadow(false)
                            chartGamePerType!!.setDrawValueAboveBar(false)
                            chartGamePerType!!.setPinchZoom(false)
                            chartGamePerType!!.isDoubleTapToZoomEnabled = false
                            chartGamePerType!!.setDrawGridBackground(false)
                            chartGamePerType!!.setDrawValueAboveBar(true)
                            chartGamePerType!!.setTouchEnabled(false)

                            val xAxis = chartGamePerType!!.xAxis
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            xAxis.textColor = textColor
                            xAxis.setDrawGridLines(true)

                            val yAxis = chartGamePerType!!.axisRight
                            yAxis.setLabelCount(4, false)
                            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                            yAxis.textColor = textColor
                            yAxis.isEnabled = false

                            val yAxis2 = chartGamePerType!!.axisLeft
                            yAxis2.setLabelCount(6, false)
                            yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                            yAxis2.textColor = textColor

                            val l = chartGamePerType!!.legend
                            l.isEnabled = false

                            val xVals = ArrayList<String>()
                            val itea: Iterator<String> = gamesPerType.keys.iterator()
                            val colorList: MutableList<Int> = ArrayList()
                            while (itea.hasNext()) {
                                val key = itea.next()
                                if (key.equals("cruiser", ignoreCase = true)) {
                                    colorList.add(Color.parseColor("#4CAF50"))
                                } else if (key.equals("battleship", ignoreCase = true)) {
                                    colorList.add(Color.parseColor("#F44336"))
                                } else if (key.equals("aircarrier", ignoreCase = true)) {
                                    colorList.add(Color.parseColor("#673AB7"))
                                } else if (key.equals("destroyer", ignoreCase = true)) {
                                    colorList.add(Color.parseColor("#FDD835"))
                                }

                                xVals.add(key)
                            }
                            colorList.add(Color.parseColor("#009688"))
                            colorList.add(Color.parseColor("#795548"))

                            val yVals1 = ArrayList<BarEntry>()
                            for (i in xVals.indices) {
                                val dValue = gamesPerType[xVals[i]]!!.toDouble()
                                val value = dValue.toFloat()
                                yVals1.add(BarEntry(value, i))
                            }

                            val set1 = BarDataSet(yVals1, "")
                            set1.barSpacePercent = 20f
                            set1.colors = colorList

                            val dataSets = ArrayList<IBarDataSet>()
                            dataSets.add(set1)

                            val data = BarData(xVals, dataSets)
                            data.setValueTextSize(10f)
                            data.setValueTextColor(textColor)
                            data.setValueFormatter(LargeValueFormatter())

                            chartGamePerType!!.setDescription("")

                            chartGamePerType!!.data = data
                            chartGamePerType!!.requestLayout()
                        }
                    }

                    private fun setUpTiersChart() {
                        if (battleCounts.size() > 0) {
                            val textColor = getTextColor(tiersChart!!.context)
                            val colorblind = isColorblind(tiersChart!!.context)
                            val accentColor = if (!colorblind) (if (getTheme(
                                    topicalChart!!.context
                                ) == "ocean"
                            ) ContextCompat.getColor(
                                topicalChart!!.context, R.color.graph_line_color
                            ) else ContextCompat.getColor(
                                topicalChart!!.context, R.color.top_background
                            ))
                            else ContextCompat.getColor(topicalChart!!.context, R.color.white)
                            tiersChart!!.setDrawBarShadow(false)
                            tiersChart!!.setDrawValueAboveBar(false)
                            tiersChart!!.setPinchZoom(false)
                            tiersChart!!.isDoubleTapToZoomEnabled = false
                            tiersChart!!.setDrawGridBackground(false)
                            tiersChart!!.setDrawValueAboveBar(true)
                            tiersChart!!.setTouchEnabled(false)

                            val xAxis = tiersChart!!.xAxis
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            xAxis.textColor = textColor
                            xAxis.setDrawGridLines(true)

                            val yAxis = tiersChart!!.axisRight
                            yAxis.setLabelCount(6, false)
                            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                            yAxis.textColor = textColor
                            yAxis.isEnabled = false

                            val yAxis2 = tiersChart!!.axisLeft
                            yAxis2.setLabelCount(6, false)
                            yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                            yAxis2.textColor = textColor

                            val l = tiersChart!!.legend
                            l.isEnabled = false
                            val xVals: MutableList<String> = ArrayList()
                            for (i in 1..10) xVals.add(i.toString() + "")

                            val yVals: MutableList<BarEntry> = ArrayList()
                            for (i in 0..9) {
                                if (battleCounts[i + 1] != null) yVals.add(
                                    BarEntry(
                                        battleCounts[i + 1]!!.toFloat(), i
                                    )
                                )
                                else yVals.add(BarEntry(0f, i))
                            }

                            val set1 = BarDataSet(yVals, "")
                            set1.color = accentColor
                            set1.barSpacePercent = 20f

                            val dataSets = ArrayList<IBarDataSet>()
                            dataSets.add(set1)

                            val data = BarData(xVals, dataSets)
                            data.setValueTextSize(10f)
                            data.setValueTextColor(textColor)
                            data.setValueFormatter(LargeValueFormatter())
                            tiersChart!!.setDescription("")
                            tiersChart!!.data = data
                            tiersChart!!.requestLayout()
                        }
                    }

                    private fun setUpWRPerModeChart() {
                        if (winRatePerMode.size > 0) {
                            val textColor = getTextColor(chartWRModes!!.context)

                            chartWRModes!!.setDrawBarShadow(false)
                            chartWRModes!!.setDrawValueAboveBar(false)
                            chartWRModes!!.setPinchZoom(false)
                            chartWRModes!!.isDoubleTapToZoomEnabled = false
                            chartWRModes!!.setDrawGridBackground(false)
                            chartWRModes!!.setDrawValueAboveBar(true)
                            chartWRModes!!.setTouchEnabled(false)

                            val xAxis = chartWRModes!!.xAxis
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            xAxis.textColor = textColor
                            xAxis.setDrawGridLines(true)

                            val yAxis = chartWRModes!!.axisRight
                            yAxis.setLabelCount(6, false)
                            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                            yAxis.textColor = textColor
                            yAxis.isEnabled = false

                            val yAxis2 = chartWRModes!!.axisLeft
                            yAxis2.setLabelCount(6, false)
                            yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                            yAxis2.textColor = textColor

                            val colorList: MutableList<Int> = ArrayList()
                            colorList.add(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.solo_pvp_color
                                )
                            )
                            colorList.add(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.pve_color
                                )
                            )
                            colorList.add(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.average_up
                                )
                            )
                            colorList.add(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.div_pvp_3
                                )
                            )
                            colorList.add(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.div_pvp_2
                                )
                            )
                            colorList.add(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.ranked_color
                                )
                            )

                            val l = chartWRModes!!.legend
                            l.isEnabled = false
                            val xVals: MutableList<String> = ArrayList()
                            xVals.addAll(modeStrings)

                            val yVals: MutableList<BarEntry> = ArrayList()
                            for (i in xVals.indices) {
                                yVals.add(BarEntry(winRatePerMode[xVals[i]]!!, i))
                            }

                            val set1 = BarDataSet(yVals, "")
                            set1.colors = colorList
                            set1.barSpacePercent = 20f

                            val dataSets = ArrayList<IBarDataSet>()
                            dataSets.add(set1)

                            val data = BarData(xVals, dataSets)
                            data.setValueTextSize(10f)
                            data.setValueTextColor(textColor)
                            data.setValueFormatter(PercentFormatter())
                            chartWRModes!!.setDescription("")
                            chartWRModes!!.data = data
                            chartWRModes!!.requestLayout()
                        }
                    }

                    private fun setUpAvgDmgPerModeChart() {
                        if (avgDamageMode.size > 0) {
                            val textColor = getTextColor(chartAvgDmg!!.context)

                            chartAvgDmg!!.setDrawBarShadow(false)
                            chartAvgDmg!!.setDrawValueAboveBar(false)
                            chartAvgDmg!!.setPinchZoom(false)
                            chartAvgDmg!!.isDoubleTapToZoomEnabled = false
                            chartAvgDmg!!.setDrawGridBackground(false)
                            chartAvgDmg!!.setDrawValueAboveBar(true)
                            chartAvgDmg!!.setTouchEnabled(false)

                            val xAxis = chartAvgDmg!!.xAxis
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            xAxis.textColor = textColor
                            xAxis.setDrawGridLines(true)

                            val yAxis = chartAvgDmg!!.axisRight
                            yAxis.setLabelCount(6, false)
                            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                            yAxis.textColor = textColor
                            yAxis.isEnabled = false

                            val yAxis2 = chartAvgDmg!!.axisLeft
                            yAxis2.setLabelCount(6, false)
                            yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                            yAxis2.textColor = textColor

                            val colorList: MutableList<Int> = ArrayList()
                            colorList.add(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.solo_pvp_color
                                )
                            )
                            colorList.add(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.pve_color
                                )
                            )
                            colorList.add(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.average_up
                                )
                            )
                            colorList.add(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.div_pvp_3
                                )
                            )
                            colorList.add(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.div_pvp_2
                                )
                            )
                            colorList.add(
                                ContextCompat.getColor(
                                    chartGamemodes!!.context,
                                    R.color.ranked_color
                                )
                            )

                            val l = chartAvgDmg!!.legend
                            l.isEnabled = false
                            val xVals: MutableList<String> = ArrayList()
                            xVals.addAll(modeStrings)

                            val yVals: MutableList<BarEntry> = ArrayList()
                            for (i in xVals.indices) {
                                yVals.add(BarEntry(avgDamageMode[xVals[i]]!!, i))
                            }

                            val set1 = BarDataSet(yVals, "")
                            set1.colors = colorList
                            set1.barSpacePercent = 20f

                            val dataSets = ArrayList<IBarDataSet>()
                            dataSets.add(set1)

                            val data = BarData(xVals, dataSets)
                            data.setValueTextSize(10f)
                            data.setValueTextColor(textColor)
                            data.setValueFormatter(LargeValueFormatter())
                            chartAvgDmg!!.setDescription("")
                            chartAvgDmg!!.data = data
                            chartAvgDmg!!.requestLayout()
                        }
                    }

                    private fun setUpSurvivalRatePerModeChart() {
                        if (survivalRateMode.size > 0) {
                            val textColor = getTextColor(chartSurvivalRate!!.context)

                            chartSurvivalRate!!.setDrawBarShadow(false)
                            chartSurvivalRate!!.setDrawValueAboveBar(false)
                            chartSurvivalRate!!.setPinchZoom(false)
                            chartSurvivalRate!!.isDoubleTapToZoomEnabled = false
                            chartSurvivalRate!!.setDrawGridBackground(false)
                            chartSurvivalRate!!.setDrawValueAboveBar(true)
                            chartSurvivalRate!!.setTouchEnabled(false)

                            val xAxis = chartSurvivalRate!!.xAxis
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            xAxis.textColor = textColor
                            xAxis.setDrawGridLines(true)

                            val yAxis = chartSurvivalRate!!.axisRight
                            yAxis.setLabelCount(6, false)
                            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                            yAxis.textColor = textColor
                            yAxis.isEnabled = false

                            val yAxis2 = chartSurvivalRate!!.axisLeft
                            yAxis2.setLabelCount(6, false)
                            yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                            yAxis2.textColor = textColor

                            val colorList: MutableList<Int> = ArrayList()
                            colorList.add(Color.parseColor("#F44336"))
                            colorList.add(Color.parseColor("#FF9800"))
                            colorList.add(
                                ContextCompat.getColor(
                                    chartSurvivalRate!!.context,
                                    R.color.average_up
                                )
                            )
                            colorList.add(Color.parseColor("#2196F3"))
                            colorList.add(Color.parseColor("#FAFA00"))

                            val l = chartSurvivalRate!!.legend
                            l.isEnabled = false
                            val xVals: MutableList<String> = ArrayList()
                            xVals.addAll(modeStrings)

                            val yVals: MutableList<BarEntry> = ArrayList()
                            for (i in xVals.indices) {
                                yVals.add(BarEntry(survivalRateMode[xVals[i]]!!, i))
                            }

                            val set1 = BarDataSet(yVals, "")
                            set1.colors = colorList
                            set1.barSpacePercent = 20f

                            val dataSets = ArrayList<IBarDataSet>()
                            dataSets.add(set1)

                            val data = BarData(xVals, dataSets)
                            data.setValueTextSize(10f)
                            data.setValueTextColor(textColor)
                            data.setValueFormatter(PercentFormatter())
                            chartSurvivalRate!!.setDescription("")
                            chartSurvivalRate!!.data = data
                            chartSurvivalRate!!.requestLayout()
                        }
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        Thread(runnable).start()
    }

    private fun setUpOtherStatistics(captain: Captain) {
        val strStatistics: MutableList<String?> = ArrayList()
        val statistics: MutableList<Statistics> = ArrayList()

        if (captain.teamBattleDetails != null && captain.teamBattleDetails.battles > 0) {
            strStatistics.add(getString(R.string.team_battles_title))
            statistics.add(captain.teamBattleDetails)
        }
        if (captain.pvpDiv2Details != null && captain.pvpDiv2Details.battles > 0) {
            strStatistics.add(getString(R.string.two_div_title))
            statistics.add(captain.pvpDiv2Details)
        }
        if (captain.pvpDiv3Details != null && captain.pvpDiv3Details.battles > 0) {
            strStatistics.add(getString(R.string.three_div_title))
            statistics.add(captain.pvpDiv3Details)
        }
        if (captain.pveDetails != null && captain.pveDetails.battles > 0) {
            strStatistics.add(getString(R.string.pve_title))
            statistics.add(captain.pveDetails)
        }

        aOtherStats!!.post { createOtherStatsArea(aOtherStats!!, strStatistics, statistics) }
    }

    override fun onPause() {
        super.onPause()
        eventBus.unregister(this)
    }

    @Subscribe
    fun onReceive(event: CaptainReceivedEvent?) {
        initView()
    }

    @Subscribe
    fun onSaveFinished(event: CaptainSavedEvent?) {
        chartProgress!!.post { initView() }
    }

    @Subscribe
    fun onProgressEvent(event: ProgressEvent) {
        d("CaptainFragment", "progressEvent")
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout!!.isRefreshing = event.isRefreshing
        }
    }

    @Subscribe
    fun onRefresh(event: RefreshEvent?) {
        refreshing(true)
        tvBattles!!.text = ""

        tvAverageDamage!!.text = ""

        tvAverageExp!!.text = ""

        tvKillDeath!!.text = ""

        tvWinRate!!.text = ""

        tvGenXP!!.text = ""
        tvGenDamage!!.text = ""

        tvGenDropped!!.text = ""
        tvGenCapture!!.text = ""
        tvGenPlanesKilled!!.text = ""
        tvGenProfileLevel!!.text = ""

        tvCADiff!!.text = ""
        tvCARating!!.text = ""

        //dates
        lastBattleTime!!.text = ""

        createdOnTime!!.text = ""

        //checkbox area
        chartAverages!!.clear()
        gAverages!!.adapter = null
        averagesAdapter = null

        chartProgress!!.visibility = View.VISIBLE

        topicalChart!!.clear()

        chartGamePerNation!!.clear()
        chartGamePerType!!.clear()

        chartGamemodes!!.clear()

        aOtherStats!!.removeAllViews()

        tiersChart!!.clear()
    }

    private inner class MyFormatter : ValueFormatter {
        private var mFormat: DecimalFormat
        private var isPercent = false

        init {
            this.mFormat = DecimalFormat("###,###,##0.0")
        }

        fun change(tag: String?) {
            if (tag == TAG_AVERAGE_EXP || tag == TAG_AVERAGE_DAMAGE) {
                this.mFormat = DecimalFormat("###,###,##0")
            } else {
                this.mFormat = DecimalFormat("###,###,##0.0")
            }
            isPercent = tag == TAG_WIN_RATE || tag == TAG_BATTLES
        }

        override fun getFormattedValue(
            value: Float,
            entry: Entry,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler
        ): String {
            return mFormat.format(value.toDouble()) + (if (isPercent) "%" else "")
        }
    }

    private inner class MyYFormatter : YAxisValueFormatter {
        private var mFormat: DecimalFormat
        private var isPercent = false

        init {
            this.mFormat = DecimalFormat("###,###,##0.0")
        }

        fun change(tag: String?) {
            if (tag == TAG_AVERAGE_EXP || tag == TAG_AVERAGE_DAMAGE) {
                this.mFormat = DecimalFormat("###,###,##0")
            } else if (tag == TAG_KILL_DEATH) {
                this.mFormat = DecimalFormat("###,###,##0.00")
            } else {
                this.mFormat = DecimalFormat("###,###,##0.0")
            }
            isPercent = tag == TAG_WIN_RATE || tag == TAG_BATTLES
        }

        override fun getFormattedValue(value: Float, yAxis: YAxis): String {
            return mFormat.format(value.toDouble()) + (if (isPercent) "%" else "")
        }
    }

    companion object {
        const val TAG_AVERAGE_DAMAGE: String = "AverageDamage"
        const val TAG_BATTLES: String = "Battles"
        const val TAG_WIN_RATE: String = "WinRate"
        const val TAG_KILL_DEATH: String = "KillDeath"
        const val TAG_AVERAGE_EXP: String = "AverageExp"
        const val SELECTED_GRAPH: String = "selected_graph"
        const val SEEN_TOPICAL_GRAPH_DESCRIPTION: String = "seen_topical_graph_descriptions"
    }
}