package com.half.wowsca.ui.viewcaptain.tabs

import android.graphics.Color
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.getTextColor
import com.half.wowsca.CAApp.Companion.getTheme
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.CAApp.Companion.isColorblind
import com.half.wowsca.R
import com.half.wowsca.interfaces.ICaptain
import com.half.wowsca.model.Captain
import com.half.wowsca.model.CaptainReceivedEvent
import com.half.wowsca.model.ProgressEvent
import com.half.wowsca.model.RefreshEvent
import com.half.wowsca.model.Ship
import com.half.wowsca.model.ShipCompare
import com.half.wowsca.model.encyclopedia.items.ShipInfo
import com.half.wowsca.ui.CAFragment
import com.utilities.logging.Dlog.d
import org.greenrobot.eventbus.Subscribe
import java.util.Collections

/**
 * Created by slai4 on 9/15/2015.
 */
class CaptainGraphsFragment() : CAFragment() {
    private var chartAverageExperience: BarChart? = null
    private var chartAverageDamage: BarChart? = null
    private var chartAverageWinRate: BarChart? = null
    private var chartAverageSurvival: BarChart? = null
    private var chartAverageAccuracy: BarChart? = null
    private var chartAverageExperienceClass: HorizontalBarChart? = null
    private var chartAverageDamageClass: HorizontalBarChart? = null
    private var chartAverageWinRateClass: HorizontalBarChart? = null
    private var chartAverageSurvivalClass: HorizontalBarChart? = null

    private var chartTopTenPlayed: HorizontalBarChart? = null
    private var chartTopTenExp: HorizontalBarChart? = null
    private var chartTopTenDmg: HorizontalBarChart? = null
    private var chartTopTenWinRate: HorizontalBarChart? = null
    private var chartTopTenKD: HorizontalBarChart? = null
    private var chartTopTenAccuracy: HorizontalBarChart? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_captain_graphs, container, false)
        bindView(view)
        return view
    }

    private fun bindView(view: View) {
        chartAverageExperience = view.findViewById(R.id.captain_details_graphs_avg_exp_per_tier)
        chartAverageDamage = view.findViewById(R.id.captain_details_graphs_damage_per_tier)
        chartAverageWinRate = view.findViewById(R.id.captain_details_graphs_win_rate_per_tier)
        chartAverageSurvival = view.findViewById(R.id.captain_details_graphs_survival_per_tier)
        chartAverageAccuracy = view.findViewById(R.id.captain_details_graphs_accuracy_per_tier)

        chartAverageDamageClass = view.findViewById(R.id.captain_details_graphs_damage_per_class)
        chartAverageWinRateClass = view.findViewById(R.id.captain_details_graphs_win_rate_per_class)
        chartAverageExperienceClass =
            view.findViewById(R.id.captain_details_graphs_experience_per_class)
        chartAverageSurvivalClass =
            view.findViewById(R.id.captain_details_graphs_survival_rate_per_class)

        chartTopTenPlayed = view.findViewById(R.id.captain_details_graphs_top_ten_played)
        chartTopTenExp = view.findViewById(R.id.captain_details_graphs_top_ten_exp)
        chartTopTenDmg = view.findViewById(R.id.captain_details_graphs_top_ten_average_dmg)
        chartTopTenWinRate = view.findViewById(R.id.captain_details_graphs_top_ten_win_rate)
        chartTopTenKD = view.findViewById(R.id.captain_details_graphs_top_ten_k_d)
        chartTopTenAccuracy = view.findViewById(R.id.captain_details_graphs_top_ten_accuracy)

        bindSwipe(view)
        initSwipeLayout()
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
        var captain: Captain? = null
        try {
            captain = (activity as ICaptain?)!!.getCaptain(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if ((captain != null) && (captain.details != null) && (captain.details.battles > 0)) {
            refreshing(false)
            setUpCharts(captain)
        } else {
        }
    }

    private fun setUpCharts(captain: Captain) {
        val runnable: Runnable = object : Runnable {
            override fun run() {
                val battleCounts = SparseArray<Int>()
                val battleCountsClass: MutableMap<String, Int> = HashMap()

                val experience = SparseArray<Long>()
                val damages = SparseArray<Long>()
                val wins = SparseArray<Long>()
                val survivalRate = SparseArray<Long>()
                val accuracyHits = SparseArray<Long>()
                val accuracyShots = SparseArray<Long>()

                val damageClass: MutableMap<String, Long> = HashMap()
                val winsClass: MutableMap<String, Long> = HashMap()
                val expClass: MutableMap<String, Long> = HashMap()
                val survivalClass: MutableMap<String, Long> = HashMap()
                val shipsHolder = infoManager!!.getShipInfo((context)!!)
                if (captain.ships != null) {
                    for (s: Ship in captain.ships) {
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
                            val winRate = wins[tier]
                            if (winRate != null) {
                                wins.put(tier, winRate + s.wins)
                            } else {
                                wins.put(tier, s.wins.toLong())
                            }
                            val survival = survivalRate[tier]
                            if (survival != null) {
                                survivalRate.put(tier, survival + s.survivedBattles)
                            } else {
                                survivalRate.put(tier, s.survivedBattles.toLong())
                            }

                            val hits = accuracyHits[tier]
                            val stats = s.mainBattery
                            if (stats.shots > 0) {
                                if (hits != null) {
                                    accuracyHits.put(tier, hits + stats.hits)
                                } else {
                                    accuracyHits.put(tier, stats.hits.toLong())
                                }
                            }

                            val shots = accuracyShots[tier]
                            val statsShots = s.mainBattery
                            if (statsShots.shots > 0) {
                                if (shots != null) {
                                    accuracyShots.put(tier, shots + statsShots.shots)
                                } else {
                                    accuracyShots.put(tier, statsShots.shots.toLong())
                                }
                            }

                            //class area
                            val shipType = info.type
                            val battleCountC = battleCountsClass[shipType]
                            if (battleCountC != null) {
                                battleCountsClass[shipType] = battleCountC + s.battles
                            } else {
                                battleCountsClass[shipType] = s.battles
                            }
                            val damageC = damageClass[shipType]
                            if (damageC != null) {
                                damageClass[shipType] = (damageC + s.totalDamage).toLong()
                            } else {
                                damageClass[shipType] = s.totalDamage.toLong()
                            }
                            val winsC = winsClass[shipType]
                            if (winsC != null) {
                                winsClass[shipType] = winsC + s.wins
                            } else {
                                winsClass[shipType] = s.wins.toLong()
                            }
                            val expC = expClass[shipType]
                            if (expC != null) {
                                expClass[shipType] = expC + s.totalXP
                            } else {
                                expClass[shipType] = s.totalXP
                            }
                            val survivalC = survivalClass[shipType]
                            if (survivalC != null) {
                                survivalClass[shipType] = survivalC + s.survivedBattles
                            } else {
                                survivalClass[shipType] = s.survivedBattles.toLong()
                            }
                        }
                    }
                    val averages: MutableMap<Int, Long> = HashMap()
                    val avgDamages: MutableMap<Int, Long> = HashMap()
                    val avgWinRate: MutableMap<Int, Long> = HashMap()
                    val avgSuvival: MutableMap<Int, Long> = HashMap()
                    val avgAccuracy: MutableMap<Int, Long> = HashMap()
                    for (i in 1..10) {
                        val battleCount = battleCounts[i]
                        val exp = experience[i]
                        if ((battleCount != null) && (exp != null) && (battleCount > 0)) {
                            averages[i] = exp / battleCount
                        } else {
                            averages[i] = 0L
                        }
                        val damage = damages[i]
                        if (damage != null && battleCount!! > 0) {
                            avgDamages[i] = damage / (battleCount)
                        } else {
                            avgDamages[i] = 0L
                        }
                        val win = wins[i]
                        if (win != null && battleCount!! > 0) {
                            avgWinRate[i] = ((win / battleCount.toFloat()) * 100).toLong()
                        } else {
                            avgWinRate[i] = 0L
                        }
                        val survival = survivalRate[i]
                        if (survival != null && battleCount!! > 0) {
                            avgSuvival[i] = ((survival / battleCount.toFloat()) * 100).toLong()
                        } else {
                            avgSuvival[i] = 0L
                        }
                        val hits = accuracyHits[i]
                        val shots = accuracyShots[i]
                        if (hits != null && shots > 0) {
                            avgAccuracy[i] = ((hits / shots.toFloat()) * 100).toLong()
                        } else {
                            avgAccuracy[i] = 0L
                        }
                    }

                    for (key: String in battleCountsClass.keys) {
                        val battles = battleCountsClass[key]
                        if (battles!! > 0) {
                            val fBattles = battles.toFloat()
                            val damage = damageClass[key]
                            val winsC = winsClass[key]
                            val expC = expClass[key]
                            val survivalC = survivalClass[key]
                            if (damage != null) {
                                damageClass[key] = (damage / fBattles).toLong()
                            } else {
                                damageClass[key] = 0L
                            }
                            if (winsC != null) {
                                winsClass[key] = ((winsC / fBattles) * 100).toLong()
                            } else {
                                winsClass[key] = 0L
                            }
                            if (expC != null) {
                                expClass[key] = ((expC / fBattles)).toLong()
                            } else {
                                expClass[key] = 0L
                            }
                            if (survivalC != null) {
                                survivalClass[key] = ((survivalC / fBattles) * 100).toLong()
                            } else {
                                survivalClass[key] = 0L
                            }
                        } else {
                            damageClass[key] = 0L
                            winsClass[key] = 0L
                            expClass[key] = 0L
                            survivalClass[key] = 0L
                        }
                    }

                    val compare = ShipCompare()
                    compare.shipsHolder = shipsHolder

                    var shipsClone: MutableList<Ship>? = ArrayList()
                    for (s: Ship in captain.ships) {
                        if (s.battles > 0) shipsClone!!.add(s)
                    }
                    val battlesTen = TopTenObj()
                    Collections.sort(shipsClone, compare.battlesComparator)
                    run {
                        var i: Int = 0
                        while (i < 10 && i < shipsClone!!.size) {
                            val s: Ship = shipsClone!!.get(i)
                            val info: ShipInfo? = shipsHolder.get(s.getShipId())
                            var name: String? = s.getShipId().toString() + ""
                            if (info != null) name = info.getName()

                            battlesTen.names!!.add(name)
                            battlesTen.data!!.add(s.getBattles().toFloat())
                            i++
                        }
                    }
                    battlesTen.reverse()

                    val averageExpTen = TopTenObj()
                    Collections.sort(shipsClone, compare.averageExpComparator)
                    run {
                        var i: Int = 0
                        while (i < 10 && i < shipsClone!!.size) {
                            val s: Ship = shipsClone!!.get(i)
                            val info: ShipInfo? = shipsHolder.get(s.getShipId())
                            var name: String? = s.getShipId().toString() + ""
                            if (info != null) name = info.getName()

                            averageExpTen.names!!.add(name)
                            averageExpTen.data!!.add((s.getTotalXP() / s.getBattles().toFloat()))
                            i++
                        }
                    }
                    averageExpTen.reverse()

                    val averageDmgTen = TopTenObj()
                    Collections.sort(shipsClone, compare.averageDamageComparator)
                    run {
                        var i: Int = 0
                        while (i < 10 && i < shipsClone!!.size) {
                            val s: Ship = shipsClone!!.get(i)
                            val info: ShipInfo? = shipsHolder.get(s.getShipId())
                            var name: String? = s.getShipId().toString() + ""
                            if (info != null) name = info.getName()

                            averageDmgTen.names!!.add(name)
                            averageDmgTen.data!!.add(
                                (s.getTotalDamage() / s.getBattles().toFloat()).toFloat()
                            )
                            i++
                        }
                    }
                    averageDmgTen.reverse()

                    val averageWRTen = TopTenObj()
                    Collections.sort(shipsClone, compare.winRateComparator)
                    run {
                        var i: Int = 0
                        while (averageWRTen.names!!.size < 10 && i < shipsClone!!.size) {
                            val s: Ship = shipsClone!!.get(i)
                            if (s.getBattles() > 4) {
                                val info: ShipInfo? = shipsHolder.get(s.getShipId())
                                var name: String? = s.getShipId().toString() + ""
                                if (info != null) name = info.getName()

                                averageWRTen.names!!.add(name)
                                averageWRTen.data!!.add(
                                    ((s.getWins().toFloat() / s.getBattles().toFloat()) * 100f)
                                )
                            }
                            i++
                        }
                    }
                    averageWRTen.reverse()

                    val averageKDTen = TopTenObj()
                    Collections.sort(shipsClone, compare.killsDeathComparator)
                    run {
                        var i: Int = 0
                        while (i < 10 && i < shipsClone!!.size) {
                            val s: Ship = shipsClone!!.get(i)
                            val info: ShipInfo? = shipsHolder.get(s.getShipId())
                            var name: String? = s.getShipId().toString() + ""
                            if (info != null) name = info.getName()

                            averageKDTen.names!!.add(name)
                            var deaths: Float = (s.getBattles() - s.getSurvivedBattles()).toFloat()
                            if (deaths <= 1) deaths = 1f
                            val frags: Float = s.getFrags().toFloat()
                            averageKDTen.data!!.add(frags / deaths)
                            i++
                        }
                    }
                    averageKDTen.reverse()

                    val averageAccuracyTen = TopTenObj()
                    Collections.sort(shipsClone, compare.accuracyComparator)
                    var i = 0
                    while (i < 10 && i < shipsClone!!.size) {
                        val s = shipsClone[i]
                        val info = shipsHolder[s.shipId]
                        var name: String? = s.shipId.toString() + ""
                        if (info != null) name = info.name

                        averageAccuracyTen.names!!.add(name)
                        val shots = s.mainBattery.shots.toFloat()
                        val hits = s.mainBattery.hits.toFloat()
                        if (shots > 0) averageAccuracyTen.data!!.add((hits / shots) * 100f)
                        i++
                    }
                    averageAccuracyTen.reverse()

                    if (avgDamages.size > 0) setUpBarChart(chartAverageDamage, avgDamages, true)
                    if (averages.size > 0) setUpBarChart(chartAverageExperience, averages, true)
                    if (avgSuvival.size > 0) setUpBarChart(chartAverageSurvival, avgSuvival, true)
                    if (avgWinRate.size > 0) setUpBarChart(chartAverageWinRate, avgWinRate, true)
                    if (avgAccuracy.size > 0) setUpBarChart(
                        chartAverageAccuracy,
                        avgAccuracy,
                        false
                    )

                    if (damageClass.size > 0) setUpClassCharts(
                        chartAverageDamageClass,
                        damageClass,
                        false
                    )
                    if (winsClass.size > 0) setUpClassCharts(
                        chartAverageWinRateClass,
                        winsClass,
                        true
                    )
                    if (expClass.size > 0) setUpClassCharts(
                        chartAverageExperienceClass,
                        expClass,
                        false
                    )
                    if (survivalClass.size > 0) setUpClassCharts(
                        chartAverageSurvivalClass,
                        survivalClass,
                        true
                    )

                    if (battlesTen.count() > 0) setUpTopTenCharts(
                        chartTopTenPlayed,
                        battlesTen,
                        false
                    )
                    if (averageExpTen.count() > 0) setUpTopTenCharts(
                        chartTopTenExp,
                        averageExpTen,
                        false
                    )
                    if (averageDmgTen.count() > 0) setUpTopTenCharts(
                        chartTopTenDmg,
                        averageDmgTen,
                        false
                    )
                    if (averageKDTen.count() > 0) setUpTopTenCharts(
                        chartTopTenKD,
                        averageKDTen,
                        false
                    )
                    if (averageWRTen.count() > 0) setUpTopTenCharts(
                        chartTopTenWinRate,
                        averageWRTen,
                        true
                    )
                    if (averageAccuracyTen.count() > 0) setUpTopTenCharts(
                        chartTopTenAccuracy,
                        averageAccuracyTen,
                        true
                    )

                    shipsClone = null
                }
            }

            private fun grabTopTen(ships: List<Ship>): Map<Long, Ship> {
                val topTen: MutableMap<Long, Ship> = HashMap()
                var i = 0
                while (i < 10 && i < ships.size) {
                    topTen[ships.get(i).shipId] = ships.get(i)
                    i++
                }
                return topTen
            }

            private fun setUpTopTenCharts(
                chart: HorizontalBarChart?,
                averages: TopTenObj,
                percentage: Boolean
            ) {
                chart!!.post(Runnable {
                    val textColor = getTextColor(chart.context)
                    val colorblind = isColorblind(chart.context)
                    val accentColor = if (!colorblind) (if ((getTheme(
                            chart.context
                        ) == "ocean")
                    ) ContextCompat.getColor(
                        chart.context,
                        R.color.graph_line_color
                    ) else ContextCompat.getColor(
                        chart.context, R.color.top_background
                    ))
                    else ContextCompat.getColor(chart.context, R.color.white)
                    chart.setDrawBarShadow(false)
                    chart.setDrawValueAboveBar(false)
                    chart.setPinchZoom(false)
                    chart.isDoubleTapToZoomEnabled = false
                    chart.setDrawGridBackground(false)
                    chart.setDrawValueAboveBar(true)
                    chart.setTouchEnabled(false)

                    setupXAxis(textColor, chart)

                    setupYAxis(textColor, chart)

                    setupYAxis2(textColor, chart)

                    val l = chart.legend
                    l.isEnabled = false

                    val xVals = averages.names

                    val yVals1 = ArrayList<BarEntry>()
                    for (i in averages.data!!.indices) {
                        val dValue = averages.data!![i]!!.toDouble()
                        val value = dValue.toFloat()
                        yVals1.add(BarEntry(value, i))
                    }

                    val set1 = BarDataSet(yVals1, "")
                    set1.barSpacePercent = 20f
                    set1.color = accentColor

                    val dataSets = ArrayList<IBarDataSet>()
                    dataSets.add(set1)

                    val data = BarData(xVals, dataSets)
                    data.setValueTextSize(10f)
                    data.setValueTextColor(textColor)
                    if (!percentage) {
//                            data.setValueFormatter(new LargeValueFormatter());
                    } else {
                        data.setValueFormatter(PercentFormatter())
                    }
                    chart.setDescription("")

                    chart.data = data
                    chart.requestLayout()
                })
            }

            private fun setUpBarChart(
                chart: BarChart?,
                averages: Map<Int, Long>,
                useLargeFormatter: Boolean
            ) {
                chart!!.post(object : Runnable {
                    override fun run() {
                        val textColor = getTextColor(chart.context)
                        val colorblind = isColorblind(chart.context)
                        val accentColor = if (!colorblind) (if ((getTheme(
                                chart.context
                            ) == "ocean")
                        ) ContextCompat.getColor(
                            chart.context,
                            R.color.graph_line_color
                        ) else ContextCompat.getColor(
                            chart.context, R.color.top_background
                        ))
                        else ContextCompat.getColor(chart.context, R.color.white)
                        chart.setDrawBarShadow(false)
                        chart.setDrawValueAboveBar(false)
                        chart.setPinchZoom(false)
                        chart.isDoubleTapToZoomEnabled = false
                        chart.setDrawGridBackground(false)
                        chart.setDrawValueAboveBar(true)
                        chart.setTouchEnabled(false)


                        setupXAxis(textColor, chart)

                        setupYAxis(textColor, chart)


                        val yAxis2 = chart.axisLeft
                        yAxis2.setLabelCount(6, false)
                        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                        yAxis2.textColor = textColor
                        yAxis2.valueFormatter = LargeValueFormatter()

                        val l = chart.legend
                        l.isEnabled = false

                        val xVals = ArrayList<String>()
                        for (i in 1..10) xVals.add(i.toString() + "")

                        val yVals1 = ArrayList<BarEntry>()
                        val colorList: List<Int> = ArrayList()
                        for (i in 0..9) {
                            val dValue = averages[i + 1]!!.toDouble()
                            val value = dValue.toFloat()
                            yVals1.add(BarEntry(value, i))
                        }

                        //                        cleanUpTitles(type, xVals);
                        val set1 = BarDataSet(yVals1, "")
                        set1.barSpacePercent = 20f
                        set1.color = accentColor
                        val dataSets = ArrayList<IBarDataSet>()
                        dataSets.add(set1)

                        val data = BarData(xVals, dataSets)
                        data.setValueTextSize(10f)
                        data.setValueTextColor(textColor)
                        if (useLargeFormatter) data.setValueFormatter(LargeValueFormatter())
                        else data.setValueFormatter(PercentFormatter())

                        chart.setDescription("")

                        chart.data = data
                        chart.requestLayout()
                    }
                })
            }

            private fun setUpClassCharts(
                chart: HorizontalBarChart?,
                averages: Map<String, Long>,
                percentage: Boolean
            ) {
                chart!!.post(object : Runnable {
                    override fun run() {
                        val textColor = getTextColor(chart.context)
                        val colorblind = isColorblind(chart.context)

                        chart.setDrawBarShadow(false)
                        chart.setDrawValueAboveBar(false)
                        chart.setPinchZoom(false)
                        chart.isDoubleTapToZoomEnabled = false
                        chart.setDrawGridBackground(false)
                        chart.setDrawValueAboveBar(true)
                        chart.setTouchEnabled(false)

                        setupXAxis(textColor, chart)

                        setupYAxis(textColor, chart)

                        setupYAxis2(textColor, chart)

                        val l = chart.legend
                        l.isEnabled = false

                        val xVals = ArrayList<String>()
                        val itea = averages.keys.iterator()
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
                            val dValue = averages[xVals[i]]!!.toDouble()
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
                        if (!percentage) {
                            data.setValueFormatter(LargeValueFormatter())
                        } else {
                            data.setValueFormatter(PercentFormatter())
                        }
                        chart.setDescription("")

                        chart.data = data
                        chart.requestLayout()
                    }
                })
            }
        }
        Thread(runnable).start()
    }

    private fun setupYAxis2(textColor: Int, chart: HorizontalBarChart?) {
        val yAxis2 = chart!!.axisLeft
        yAxis2.setLabelCount(6, false)
        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis2.textColor = textColor
    }

    private fun setupYAxis(textColor: Int, chart: BarChart?) {
        val yAxis = chart!!.axisRight
        yAxis.setLabelCount(4, false)
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.textColor = textColor
        yAxis.isEnabled = false
    }

    private fun setupXAxis(textColor: Int, chart: BarChart?) {
        val xAxis = chart!!.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = textColor
        xAxis.setDrawGridLines(true)
    }

    @Subscribe
    fun onReceive(event: CaptainReceivedEvent?) {
        initView()
    }

    @Subscribe
    fun onRefresh(event: RefreshEvent?) {
        refreshing(true)
        chartAverageExperienceClass!!.clear()
        chartAverageExperience!!.clear()
        chartAverageWinRateClass!!.clear()
        chartAverageWinRate!!.clear()
        chartAverageDamageClass!!.clear()
        chartAverageDamage!!.clear()
        chartAverageSurvivalClass!!.clear()
        chartAverageSurvival!!.clear()
    }

    @Subscribe
    fun onProgressEvent(event: ProgressEvent) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout!!.isRefreshing = event.isRefreshing
        }
    }

    private inner class TopTenObj() {
        var names: ArrayList<String?>?
        var data: ArrayList<Float?>?

        init {
            this.names = ArrayList()
            this.data = ArrayList()
        }

        fun reverse() {
            Collections.reverse(names)
            Collections.reverse(data)
        }

        fun print() {
            d("names", names.toString())
            d("data", data.toString())
        }

        fun count(): Int {
            var total = 0
            if (names != null) total = names!!.size
            else if (data != null) total = data!!.size
            return total
        }
    }
}
