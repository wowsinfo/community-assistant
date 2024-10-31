package com.half.wowsca.ui.viewcaptain

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.formatter.YAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.github.mikephil.charting.utils.ViewPortHandler
import com.half.wowsca.CAApp.Companion.getTextColor
import com.half.wowsca.CAApp.Companion.getTheme
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.CAApp.Companion.isColorblind
import com.half.wowsca.R
import com.half.wowsca.interfaces.ICaptain
import com.half.wowsca.managers.CaptainManager.createCapIdStr
import com.half.wowsca.managers.StorageManager.getPlayerShips
import com.half.wowsca.model.Captain
import com.half.wowsca.model.Ship
import com.half.wowsca.model.Statistics
import com.half.wowsca.model.encyclopedia.items.ShipStat
import com.half.wowsca.model.enums.AverageType
import com.half.wowsca.model.listModels.ListAverages
import com.half.wowsca.model.saveobjects.SavedShips
import com.half.wowsca.ui.SettingActivity
import com.half.wowsca.ui.UIUtils.createOtherStatsArea
import com.half.wowsca.ui.UIUtils.getNationText
import com.half.wowsca.ui.UIUtils.setShipImage
import com.half.wowsca.ui.UIUtils.setUpCard
import com.half.wowsca.ui.adapter.AveragesAdapter
import com.half.wowsca.ui.viewcaptain.tabs.CaptainRankedFragment
import com.half.wowsca.ui.views.NonScrollableGridView
import com.half.wowsca.ui.views.RadarMarkerView
import com.utilities.Utils.defaultDecimalFormatter
import com.utilities.Utils.oneDepthDecimalFormatter
import com.utilities.logging.Dlog.wtf
import com.utilities.preferences.Prefs
import java.text.DecimalFormat
import java.util.Collections
import kotlin.math.abs

/**
 * Created by slai4 on 12/12/2015.
 */
class ShipFragment : Fragment() {
    private var id: Long = 0

    private var ivShip: ImageView? = null
    private var tvName: TextView? = null
    private var tvNationTier: TextView? = null
    private var tvBattles: TextView? = null
    private var tvWinRate: TextView? = null
    private var tvAvgExp: TextView? = null
    private var tvAvgFrag: TextView? = null
    private var tvAvgDamage: TextView? = null
    private var tvTopCARating: TextView? = null

    private var tvBatteryMain: TextView? = null
    private var tvBatteryTorps: TextView? = null
    private var tvBatteryAircraft: TextView? = null
    private var tvBatteryOther: TextView? = null

    private var aCompare: View? = null
    private var chartAverages: RadarChart? = null
    private var gridView: NonScrollableGridView? = null
    private var tvCARating: TextView? = null
    private var tvCADiff: TextView? = null
    private var tvAverageTitle: TextView? = null

    private var tvMaxKills: TextView? = null
    private var tvMaxDamage: TextView? = null
    private var tvMaxPlanes: TextView? = null
    private var tvMaxXp: TextView? = null
    private var tvTraveled: TextView? = null
    private var tvAvgPlanes: TextView? = null
    private var tvSurvivalRate: TextView? = null
    private var tvAvgCaps: TextView? = null
    private var tvAvgDropped: TextView? = null
    private var tvTotalXp: TextView? = null
    private var tvSurvivedWins: TextView? = null

    private var tvWins: TextView? = null
    private var tvLosses: TextView? = null
    private var tvDraws: TextView? = null

    private var tvTotalPlanes: TextView? = null
    private var tvTotalCaptures: TextView? = null
    private var tvTotalDefReset: TextView? = null

    private var tvBatteryAccMain: TextView? = null
    private var tvBatteryAccTorps: TextView? = null

    private var tvSpottingDamage: TextView? = null
    private var tvArgoDamage: TextView? = null
    private var tvBuildingDamage: TextView? = null
    private var tvArgoTorpDamage: TextView? = null
    private var tvSuppressionCount: TextView? = null
    private var tvSpottingCount: TextView? = null
    private var tvMaxSpotting: TextView? = null

    private var aRanked: View? = null
    private var llContainer: LinearLayout? = null

    private var aChartArea: View? = null
    private var chartSavedArea: LineChart? = null
    private var tvChartSaved: TextView? = null

    private var aSavedChartBattles: View? = null
    private var aSavedChartDamage: View? = null
    private var aSavedChartWinRate: View? = null
    private var aSavedChartExp: View? = null
    private var aSavedChartKills: View? = null

    private var savedShipsInfo: List<Ship?>? = null

    private var formatter: MyFormatter? = null
    private var yFormatter: MyYFormatter? = null

    private var aOtherStats: LinearLayout? = null

    private var chartGameModes: PieChart? = null

    private var tvGameModeTitle: TextView? = null

    private var aCARatingArea: View? = null

    private var aTopCARatingArea: View? = null

    private var aAveragesContribution: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ship, container, false)
        bindView(view)
        if (savedInstanceState != null) {
            id = savedInstanceState.getLong(SHIP_ID)
        }
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("shipId", id)
    }

    private fun bindView(view: View) {
        ivShip = view.findViewById(R.id.snippet_ship_icon)
        tvName = view.findViewById(R.id.snippet_ship_name)
        tvNationTier = view.findViewById(R.id.snippet_ship_nation_tier)
        tvBattles = view.findViewById(R.id.snippet_ship_battles)
        tvWinRate = view.findViewById(R.id.snippet_ship_win_rate)
        tvAvgExp = view.findViewById(R.id.snippet_ship_avg_exp)
        tvAvgFrag = view.findViewById(R.id.snippet_ship_avg_kills)
        tvAvgDamage = view.findViewById(R.id.snippet_ship_avg_damage)
        tvTopCARating = view.findViewById(R.id.snippet_ship_ca_rating)

        tvBatteryAircraft = view.findViewById(R.id.fragment_ship_battery_kills_aircraft)
        tvBatteryMain = view.findViewById(R.id.fragment_ship_battery_kills_main)
        tvBatteryTorps = view.findViewById(R.id.fragment_ship_battery_kills_torps)
        tvBatteryOther = view.findViewById(R.id.fragment_ship_battery_kills_other)

        aCompare = view.findViewById(R.id.averages_grid_area)
        chartAverages = view.findViewById(R.id.averages_chart)
        gridView = view.findViewById(R.id.averages_grid)
        tvCARating = view.findViewById(R.id.averages_car)
        tvCADiff = view.findViewById(R.id.averages_car_dif)

        tvMaxDamage = view.findViewById(R.id.fragment_ship_max_damage)
        tvMaxKills = view.findViewById(R.id.fragment_ship_max_kills)
        tvMaxPlanes = view.findViewById(R.id.fragment_ship_max_planes_killed)
        tvMaxXp = view.findViewById(R.id.fragment_ship_max_xp)
        tvTraveled = view.findViewById(R.id.fragment_ship_distance_traveled)
        tvAvgPlanes = view.findViewById(R.id.fragment_ship_planes_destroyed)
        tvSurvivalRate = view.findViewById(R.id.fragment_ship_survived_battles)
        tvAvgCaps = view.findViewById(R.id.fragment_ship_capture_points)
        tvAvgDropped = view.findViewById(R.id.fragment_ship_dropped_capture_points)
        tvTotalXp = view.findViewById(R.id.fragment_ship_total_exp)
        tvSurvivedWins = view.findViewById(R.id.fragment_ship_survived_wins)

        tvWins = view.findViewById(R.id.fragment_ship_wins)
        tvLosses = view.findViewById(R.id.fragment_ship_losses)
        tvDraws = view.findViewById(R.id.fragment_ship_draws)

        tvTotalCaptures = view.findViewById(R.id.fragment_ship_total_captures)
        tvTotalDefReset = view.findViewById(R.id.fragment_ship_total_def_points)
        tvTotalPlanes = view.findViewById(R.id.fragment_ship_total_planes)

        tvBatteryAccMain = view.findViewById(R.id.fragment_ship_battery_accuracy_main)
        tvBatteryAccTorps = view.findViewById(R.id.fragment_ship_battery_accuracy_torp)

        aRanked = view.findViewById(R.id.fragment_ship_ranked_area)
        llContainer = view.findViewById(R.id.fragment_ship_ranked_container)

        aChartArea = view.findViewById(R.id.fragment_ship_saved_chart_area)
        chartSavedArea = view.findViewById(R.id.fragment_ship_saved_chart_graph_topical_line)

        aSavedChartBattles = view.findViewById(R.id.fragment_ship_saved_chart_battles_area)
        aSavedChartWinRate = view.findViewById(R.id.fragment_ship_saved_chart_winning_area)
        aSavedChartDamage = view.findViewById(R.id.fragment_ship_saved_chart_damage_area)
        aSavedChartExp = view.findViewById(R.id.fragment_ship_saved_chart_experience_area)
        aSavedChartKills = view.findViewById(R.id.fragment_ship_saved_chart_kills_area)
        tvChartSaved = view.findViewById(R.id.fragment_ship_saved_chart_graph_topical_text)

        tvSpottingDamage = view.findViewById(R.id.fragment_ship_total_spotting)
        tvArgoDamage = view.findViewById(R.id.fragment_ship_total_argo)
        tvBuildingDamage = view.findViewById(R.id.fragment_ship_total_building)
        tvArgoTorpDamage = view.findViewById(R.id.fragment_ship_total_torp_argo)

        tvSuppressionCount = view.findViewById(R.id.fragment_ship_total_supressions)
        tvSpottingCount = view.findViewById(R.id.fragment_ship_total_spots)
        tvMaxSpotting = view.findViewById(R.id.fragment_ship_max_spots)

        aOtherStats = view.findViewById(R.id.fragment_ship_other_stats)
        tvGameModeTitle = view.findViewById(R.id.fragment_ship_game_mode_title)

        chartGameModes = view.findViewById(R.id.fragment_ship_graphs_games_per_mode)

        tvAverageTitle = view.findViewById(R.id.averages_top_title)
        tvAverageTitle?.setText(R.string.community_assistant_rating)

        aCARatingArea = view.findViewById(R.id.averages_ca_rating_top_area)
        aTopCARatingArea = view.findViewById(R.id.snippet_ship_ca_rating_area)

        aAveragesContribution = view.findViewById(R.id.averages_contribution_chart_area)
        view.findViewById<View>(R.id.ca_rating_breakdown_area).isClickable = false
        aAveragesContribution?.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView() {
        try {
            requireActivity().invalidateOptionsMenu()
        } catch (e: Exception) {
        }
        val info = infoManager!!.getShipInfo(requireContext())[id]
        val stat = infoManager!!.getShipStats(requireContext())[id]
        var captain: Captain? = null
        try {
            captain = (activity as ICaptain?)!!.getCaptain(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (captain != null && captain.ships != null) {
            var ship: Ship? = null
            for (s in captain.ships) {
                if (s.shipId == id) {
                    ship = s
                    wtf("ShipInfo", " ship - $s")
                    break
                }
            }

            if (info != null) {
                tvName!!.text = info.name
                setShipImage(ivShip!!, info, true)
                val nation = getNationText(requireContext(), info.nation)
                tvNationTier!!.text = nation + " - " + info.tier
                if (info.isPremium) {
                    tvName!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.premium_shade))
                } else {
                    tvName!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
            } else {
                tvName!!.text = "" + id
                tvNationTier!!.setText(R.string.unknown)
            }

            if (ship != null) {
                val battles = ship.battles.toFloat()
                wtf("ShipFragment", "battles = $battles")

                val prefs = Prefs(context)
                val showCompare = prefs.getBoolean(SettingActivity.SHOW_COMPARE, true)
                aCARatingArea!!.visibility = View.VISIBLE
                aTopCARatingArea!!.visibility = View.VISIBLE
                if (stat != null && showCompare && battles > 0) {
                    aCompare!!.visibility = View.VISIBLE
                    val averages: MutableList<ListAverages> = ArrayList()
                    averages.add(
                        ListAverages.create(
                            getString(R.string.damage),
                            (ship.totalDamage / battles).toFloat(),
                            stat.dmg_dlt,
                            AverageType.LARGE_NUMBER
                        )
                    )
                    averages.add(
                        ListAverages.create(
                            getString(R.string.short_kills_game),
                            ship.frags / battles,
                            stat.frags,
                            AverageType.DEFAULT
                        )
                    )
                    averages.add(
                        ListAverages.create(
                            getString(R.string.short_win_rate),
                            (ship.wins / battles),
                            stat.wins,
                            AverageType.PERCENT
                        )
                    )
                    averages.add(
                        ListAverages.create(
                            getString(R.string.short_planes_game),
                            ship.planesKilled / battles,
                            stat.pls_kd,
                            AverageType.DEFAULT
                        )
                    )

                    //                    averages.add(ListAverages.create(getString(R.string.survival_rate), ship.getSurvivedBattles() / battles, stat.getSr_bat(), AverageType.PERCENT));
//                    averages.add(ListAverages.create(getString(R.string.survived_wins), ship.getSurvivedWins() / battles, stat.getSr_wins(), AverageType.PERCENT));
//                    averages.add(ListAverages.create(getString(R.string.average_xp), ship.getTotalXP() / battles, stat.getAvg_xp(), AverageType.DEFAULT));
                    if (gridView!!.adapter == null) {
                        val averagesAdapter =
                            AveragesAdapter(context, R.layout.list_averages, averages)
                        gridView!!.adapter = averagesAdapter
                    } else {
                        val adapter = gridView!!.adapter as AveragesAdapter
                        adapter.setObjects(averages)
                    }
                } else {
                    aCompare!!.visibility = View.GONE
                }

                if (battles > 0) {
                    tvBattles!!.text = "" + (battles.toInt())
                    val avgExp = ship.totalXP / battles
                    tvAvgExp!!.text = avgExp.toInt().toString() + ""
                    val wr = (ship.wins / battles) * 100.0f
                    tvWinRate!!.text = defaultDecimalFormatter.format(wr.toDouble()) + "%"
                    var kdBattles = battles.toInt()
                    if (kdBattles != ship.survivedBattles) {
                        kdBattles = (battles - ship.survivedBattles).toInt()
                    }
                    val kd = ship.frags.toFloat() / kdBattles
                    tvAvgFrag!!.text = defaultDecimalFormatter.format(kd.toDouble())
                    val avgDamage = (ship.totalDamage / battles).toInt()
                    tvAvgDamage!!.text = "" + avgDamage

                    val rating = ship.caRating
                    tvCARating!!.text = Math.round(rating).toString() + ""
                    tvCARating!!.tag = rating.toString() + ""
                    tvTopCARating!!.text = Math.round(rating).toString() + ""

                    val mainBatteryStats = ship.mainBattery
                    val torpStats = ship.torpedoes
                    val aircraftStats = ship.aircraft

                    tvBatteryMain!!.text = "" + mainBatteryStats.frags
                    if (mainBatteryStats.shots > 0) tvBatteryAccMain!!.text =
                        oneDepthDecimalFormatter.format((mainBatteryStats.hits / mainBatteryStats.shots.toFloat() * 100).toDouble()) + "%"
                    tvBatteryTorps!!.text = "" + torpStats.frags
                    if (torpStats.shots > 0) tvBatteryAccTorps!!.text =
                        oneDepthDecimalFormatter.format((torpStats.hits / torpStats.shots.toFloat() * 100).toDouble()) + "%"
                    tvBatteryAircraft!!.text = "" + aircraftStats.frags

                    val others =
                        ship.frags - mainBatteryStats.frags - torpStats.frags - aircraftStats.frags
                    tvBatteryOther!!.text = "" + others

                    tvMaxKills!!.text = "" + ship.maxFragsInBattle
                    tvMaxDamage!!.text = "" + ship.maxDamage
                    tvMaxPlanes!!.text = "" + ship.maxPlanesKilled
                    tvMaxXp!!.text = "" + ship.maxXP
                    tvAvgCaps!!.text =
                        oneDepthDecimalFormatter.format((ship.capturePoints.toFloat() / battles).toDouble())
                    tvSurvivedWins!!.text =
                        oneDepthDecimalFormatter.format((ship.survivedWins.toFloat() / battles).toDouble()) + "%"
                    tvSurvivalRate!!.text =
                        oneDepthDecimalFormatter.format(((ship.survivedBattles.toFloat() / battles) * 100).toDouble()) + "%"
                    tvAvgDropped!!.text =
                        oneDepthDecimalFormatter.format((ship.droppedCapturePoints.toFloat() / battles).toDouble())
                    tvTotalXp!!.text = "" + ship.totalXP

                    tvWins!!.text = "" + ship.wins
                    tvLosses!!.text = "" + ship.losses
                    tvDraws!!.text = "" + ship.draws

                    tvTotalCaptures!!.text = "" + ship.capturePoints
                    tvTotalPlanes!!.text = "" + ship.planesKilled
                    tvTotalDefReset!!.text = "" + ship.droppedCapturePoints

                    tvAvgPlanes!!.text =
                        oneDepthDecimalFormatter.format((ship.planesKilled.toFloat() / battles).toDouble())
                    tvTraveled!!.text = ship.distanceTraveled.toString() + " miles"

                    var argoDamage = "" + ship.totalArgoDamage
                    if (ship.totalArgoDamage > 1000000) {
                        argoDamage =
                            defaultDecimalFormatter.format(ship.totalArgoDamage / 1000000) + getString(
                                R.string.million
                            )
                    }
                    tvArgoDamage!!.text = argoDamage

                    var argoTorpDamage = "" + ship.torpArgoDamage
                    if (ship.totalArgoDamage > 1000000) {
                        argoTorpDamage =
                            defaultDecimalFormatter.format(ship.totalArgoDamage / 1000000) + getString(
                                R.string.million
                            )
                    }
                    tvArgoTorpDamage!!.text = argoTorpDamage

                    var buildingDamage = "" + ship.buildingDamage
                    if (ship.buildingDamage > 1000000) {
                        buildingDamage =
                            defaultDecimalFormatter.format(ship.buildingDamage / 1000000) + getString(
                                R.string.million
                            )
                    }
                    tvBuildingDamage!!.text = buildingDamage

                    var scoutingDamage = "" + ship.scoutingDamage
                    if (ship.scoutingDamage > 1000000) {
                        scoutingDamage =
                            defaultDecimalFormatter.format(ship.scoutingDamage / 1000000) + getString(
                                R.string.million
                            )
                    }
                    tvSpottingDamage!!.text = scoutingDamage

                    tvSpottingCount!!.text = "" + ship.spotted
                    tvSuppressionCount!!.text = "" + ship.suppressionCount
                    tvMaxSpotting!!.text = "" + ship.maxSpotted

                    setUpAverages(ship, stat)

                    setUpOtherModeInformation(ship)

                    val onClick = View.OnClickListener { v ->
                        val tag = v.tag as String
                        val prefs = Prefs(v.context)
                        prefs.setString(SELECTED_GRAPH, tag)
                        //check background
                        checkBackgrounds(tag)
                        //setUpGraph
                        if (savedShipsInfo != null) setUpTopicalInfo(tag, savedShipsInfo!!, false)
                    }
                    aSavedChartDamage!!.setOnClickListener(onClick)
                    aSavedChartDamage!!.tag = TAG_AVERAGE_DAMAGE
                    aSavedChartBattles!!.setOnClickListener(onClick)
                    aSavedChartBattles!!.tag = TAG_BATTLES
                    aSavedChartWinRate!!.setOnClickListener(onClick)
                    aSavedChartWinRate!!.tag = TAG_WIN_RATE
                    aSavedChartKills!!.setOnClickListener(onClick)
                    aSavedChartKills!!.tag = TAG_KILL_DEATH
                    aSavedChartExp!!.setOnClickListener(onClick)
                    aSavedChartExp!!.tag = TAG_AVERAGE_EXP

                    val id = createCapIdStr(captain.server, captain.id)
                    getSavedData(id, ship.shipId)

                    createGameModeGraphs(ship)
                }
                setUpRankedArea(ship)
            }
        }
    }

    private fun createGameModeGraphs(ship: Ship) {
        val modeStrings: MutableList<String> = ArrayList()
        val gamesPerMode: MutableMap<String, Int> = HashMap()
        val soloBattles = (ship.battles
                - ship.pve.battles
                - ship.pvpDiv2.battles
                - ship.pvpDiv3.battles
                - ship.teamBattles.battles)

        if (soloBattles > 0) {
            val str = getString(R.string.solo_pvp)
            modeStrings.add(str)
            gamesPerMode[str] = soloBattles
        }
        if (ship.pve.battles > 0) {
            val str = getString(R.string.pve)
            modeStrings.add(str)
            gamesPerMode[str] = ship.pve.battles
        }
        if (ship.pvpDiv2.battles > 0) {
            val str = getString(R.string.pvp_2_div)
            modeStrings.add(str)
            gamesPerMode[str] = ship.pvpDiv2.battles
        }
        if (ship.pvpDiv3.battles > 0) {
            val str = getString(R.string.pvp_3_div)
            modeStrings.add(str)
            gamesPerMode[str] = ship.pvpDiv3.battles
        }
        if (ship.teamBattles.battles > 0) {
            val str = getString(R.string.team_battles)
            modeStrings.add(str)
            gamesPerMode[str] = ship.teamBattles.battles
        }

        var rankedBattles = 0
        if (ship.rankedInfo != null) {
            for (info in ship.rankedInfo) {
                try {
                    rankedBattles += info.solo.battles
                } catch (e: Exception) {
                }
            }
        }
        if (rankedBattles > 0) {
            val ranked = getString(R.string.ranked)
            modeStrings.add(ranked)
            gamesPerMode[ranked] = rankedBattles
        }

        chartGameModes!!.post { setUpGamesPerModeChart(gamesPerMode, modeStrings) }
    }

    private fun setUpGamesPerModeChart(gamesPerMode: Map<String, Int>, modeStrings: List<String>) {
        if (gamesPerMode.size > 0) {
            val textColor = getTextColor(chartGameModes!!.context)
            val colorblind = isColorblind(chartGameModes!!.context)

            chartGameModes!!.isRotationEnabled = false

            chartGameModes!!.isDrawHoleEnabled = true
            chartGameModes!!.setHoleColor(R.color.transparent)
            chartGameModes!!.transparentCircleRadius = 50f
            chartGameModes!!.holeRadius = 50f

            chartGameModes!!.setDrawSliceText(false)

            val l = chartGameModes!!.legend
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
            colorList.add(Color.parseColor("#F44336"))
            colorList.add(Color.parseColor("#FF9800"))
            colorList.add(ContextCompat.getColor(chartGameModes!!.context, R.color.average_up))
            colorList.add(Color.parseColor("#2196F3"))
            colorList.add(Color.parseColor("#FAFA00"))

            val dataSet = PieDataSet(yVals1, "")

            val dataSets = ArrayList<PieDataSet>()
            dataSets.add(dataSet)

            dataSet.colors = colorList

            val data = PieData(xVals, dataSet)
            data.setValueTextColor(ContextCompat.getColor(chartGameModes!!.context, R.color.black))
            data.setValueTextSize(14f)
            chartGameModes!!.setDescription("")
            data.setValueFormatter(LargeValueFormatter())

            chartGameModes!!.highlightValues(null)

            chartGameModes!!.data = data
            chartGameModes!!.requestLayout()
            tvGameModeTitle!!.visibility = View.VISIBLE
        } else {
            tvGameModeTitle!!.visibility = View.GONE
        }
    }

    private fun setUpOtherModeInformation(ship: Ship) {
        val strStatistics: MutableList<String?> = ArrayList()
        val statistics: MutableList<Statistics> = ArrayList()

        if (ship.teamBattles != null && ship.teamBattles.battles > 0) {
            strStatistics.add(getString(R.string.team_battles_title))
            statistics.add(ship.teamBattles)
        }
        if (ship.pvpDiv2 != null && ship.pvpDiv2.battles > 0) {
            strStatistics.add(getString(R.string.two_div_title))
            statistics.add(ship.pvpDiv2)
        }
        if (ship.pvpDiv3 != null && ship.pvpDiv3.battles > 0) {
            strStatistics.add(getString(R.string.three_div_title))
            statistics.add(ship.pvpDiv3)
        }
        if (ship.pve != null && ship.pve.battles > 0) {
            strStatistics.add(getString(R.string.pve_title))
            statistics.add(ship.pve)
        }

        aOtherStats!!.post { createOtherStatsArea(aOtherStats!!, strStatistics, statistics) }
    }

    private fun setUpRankedArea(ship: Ship) {
        llContainer!!.removeAllViews()
        if (ship.rankedInfo != null) {
            aRanked!!.visibility = View.VISIBLE

            Collections.sort(ship.rankedInfo) { lhs, rhs ->
                rhs.seasonNum.compareTo(
                    lhs.seasonNum,
                    ignoreCase = true
                )
            }

            for (stats in ship.rankedInfo) {
                if (stats.solo != null) {
                    val view = LayoutInflater.from(context)
                        .inflate(R.layout.list_ship_ranked, llContainer, false)

                    setUpCard(view, R.id.list_ship_ranked_card_area)

                    val tvTitle = view.findViewById<TextView>(R.id.list_ship_ranked_title)

                    val tvBattles = view.findViewById<TextView>(R.id.list_ship_ranked_battles)
                    val tvAvgDamage = view.findViewById<TextView>(R.id.list_ship_ranked_avg_dmg)
                    val tvAvgKills = view.findViewById<TextView>(R.id.list_ship_ranked_kills)
                    val tvAvgCaps = view.findViewById<TextView>(R.id.list_ship_ranked_avg_caps)
                    val tvDrpCapPts = view.findViewById<TextView>(R.id.list_ship_ranked_drp_cap_pts)
                    val tvAvgPlanes = view.findViewById<TextView>(R.id.list_ship_ranked_avg_planes)
                    val tvAvgExp = view.findViewById<TextView>(R.id.list_ship_ranked_avg_exp)
                    val tvSurvivalRate =
                        view.findViewById<TextView>(R.id.list_ship_ranked_survival_rate)
                    val tvWinRate = view.findViewById<TextView>(R.id.list_ship_ranked_win_rate)
                    val tvSurvivedWins =
                        view.findViewById<TextView>(R.id.list_ship_ranked_survival_wins)


                    val tvBatteryMain =
                        view.findViewById<TextView>(R.id.list_ship_ranked_battery_kills_main)
                    val tvBatteryTorps =
                        view.findViewById<TextView>(R.id.list_ship_ranked_battery_kills_torps)
                    val tvBatteryPlanes =
                        view.findViewById<TextView>(R.id.list_ship_ranked_battery_kills_aircraft)
                    val tvBatteryOther =
                        view.findViewById<TextView>(R.id.list_ship_ranked_battery_kills_other)

                    tvTitle.text = getString(R.string.ranked_season) + " " + stats.seasonNum
                    val season = stats.solo
                    val bat = season.battles.toFloat()
                    tvBattles.text = season.battles.toString() + ""
                    if (bat > 0) {
                        val avgExp = season.xp / bat
                        tvAvgExp.text = defaultDecimalFormatter.format(avgExp.toDouble())
                        val wr = (season.wins / bat) * 100.0f
                        tvWinRate.text = defaultDecimalFormatter.format(wr.toDouble()) + "%"
                        val kd = season.frags.toFloat() / bat
                        tvAvgKills.text = defaultDecimalFormatter.format(kd.toDouble())
                        val avgDamage = (season.damage / bat).toInt()
                        tvAvgDamage.text = "" + avgDamage

                        val mainBatteryStats = season.main
                        val torpStats = season.torps
                        val aircraftStats = season.aircraft
                        tvBatteryMain.text =
                            CaptainRankedFragment.createBatteryString(mainBatteryStats)
                        tvBatteryTorps.text = CaptainRankedFragment.createBatteryString(torpStats)
                        tvBatteryPlanes.text =
                            CaptainRankedFragment.createBatteryString(aircraftStats)
                        val others =
                            season.frags - mainBatteryStats.frags - torpStats.frags - aircraftStats.frags
                        tvBatteryOther.text = "" + others

                        tvAvgPlanes.text =
                            oneDepthDecimalFormatter.format((season.planesKilled / bat).toDouble())

                        tvSurvivedWins.text =
                            oneDepthDecimalFormatter.format(((season.surWins / bat) * 100).toDouble()) + "%"
                        tvAvgCaps.text =
                            oneDepthDecimalFormatter.format((season.capPts.toFloat() / bat).toDouble())
                        tvSurvivalRate.text =
                            oneDepthDecimalFormatter.format(((season.survived.toFloat() / bat) * 100).toDouble()) + "%"
                        tvDrpCapPts.text =
                            oneDepthDecimalFormatter.format((season.drpCapPts.toFloat() / bat).toDouble())
                    }
                    llContainer!!.addView(view)
                }
            }
        } else {
            aRanked!!.visibility = View.GONE
        }
    }

    private fun getSavedData(accountId: String, shipId: Long) {
        if (savedShipsInfo == null) {
            val t = Thread {
                var ships: SavedShips? = getPlayerShips(
                    requireContext(), accountId
                )
                if (ships != null) {
                    savedShipsInfo = ships.savedShips[shipId]
                    if (savedShipsInfo != null && savedShipsInfo!!.size > 1) {
                        setupSavedInfo(savedShipsInfo, true)
                    } else {
                        aChartArea!!.post { aChartArea!!.visibility = View.GONE }
                    }
                } else {
                    aChartArea!!.post { aChartArea!!.visibility = View.GONE }
                }
                ships = null
            }
            t.start()
        } else {
            setupSavedInfo(savedShipsInfo, false)
        }
    }

    private fun setupSavedInfo(shipStats: List<Ship?>?, firstLoad: Boolean) {
        val prefs = Prefs(context)
        var selectedGraph: String? = null
        try {
            selectedGraph = prefs.getString(SELECTED_GRAPH, "")
        } catch (e: Exception) {
        }
        if (TextUtils.isEmpty(selectedGraph)) selectedGraph = TAG_AVERAGE_EXP

        val tag = selectedGraph
        aChartArea!!.post {
            checkBackgrounds(tag)
            if (shipStats != null) setUpTopicalInfo(tag, shipStats, firstLoad)
        }

        tvCADiff!!.post {
            val rating = tvCARating!!.tag
            try {
                if (rating != null && shipStats!!.size > 0) {
                    val fRating = (rating as String).toFloat()
                    val lastShip = shipStats[shipStats.size - 2]
                    val prevRating = lastShip!!.caRating
                    if (prevRating > 0) {
                        val dif = fRating - prevRating
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
                } else {
                    tvCADiff!!.visibility = View.GONE
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun setUpTopicalInfo(tag: String?, ships: List<Ship?>, firstLoad: Boolean) {
        aChartArea!!.visibility = View.VISIBLE
        val nameValues: MutableList<String> = ArrayList()
        val numbers: MutableList<Float> = ArrayList()
        if (firstLoad) Collections.reverse(ships)
        var strResId = R.string.captain_average_exp

        for (detail in ships) {
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
                val kd = detail.frags.toFloat() / (battles - detail.survivedBattles.toFloat())
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
            aChartArea!!.post {
                tvChartSaved!!.setText(topicalStrRes)
                chartSavedArea!!.clear()

                val colorblind = isColorblind(chartSavedArea!!.context)
                val textColor = getTextColor(chartSavedArea!!.context)
                val accentColor = if (!colorblind) (if (getTheme(
                        chartSavedArea!!.context
                    ) == "ocean"
                ) ContextCompat.getColor(
                    chartSavedArea!!.context, R.color.graph_line_color
                ) else ContextCompat.getColor(
                    chartSavedArea!!.context, R.color.top_background
                ))
                else ContextCompat.getColor(chartSavedArea!!.context, R.color.white)
                chartSavedArea!!.isDoubleTapToZoomEnabled = false
                chartSavedArea!!.setPinchZoom(false)

                chartSavedArea!!.setDescription("")

                chartSavedArea!!.isDragEnabled = false
                chartSavedArea!!.setScaleEnabled(false)
                chartSavedArea!!.setDrawGridBackground(false)

                chartSavedArea!!.legend.isEnabled = false

                val xAxis = chartSavedArea!!.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.textColor = textColor
                xAxis.setDrawGridLines(true)

                val yAxis = chartSavedArea!!.axisRight
                yAxis.isEnabled = false

                val yAxis2 = chartSavedArea!!.axisLeft
                yAxis2.setLabelCount(6, true)
                yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                yAxis2.textColor = textColor
                if (yFormatter == null) yFormatter = MyYFormatter()
                yFormatter!!.change(tag)
                yAxis2.valueFormatter = yFormatter

                val set = LineDataSet(yVals, "")

                set.color = accentColor
                set.lineWidth = 2f
                set.circleSize = 3f
                set.fillColor = accentColor
                set.setDrawValues(false)
                set.setCircleColor(accentColor)

                //                            if(tag.equals(TAG_AVERAGE_EXP) || tag.equals(TAG_AVERAGE_DAMAGE)){
//                                set.setValueFormatter(new DefaultValueFormatter(0));
//                            } else {
//                            }
                val dataSets: MutableList<ILineDataSet> = ArrayList()
                dataSets.add(set)

                val data = LineData(nameValues, dataSets)

                if (formatter == null) formatter = MyFormatter()
                formatter!!.change(tag)
                data.setValueFormatter(formatter)

                chartSavedArea!!.data = data
                chartSavedArea!!.requestLayout()
                chartSavedArea!!.animateX(750)
            }
            aChartArea!!.visibility = View.VISIBLE
        } else {
            aChartArea!!.visibility = View.GONE
        }
    }

    private fun checkBackgrounds(tag: String?) {
        if (tag == TAG_AVERAGE_EXP) {
            aSavedChartExp!!.setBackgroundResource(R.color.captain_top_background)
        } else {
            aSavedChartExp!!.setBackgroundResource(R.color.transparent)
        }
        if (tag == TAG_KILL_DEATH) {
            aSavedChartKills!!.setBackgroundResource(R.color.captain_top_background)
        } else {
            aSavedChartKills!!.setBackgroundResource(R.color.transparent)
        }
        if (tag == TAG_WIN_RATE) {
            aSavedChartWinRate!!.setBackgroundResource(R.color.captain_top_background)
        } else {
            aSavedChartWinRate!!.setBackgroundResource(R.color.transparent)
        }
        if (tag == TAG_AVERAGE_DAMAGE) {
            aSavedChartDamage!!.setBackgroundResource(R.color.captain_top_background)
        } else {
            aSavedChartDamage!!.setBackgroundResource(R.color.transparent)
        }
        if (tag == TAG_BATTLES) {
            aSavedChartBattles!!.setBackgroundResource(R.color.captain_top_background)
        } else {
            aSavedChartBattles!!.setBackgroundResource(R.color.transparent)
        }
    }

    private fun setUpAverages(ship: Ship, stat: ShipStat?) {
        val runnable: Runnable = object : Runnable {
            private fun cleanTitleString(id: Int): String {
                var str = getString(id)
                if (str.length > 14) {
                    str = str.substring(0, 15).trim { it <= ' ' } + "..."
                }
                return str
            }

            override fun run() {
                try {
                    val isColorblind = isColorblind(chartAverages!!.context)
                    val color = if (!isColorblind) (if (getTheme(
                            chartAverages!!.context
                        ) == "ocean"
                    ) ContextCompat.getColor(
                        chartAverages!!.context, R.color.graph_line_color
                    ) else ContextCompat.getColor(
                        chartAverages!!.context, R.color.top_background
                    ))
                    else ContextCompat.getColor(chartAverages!!.context, R.color.white)
                    val textColor = getTextColor(chartAverages!!.context)
                    val webTextColor =
                        ContextCompat.getColor(chartAverages!!.context, R.color.web_text_color)

                    chartAverages!!.setDescription(getString(R.string.baseline))
                    chartAverages!!.setDescriptionColor(webTextColor)

                    chartAverages!!.webLineWidth = 1.5f
                    chartAverages!!.webAlpha = 200
                    chartAverages!!.webLineWidthInner = 0.75f
                    chartAverages!!.webColor =
                        ContextCompat.getColor(chartAverages!!.context, R.color.transparent)
                    chartAverages!!.webColorInner =
                        ContextCompat.getColor(chartAverages!!.context, R.color.transparent)

                    chartAverages!!.setTouchEnabled(false)

                    val mv = RadarMarkerView(chartAverages!!.context, R.layout.custom_marker_view)
                    chartAverages!!.markerView = mv

                    val xAxis = chartAverages!!.xAxis
                    xAxis.textSize = 9f
                    xAxis.textColor = textColor

                    val yAxis = chartAverages!!.yAxis
                    yAxis.setLabelCount(5, false)
                    yAxis.textSize = 9f
                    yAxis.textColor = webTextColor
                    yAxis.setAxisMinValue(0f)

                    val l = chartAverages!!.legend
                    l.isEnabled = false

                    val titles: MutableList<String> = ArrayList()

                    titles.add(cleanTitleString(R.string.damage))
                    titles.add(cleanTitleString(R.string.short_kills_game))
                    titles.add(cleanTitleString(R.string.short_win_rate))
                    titles.add(cleanTitleString(R.string.short_planes_game))

                    //                    titles.add(getString(R.string.short_cap_points));
//                    titles.add(getString(R.string.short_def_reset));

//                    titles.add(getString(R.string.survival_rate));
//                    titles.add(getString(R.string.survived_wins));
//                    titles.add(getString(R.string.average_xp));
                    val yVals: MutableList<Entry> = ArrayList()

                    val battles = ship.battles.toFloat()
                    yVals.add(
                        Entry(
                            (((ship.totalDamage / battles) / stat!!.dmg_dlt) * 100).toFloat(),
                            0
                        )
                    )
                    yVals.add(Entry(((ship.frags / battles) / stat.frags) * 100, 1)) //kills
                    yVals.add(Entry(((ship.wins / battles) / stat.wins) * 100, 2)) //winrate
                    yVals.add(
                        Entry(
                            ((ship.planesKilled / battles) / stat.pls_kd) * 100,
                            5
                        )
                    ) // planes


                    //                    yVals.add(new Entry((float) (((ship.getCapturePoints() / battles) / stat.getCap_pts()) * 100), 3)); // captures
//                    yVals.add(new Entry((float) (((ship.getDroppedCapturePoints() / battles) / stat.getDr_cap_pts()) * 100), 4)); // def reset

//                    yVals.add(new Entry(((float) (((ship.getSurvivedBattles() / battles) / stat.getSr_bat()) * 100)), 6)); // survival
//                    yVals.add(new Entry(((float) (((ship.getSurvivedWins() / battles) / stat.getSr_wins()) * 100)), 7)); // survived wins
//                    yVals.add(new Entry(((float) (((ship.getTotalXP() / battles) / stat.getAvg_xp()) * 100)), 8)); // xp
                    val set = RadarDataSet(yVals, "")
                    set.color = color
                    set.setDrawFilled(true)
                    set.lineWidth = 2f

                    val sets: MutableList<IRadarDataSet> = ArrayList()
                    sets.add(set)

                    val data = RadarData(titles, sets)
                    data.setValueTextColor(textColor)
                    data.setValueTextSize(8f)


                    chartAverages!!.data = data
                    chartAverages!!.invalidate()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        chartAverages!!.post(runnable)
    }

    fun setId(id: Long) {
        this.id = id
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
        const val SHIP_ID: String = "shipId"
    }
}