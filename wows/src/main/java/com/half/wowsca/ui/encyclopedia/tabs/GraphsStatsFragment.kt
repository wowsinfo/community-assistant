package com.half.wowsca.ui.encyclopedia.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.R
import com.half.wowsca.model.encyclopedia.items.ShipInfo
import com.half.wowsca.model.enums.EncyclopediaType
import com.half.wowsca.model.listModels.EncyclopediaChild
import com.half.wowsca.ui.adapter.ExpandableStatsAdapter
import java.util.Collections

/**
 * Created by slai4 on 12/1/2015.
 */
class GraphsStatsFragment : Fragment() {
    var listView: ExpandableListView? = null
    var adapter: ExpandableStatsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_encyclopedia_stats, container, false)
        bindView(view)
        return view
    }

    private fun bindView(view: View) {
        listView = view.findViewById(R.id.encyclopedia_stats_list)
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView() {
        val headers: MutableList<String> = ArrayList()
        val data: MutableMap<String, List<EncyclopediaChild>> = HashMap()

        if (listView!!.adapter == null) {
            val shipsInfos = shipInfos

            createTierInfo(10, shipsInfos[10], headers, data)
            createTierInfo(PREMIUM_KEY, shipsInfos[PREMIUM_KEY], headers, data)
            createTierInfo(9, shipsInfos[9], headers, data)
            createTierInfo(8, shipsInfos[8], headers, data)
            createTierInfo(7, shipsInfos[7], headers, data)
            createTierInfo(6, shipsInfos[6], headers, data)
            createTierInfo(5, shipsInfos[5], headers, data)
            createTierInfo(4, shipsInfos[4], headers, data)
            createTierInfo(3, shipsInfos[3], headers, data)
            createTierInfo(2, shipsInfos[2], headers, data)
            createTierInfo(1, shipsInfos[1], headers, data)

            adapter = ExpandableStatsAdapter(headers, data, requireContext())
            listView!!.setAdapter(adapter)
        } else {
        }
    }

    private fun createTierInfo(
        tier: Int,
        ships: List<ShipInfo>?,
        headers: MutableList<String>,
        data: MutableMap<String, List<EncyclopediaChild>>
    ) {
        if (ships != null && !ships.isEmpty()) {
            val children: MutableList<EncyclopediaChild> = ArrayList()

            val shipsTitles: MutableList<String> = ArrayList()
            val damages: MutableList<Float> = ArrayList()
            val winRate: MutableList<Float> = ArrayList()
            val kills: MutableList<Float> = ArrayList()
            val planesDropped: MutableList<Float> = ArrayList()
            val types: MutableList<String> = ArrayList()

            val stats = infoManager!!.getShipStats(listView!!.context)

            Collections.sort(ships) { lhs, rhs -> rhs.name.compareTo(lhs.name, ignoreCase = true) }

            if (tier != PREMIUM_KEY) {
                Collections.sort(ships) { lhs, rhs ->
                    lhs.type.compareTo(
                        rhs.type,
                        ignoreCase = true
                    )
                }
            } else {
                Collections.sort(ships) { lhs, rhs -> lhs.tier - rhs.tier }
            }

            for (info in ships) {
                val stat = stats[info.shipId]
                if (stat != null) {
                    shipsTitles.add(info.name)
                    damages.add(stat.dmg_dlt)
                    winRate.add(stat.wins)
                    kills.add(stat.frags)
                    planesDropped.add(stat.pls_kd)
                    types.add(info.type)
                }
            }

            //            Dlog.wtf("Tier " + tier, "kills = " + kills.toString());
            children.add(
                EncyclopediaChild.create(
                    EncyclopediaType.LARGE_NUMBER,
                    damages,
                    shipsTitles,
                    types,
                    getString(R.string.encyclopedia_average_damage)
                )
            )
            children.add(
                EncyclopediaChild.create(
                    EncyclopediaType.PERCENT,
                    winRate,
                    shipsTitles,
                    types,
                    getString(R.string.encyclopedia_winrate)
                )
            )
            children.add(
                EncyclopediaChild.create(
                    EncyclopediaType.NONE,
                    kills,
                    shipsTitles,
                    types,
                    getString(R.string.encyclopedia_kills)
                )
            )

            children.add(
                EncyclopediaChild.create(
                    EncyclopediaType.NONE,
                    planesDropped,
                    shipsTitles,
                    types,
                    getString(R.string.planes_downed_encyclo)
                )
            )

            var header =
                getString(R.string.encyclopedia_tier_start) + " " + tier + " " + getString(R.string.encyclopedia_tier_end)
            if (tier == PREMIUM_KEY) {
                header = getString(R.string.premium_ship_stats)
            }
            headers.add(header)
            data[header] = children
        }
    }

    private val shipInfos: Map<Int, MutableList<ShipInfo>>
        get() {
            val ships: MutableMap<Int, MutableList<ShipInfo>> = HashMap()
            ships[1] = ArrayList()
            ships[2] = ArrayList()
            ships[3] = ArrayList()
            ships[4] = ArrayList()
            ships[5] = ArrayList()
            ships[6] = ArrayList()
            ships[7] = ArrayList()
            ships[8] = ArrayList()
            ships[9] = ArrayList()
            ships[10] = ArrayList()
            ships[PREMIUM_KEY] = ArrayList()

            try {
                infoManager?.getShipInfo(listView!!.context)?.items?.values?.forEach { info ->
                    ships[info?.tier]?.add(info!!)
                    if (info?.isPremium == true) ships[PREMIUM_KEY]?.add(info)
                }

            } catch (e: Exception) {
                Toast.makeText(context, getString(R.string.resources_error), Toast.LENGTH_SHORT)
                    .show()
            }

            return ships
        }

    companion object {
        const val PREMIUM_KEY: Int = 47
    }
}
