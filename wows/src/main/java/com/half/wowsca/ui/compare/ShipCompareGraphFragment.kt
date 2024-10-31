package com.half.wowsca.ui.compare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.utils.ColorTemplate
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.R
import com.half.wowsca.managers.CompareManager.getSHIPS
import com.half.wowsca.managers.CompareManager.shipInformation
import com.half.wowsca.managers.InfoManager
import com.half.wowsca.model.ShipInformation
import com.half.wowsca.ui.CAFragment
import com.half.wowsca.ui.adapter.ShipsCompareAdapter
import org.greenrobot.eventbus.Subscribe

/**
 * Created by slai47 on 4/19/2017.
 */
class ShipCompareGraphFragment : CAFragment() {
    var recyclerView: RecyclerView? = null
    var adapter: ShipsCompareAdapter? = null


    /**
     * this will produce all the graphs from the ship data grabbed
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_compare_ships_graphs, container, false)
        onBind(view)
        return view
    }

    private fun onBind(view: View) {
        recyclerView = view.findViewById(R.id.compare_ships_list)
        val manager = GridLayoutManager(view.context, resources.getInteger(R.integer.twitch_cols))
        recyclerView?.setLayoutManager(manager)
    }

    override fun onResume() {
        super.onResume()
        initView()
        eventBus.register(this)
    }

    override fun onPause() {
        super.onPause()
        eventBus.unregister(this)
    }

    private fun initView() {
        val holder = InfoManager().getShipInfo(recyclerView!!.context)

        val graphsList: MutableList<Map<String, Float>> = ArrayList()
        val graphNames: MutableList<String> = ArrayList()
        val shipColors: MutableMap<String, Int> = HashMap()

        val health: MutableMap<String, Float> = HashMap()
        val survivalHealth: MutableMap<String, Float> = HashMap()
        val planeAmounts: MutableMap<String, Float> = HashMap()

        val overallMin: MutableMap<String, Float> = HashMap()
        val overallMax: MutableMap<String, Float> = HashMap()
        val deckMin: MutableMap<String, Float> = HashMap()
        val deckMax: MutableMap<String, Float> = HashMap()
        val extremitiesMin: MutableMap<String, Float> = HashMap()
        val extremitiesMax: MutableMap<String, Float> = HashMap()
        val casemateMin: MutableMap<String, Float> = HashMap()
        val casemateMax: MutableMap<String, Float> = HashMap()
        val citadelMin: MutableMap<String, Float> = HashMap()
        val citadelMax: MutableMap<String, Float> = HashMap()

        val artilleryTotal: MutableMap<String, Float> = HashMap()
        val torpTotal: MutableMap<String, Float> = HashMap()
        val antiAirTotal: MutableMap<String, Float> = HashMap()
        val aircraftTotal: MutableMap<String, Float> = HashMap()
        val mobilityTotal: MutableMap<String, Float> = HashMap()
        val concealmentTotal: MutableMap<String, Float> = HashMap()

        val turningRadius: MutableMap<String, Float> = HashMap()

        val speed: MutableMap<String, Float> = HashMap()
        val rudderTime: MutableMap<String, Float> = HashMap()

        val artiDistance: MutableMap<String, Float> = HashMap()
        val secondaryRange: MutableMap<String, Float> = HashMap()

        val torpDistance: MutableMap<String, Float> = HashMap()
        val torpReload: MutableMap<String, Float> = HashMap()
        val torpMaxDamage: MutableMap<String, Float> = HashMap()
        val torpSpeed: MutableMap<String, Float> = HashMap()
        val torpSlots: MutableMap<String, Float> = HashMap()
        val torpBarrels: MutableMap<String, Float> = HashMap()
        val torpGuns: MutableMap<String, Float> = HashMap()

        val torpBDistance: MutableMap<String, Float> = HashMap()
        val torpBprepareTime: MutableMap<String, Float> = HashMap()
        val torpBDamage: MutableMap<String, Float> = HashMap()
        val torpBMaxSpeed: MutableMap<String, Float> = HashMap()
        val fighterSquadrons: MutableMap<String, Float> = HashMap()
        val bomberSquadrons: MutableMap<String, Float> = HashMap()
        val torpedoSquadrons: MutableMap<String, Float> = HashMap()

        val concealmentDistanceShip: MutableMap<String, Float> = HashMap()
        val concealmentDistancePlane: MutableMap<String, Float> = HashMap()

        val artiAPDmg: MutableMap<String, Float> = HashMap()
        val artiHEDmg: MutableMap<String, Float> = HashMap()
        val artiHEBurnProb: MutableMap<String, Float> = HashMap()
        val artiRotation: MutableMap<String, Float> = HashMap()
        val artiMaxDispersion: MutableMap<String, Float> = HashMap()
        val artiTurrets: MutableMap<String, Float> = HashMap()
        val artiBarrels: MutableMap<String, Float> = HashMap()
        val artiGunRate: MutableMap<String, Float> = HashMap()

        val diveBPrepareTime: MutableMap<String, Float> = HashMap()
        val diveBMaxDmg: MutableMap<String, Float> = HashMap()
        val diveBBurnProbably: MutableMap<String, Float> = HashMap()

        val topAARange: MutableMap<String, Float> = HashMap()


        val warshipStatsDmg: MutableMap<String, Float> = HashMap()
        val warshipStatsWR: MutableMap<String, Float> = HashMap()
        val warshipStatsKills: MutableMap<String, Float> = HashMap()
        val warshipStatsPlanes: MutableMap<String, Float> = HashMap()

        val colors = ColorTemplate.PASTEL_COLORS
        var i = 0

        for (shipId in getSHIPS()!!) {
            val shipInfo = shipInformation!![shipId]
            if (shipInfo != null) {
                val ship = ShipInformation()
                ship.parse(shipInfo)
                val shipName = holder[shipId]!!.name

                shipColors[shipName] = colors[i]

                addGraphInfo(health, shipName, ship.health.toFloat())

                addGraphInfo(survivalHealth, shipName, ship.survivalHealth.toFloat())
                addGraphInfo(planeAmounts, shipName, ship.planesAmount.toFloat())

                addGraphInfo(overallMin, shipName, ship.overallMin.toFloat())
                addGraphInfo(overallMax, shipName, ship.overallMax.toFloat())
                addGraphInfo(deckMin, shipName, ship.deckMin.toFloat())
                addGraphInfo(deckMax, shipName, ship.deckMax.toFloat())
                addGraphInfo(extremitiesMin, shipName, ship.extremitiesMin.toFloat())
                addGraphInfo(extremitiesMax, shipName, ship.extremitiesMax.toFloat())
                addGraphInfo(casemateMin, shipName, ship.casemateMin.toFloat())
                addGraphInfo(casemateMax, shipName, ship.casemateMax.toFloat())
                addGraphInfo(citadelMin, shipName, ship.citadelMin.toFloat())
                addGraphInfo(citadelMax, shipName, ship.citadelMax.toFloat())

                addGraphInfo(artilleryTotal, shipName, ship.artilleryTotal.toFloat())
                addGraphInfo(torpTotal, shipName, ship.torpTotal.toFloat())
                addGraphInfo(antiAirTotal, shipName, ship.antiAirTotal.toFloat())
                addGraphInfo(aircraftTotal, shipName, ship.aircraftTotal.toFloat())
                addGraphInfo(mobilityTotal, shipName, ship.mobilityTotal.toFloat())
                addGraphInfo(concealmentTotal, shipName, ship.concealmentTotal.toFloat())


                addGraphInfo(turningRadius, shipName, ship.turningRadius.toFloat())

                addGraphInfo(speed, shipName, ship.speed.toFloat())
                addGraphInfo(rudderTime, shipName, ship.rudderTime.toFloat())

                addGraphInfo(artiDistance, shipName, ship.artiDistance.toFloat())
                addGraphInfo(secondaryRange, shipName, ship.secondaryRange.toFloat())

                addGraphInfo(torpDistance, shipName, ship.torpDistance.toFloat())
                addGraphInfo(torpReload, shipName, ship.torpReload.toFloat())
                addGraphInfo(torpMaxDamage, shipName, ship.torpMaxDamage.toFloat())
                addGraphInfo(torpSpeed, shipName, ship.torpSpeed.toFloat())
                addGraphInfo(torpSlots, shipName, ship.torpSlots.toFloat())
                addGraphInfo(torpBarrels, shipName, ship.torpBarrels.toFloat())
                addGraphInfo(torpGuns, shipName, ship.torpGuns.toFloat())


                addGraphInfo(torpBDistance, shipName, ship.getTorpBDistance().toFloat())
                addGraphInfo(torpBprepareTime, shipName, ship.torpBprepareTime.toFloat())
                addGraphInfo(torpBDamage, shipName, ship.torpBDamage.toFloat())
                addGraphInfo(torpBMaxSpeed, shipName, ship.torpBMaxSpeed.toFloat())
                addGraphInfo(fighterSquadrons, shipName, ship.fighterSquadrons.toFloat())
                addGraphInfo(bomberSquadrons, shipName, ship.bomberSquadrons.toFloat())
                addGraphInfo(torpedoSquadrons, shipName, ship.torpedoSquadrons.toFloat())

                addGraphInfo(
                    concealmentDistanceShip,
                    shipName,
                    ship.concealmentDistanceShip.toFloat()
                )
                addGraphInfo(
                    concealmentDistancePlane,
                    shipName,
                    ship.concealmentDistancePlane.toFloat()
                )

                addGraphInfo(artiAPDmg, shipName, ship.artiAPDmg.toFloat())
                addGraphInfo(artiHEDmg, shipName, ship.artiHEDmg.toFloat())
                addGraphInfo(artiHEBurnProb, shipName, ship.artiHEBurnProb.toFloat())
                addGraphInfo(artiRotation, shipName, ship.artiRotation.toFloat())
                addGraphInfo(artiMaxDispersion, shipName, ship.artiMaxDispersion.toFloat())
                addGraphInfo(artiTurrets, shipName, ship.artiTurrets.toFloat())
                addGraphInfo(artiBarrels, shipName, ship.artiBarrels.toFloat())
                addGraphInfo(artiGunRate, shipName, ship.artiGunRate.toFloat())

                addGraphInfo(diveBPrepareTime, shipName, ship.diveBPrepareTime.toFloat())
                addGraphInfo(diveBMaxDmg, shipName, ship.diveBMaxDmg.toFloat())
                addGraphInfo(diveBBurnProbably, shipName, ship.diveBBurnProbably.toFloat())

                addGraphInfo(topAARange, shipName, ship.topAARange.toFloat())

                val stats = infoManager!!.getShipStats(requireContext())[shipId]
                if (stats != null) {
                    addGraphInfo(warshipStatsDmg, shipName, stats.dmg_dlt)
                    addGraphInfo(warshipStatsWR, shipName, (stats.wins * 100))
                    addGraphInfo(warshipStatsKills, shipName, stats.frags)
                    addGraphInfo(warshipStatsPlanes, shipName, stats.pls_kd)
                }
                i++
            }
        }

        setGraphInfo(
            graphsList,
            graphNames,
            artilleryTotal,
            getString(R.string.encyclopedia_artillery)
        )

        setGraphInfo(graphsList, graphNames, torpTotal, getString(R.string.encyclopedia_torps))
        setGraphInfo(graphsList, graphNames, antiAirTotal, getString(R.string.encyclopedia_aa_def))
        setGraphInfo(
            graphsList,
            graphNames,
            aircraftTotal,
            getString(R.string.encyclopedia_aircraft)
        )
        setGraphInfo(
            graphsList,
            graphNames,
            mobilityTotal,
            getString(R.string.encyclopedia_mobility)
        )
        setGraphInfo(
            graphsList,
            graphNames,
            concealmentTotal,
            getString(R.string.encyclopedia_concealment)
        )

        setGraphInfo(graphsList, graphNames, health, getString(R.string.encyclopedia_survivability))

        setGraphInfo(
            graphsList,
            graphNames,
            survivalHealth,
            getString(R.string.encyclopedia_health)
        )

        setGraphInfo(
            graphsList,
            graphNames,
            concealmentDistanceShip,
            getString(R.string.encyclopedia_spotted_range)
        )
        setGraphInfo(
            graphsList,
            graphNames,
            concealmentDistancePlane,
            getString(R.string.encyclopedia_aircraft_spotted_range)
        )

        setGraphInfo(
            graphsList,
            graphNames,
            turningRadius,
            getString(R.string.encyclopedia_turning_radius)
        )

        setGraphInfo(graphsList, graphNames, speed, getString(R.string.encyclopedia_speed))
        setGraphInfo(
            graphsList,
            graphNames,
            rudderTime,
            getString(R.string.encyclopedia_rudder_time)
        )

        setGraphInfo(
            graphsList,
            graphNames,
            artiDistance,
            getString(R.string.encyclopedia_gun_range)
        )
        setGraphInfo(
            graphsList,
            graphNames,
            secondaryRange,
            getString(R.string.encyclopedia_secondary_range)
        )

        setGraphInfo(graphsList, graphNames, artiAPDmg, getString(R.string.ap_damage))
        setGraphInfo(graphsList, graphNames, artiHEDmg, getString(R.string.he_damage))
        setGraphInfo(graphsList, graphNames, artiHEBurnProb, getString(R.string.he_burn_chance))
        setGraphInfo(graphsList, graphNames, artiRotation, getString(R.string.gun_rotation_speed))
        setGraphInfo(graphsList, graphNames, artiMaxDispersion, getString(R.string.gun_dispersion))
        setGraphInfo(graphsList, graphNames, artiTurrets, getString(R.string.num_of_turrets))
        setGraphInfo(graphsList, graphNames, artiBarrels, getString(R.string.encyclopedia_num_guns))
        setGraphInfo(graphsList, graphNames, artiGunRate, getString(R.string.encyclopedia_arty_fr))

        setGraphInfo(graphsList, graphNames, overallMin, getString(R.string.minimum_armor))
        setGraphInfo(graphsList, graphNames, overallMax, getString(R.string.maximum_armor))
        setGraphInfo(graphsList, graphNames, deckMin, getString(R.string.deck_minimum_armor))
        setGraphInfo(graphsList, graphNames, deckMax, getString(R.string.deck_maximum_armor))
        setGraphInfo(
            graphsList,
            graphNames,
            extremitiesMin,
            getString(R.string.extremities_min_armor)
        )
        setGraphInfo(
            graphsList,
            graphNames,
            extremitiesMax,
            getString(R.string.extremities_max_armor)
        )
        setGraphInfo(graphsList, graphNames, casemateMin, getString(R.string.casemate_min_armor))
        setGraphInfo(graphsList, graphNames, casemateMax, getString(R.string.casemate_max_armor))
        setGraphInfo(graphsList, graphNames, citadelMin, getString(R.string.citadel_min_armor))
        setGraphInfo(graphsList, graphNames, citadelMax, getString(R.string.citadel_max_armor))

        setGraphInfo(
            graphsList,
            graphNames,
            torpDistance,
            getString(R.string.encyclopedia_torp_range)
        )
        setGraphInfo(graphsList, graphNames, torpReload, getString(R.string.encyclopedia_torp_fr))
        setGraphInfo(
            graphsList,
            graphNames,
            torpMaxDamage,
            getString(R.string.encyclopedia_torp_damage)
        )
        setGraphInfo(graphsList, graphNames, torpSpeed, getString(R.string.encyclopedia_torp_speed))
        setGraphInfo(graphsList, graphNames, torpSlots, getString(R.string.num_of_torp_turret))
        setGraphInfo(
            graphsList,
            graphNames,
            torpBarrels,
            getString(R.string.encyclopedia_num_torps)
        )
        setGraphInfo(graphsList, graphNames, torpGuns, getString(R.string.encyclopedia_num_torps))

        setGraphInfo(
            graphsList,
            graphNames,
            planeAmounts,
            getString(R.string.encyclopedia_num_planes)
        )

        setGraphInfo(
            graphsList,
            graphNames,
            fighterSquadrons,
            getString(R.string.fighter_squadrons)
        )
        setGraphInfo(graphsList, graphNames, bomberSquadrons, getString(R.string.bomber_squadron))
        setGraphInfo(
            graphsList,
            graphNames,
            torpedoSquadrons,
            getString(R.string.torpedo_squadrons)
        )

        setGraphInfo(
            graphsList,
            graphNames,
            torpBDistance,
            getString(R.string.encyclopedia_torp_range)
        )
        setGraphInfo(
            graphsList,
            graphNames,
            torpBprepareTime,
            getString(R.string.encyclopedia_tb_prep_time)
        )
        setGraphInfo(
            graphsList,
            graphNames,
            torpBDamage,
            getString(R.string.encyclopedia_tb_torp_dam)
        )
        setGraphInfo(
            graphsList,
            graphNames,
            torpBMaxSpeed,
            getString(R.string.encyclopedia_tb_torp_speed)
        )

        setGraphInfo(
            graphsList,
            graphNames,
            diveBPrepareTime,
            getString(R.string.encyclopedia_db_prep_time)
        )
        setGraphInfo(
            graphsList,
            graphNames,
            diveBMaxDmg,
            getString(R.string.encyclopedia_db_damage)
        )
        setGraphInfo(graphsList, graphNames, diveBBurnProbably, getString(R.string.db_burn_chance))

        setGraphInfo(graphsList, graphNames, warshipStatsDmg, getString(R.string.damage))
        setGraphInfo(graphsList, graphNames, warshipStatsWR, getString(R.string.win_rate))
        setGraphInfo(graphsList, graphNames, warshipStatsKills, getString(R.string.kills_game))
        setGraphInfo(
            graphsList,
            graphNames,
            warshipStatsPlanes,
            getString(R.string.planes_downed_game)
        )

        if (adapter == null) adapter = ShipsCompareAdapter()

        adapter!!.setShips(graphsList)
        adapter!!.graphNames = graphNames
        adapter!!.shipColors = shipColors

        if (recyclerView!!.adapter == null) recyclerView!!.adapter = adapter
    }

    private fun setGraphInfo(
        graphs: MutableList<Map<String, Float>>,
        graphNames: MutableList<String>,
        graphInfo: Map<String, Float>,
        title: String
    ) {
        if (graphInfo.size > 0) {
            graphs.add(graphInfo)
            graphNames.add(title)
        }
    }

    private fun addGraphInfo(map: MutableMap<String, Float>, shipName: String, value: Float) {
        if (value != 0.0f) {
            map[shipName] = value
        }
    }

    @Subscribe
    fun onRefresh(shipId: Long?) {
        initView()
    }
}