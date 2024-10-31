package com.half.wowsca.ui.viewcaptain.tabs

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.R
import com.half.wowsca.interfaces.ICaptain
import com.half.wowsca.model.BatteryStats
import com.half.wowsca.model.Captain
import com.half.wowsca.model.CaptainReceivedEvent
import com.half.wowsca.model.ProgressEvent
import com.half.wowsca.model.RefreshEvent
import com.half.wowsca.model.Ship
import com.half.wowsca.model.ShipClickedEvent
import com.half.wowsca.model.encyclopedia.items.ShipInfo
import com.half.wowsca.model.ranked.RankedInfo
import com.half.wowsca.model.ranked.SeasonInfo
import com.half.wowsca.model.ranked.SeasonStats
import com.half.wowsca.ui.CAFragment
import com.half.wowsca.ui.UIUtils.setUpCard
import com.utilities.Utils.defaultDecimalFormatter
import com.utilities.Utils.oneDepthDecimalFormatter
import com.utilities.logging.Dlog.wtf
import org.greenrobot.eventbus.Subscribe
import java.text.DecimalFormat
import java.util.Collections

/**
 * Created by slai4 on 11/29/2015.
 */
class CaptainRankedFragment : CAFragment() {
    private var aSeasons: LinearLayout? = null

    private var aShips: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_ranked, container, false)
        bindView(view)
        return view
    }

    private fun bindView(view: View) {
        aSeasons = view.findViewById(R.id.ranked_container)
        aShips = view.findViewById(R.id.ranked_ship_container)

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

        if ((captain != null) && (captain.rankedSeasons != null) && (captain.ships != null)) {
            wtf("Ranked", "seasons = " + captain.rankedSeasons)
            refreshing(false)
            createSeasonList(aSeasons, captain.rankedSeasons, captain.ships)
        } else {
            aSeasons!!.removeAllViews()
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.list_season, aSeasons, false)

            val aHas: View = view.findViewById(R.id.list_season_has_info)
            val aNo: View = view.findViewById(R.id.list_season_no_info)
            val tvNoInfo: TextView = view.findViewById(R.id.list_season_no_info_text)
            aNo.visibility = View.VISIBLE
            aHas.visibility = View.GONE
            tvNoInfo.text = getString(R.string.search_no_results)
            setUpCard(view, R.id.list_season_no_info_card)

            aSeasons!!.addView(view)
        }
    }

    private fun createSeasonList(
        container: LinearLayout?,
        seasons: List<RankedInfo>,
        ships: List<Ship>?
    ) {
        container!!.removeAllViews()

        Collections.sort(seasons, object : Comparator<RankedInfo> {
            override fun compare(lhs: RankedInfo, rhs: RankedInfo): Int {
                if (lhs.seasonInt != null && rhs.seasonInt != null) return rhs.seasonInt
                    .compareTo(lhs.seasonInt)
                else return rhs.seasonNum.compareTo(lhs.seasonNum, ignoreCase = true)
            }
        })
        val shipMap: MutableMap<String, MutableList<Ship>> = HashMap()
        val seasonMap: MutableMap<String, SeasonStats> = HashMap()
        for (info: RankedInfo in seasons) {
            shipMap.put(info.seasonNum, ArrayList())
        }

        for (s: Ship in ships!!) {
            if (s.rankedInfo != null) {
                for (info: SeasonInfo in s.rankedInfo) {
                    if (info.solo != null) {
                        var seasonShips: MutableList<Ship>? = shipMap.get(info.seasonNum)
                        if (seasonShips == null) {
                            seasonShips = ArrayList()
                            shipMap.put(info.seasonNum, seasonShips)
                        }
                        seasonShips.add(s)
                        seasonMap.put(info.seasonNum + s.shipId, info.solo)
                    }
                }
            }
        }

        for (info: RankedInfo in seasons) {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.list_season, aSeasons, false)

            val aHas: View = view.findViewById(R.id.list_season_has_info)
            val aNo: View = view.findViewById(R.id.list_season_no_info)

            val tvNoInfo: TextView = view.findViewById(R.id.list_season_no_info_text)

            val llStars: LinearLayout = view.findViewById(R.id.list_season_star_amount)

            val tvRank: TextView = view.findViewById(R.id.list_season_rank)
            val tvMaxRank: TextView = view.findViewById(R.id.list_season_max_rank)
            val tvTitle: TextView = view.findViewById(R.id.list_season_title)

            val tvWinRate: TextView = view.findViewById(R.id.list_season_winrate)
            val tvSurvivalRate: TextView = view.findViewById(R.id.list_season_survival_rate)

            val tvBattles: TextView = view.findViewById(R.id.list_season_battles)
            val tvAvgDmg: TextView = view.findViewById(R.id.list_season_avg_dmg)
            val tvAvgKills: TextView = view.findViewById(R.id.list_season_avg_kills)
            val tvAvgCaps: TextView = view.findViewById(R.id.list_season_avg_caps)
            val tvAvgResets: TextView = view.findViewById(R.id.list_season_avg_resets)
            val tvAvgPlanes: TextView = view.findViewById(R.id.list_season_avg_planes)
            val tvAvgXP: TextView = view.findViewById(R.id.list_season_avg_xp)

            val tvBatteryMain: TextView = view.findViewById(R.id.list_season_battery_kills_main)
            val tvBatteryAircraft: TextView =
                view.findViewById(R.id.list_season_battery_kills_aircraft)
            val tvBatteryTorps: TextView = view.findViewById(R.id.list_season_battery_kills_torps)
            val tvBatteryOther: TextView = view.findViewById(R.id.list_season_battery_kills_other)

            val tvTopDamage: TextView = view.findViewById(R.id.list_season_top_damage)
            val tvTopExp: TextView = view.findViewById(R.id.list_season_top_exp)

            val aShips: View = view.findViewById(R.id.list_season_ships_area)
            val aShipsTop: View = view.findViewById(R.id.list_season_ships_top_area)
            val llShips: LinearLayout = view.findViewById(R.id.list_season_ships_container)
            val ivShipsArea: ImageView = view.findViewById(R.id.list_season_ships_image)

            aShips.tag = ivShipsArea
            aShipsTop.tag = aShips
            if (ivShipsArea.visibility == View.VISIBLE) aShipsTop.setOnClickListener(object :
                View.OnClickListener {
                override fun onClick(v: View) {
                    val ships: View = v.tag as View
                    val iv: ImageView = ships.tag as ImageView
                    if (ships.visibility == View.VISIBLE) {
                        iv.setImageResource(R.drawable.ic_expand)
                        ships.visibility = View.GONE
                    } else {
                        iv.setImageResource(R.drawable.ic_collapse)
                        ships.visibility = View.VISIBLE
                    }
                }
            })

            if (info.solo != null) {
                aHas.visibility = View.VISIBLE
                aNo.visibility = View.GONE

                setUpCard(view, R.id.list_season_info_card)

                tvRank.text = info.rank.toString() + ""

                tvMaxRank.text = " / " + info.startRank

                llStars.removeAllViews()
                for (i in 0 until info.stars) {
                    val iv: ImageView = ImageView(context)
                    val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(30, 30)
                    iv.layoutParams = params
                    iv.setImageResource(R.drawable.ic_star)
                    iv.colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.premium_shade
                        ), PorterDuff.Mode.MULTIPLY
                    )
                    llStars.addView(iv)
                }

                tvTitle.text = getString(R.string.ranked_season) + " " + info.seasonNum


                val stats: SeasonStats = info.solo
                val battles: Float = stats.battles.toFloat()
                if (battles > 0) {
                    val winrate: Float = stats.wins / battles * 100
                    val survival: Float = stats.survived / battles * 100
                    val avgDmg: Float = stats.damage / battles
                    val avgCaps: Float = stats.capPts / battles
                    val avgResets: Float = stats.drpCapPts / battles
                    val avgKills: Float = stats.frags / battles
                    val avgPlanes: Float = stats.planesKilled / battles
                    val avgXP: Float = stats.xp / battles

                    val format: DecimalFormat = DecimalFormat("###,###,###")
                    tvTopDamage.text = format.format(stats.maxDamage.toLong())
                    tvTopExp.text = stats.maxXP.toString() + ""

                    tvBattles.text = (battles.toInt()).toString() + ""

                    tvWinRate.text = defaultDecimalFormatter.format(winrate.toDouble()) + "%"
                    tvSurvivalRate.text = defaultDecimalFormatter.format(survival.toDouble()) + "%"

                    tvAvgDmg.text = oneDepthDecimalFormatter.format(avgDmg.toDouble())
                    tvAvgKills.text = oneDepthDecimalFormatter.format(avgKills.toDouble())
                    tvAvgCaps.text = oneDepthDecimalFormatter.format(avgCaps.toDouble())
                    tvAvgResets.text = oneDepthDecimalFormatter.format(avgResets.toDouble())
                    tvAvgPlanes.text = oneDepthDecimalFormatter.format(avgPlanes.toDouble())
                    tvAvgXP.text = oneDepthDecimalFormatter.format(avgXP.toDouble())

                    tvBatteryMain.text = createBatteryString(stats.main)
                    tvBatteryTorps.text = createBatteryString(stats.torps)
                    tvBatteryAircraft.text = createBatteryString(stats.aircraft)
                    val otherTotal: Int =
                        stats.frags - stats.main.frags - stats.aircraft.frags - stats.torps.frags
                    tvBatteryOther.text = otherTotal.toString() + ""

                    if (ships != null) {
                        val seasonsShips: List<Ship> = (shipMap.get(info.seasonNum))!!
                        val seasonName: String = info.seasonNum
                        Collections.sort<Ship>(seasonsShips, object : Comparator<Ship> {
                            override fun compare(lhs: Ship, rhs: Ship): Int {
                                val id: String = seasonName + lhs.shipId
                                val shipStats: SeasonStats? = seasonMap.get(id)
                                val id2: String = seasonName + rhs.shipId
                                val shipStats2: SeasonStats? = seasonMap.get(id2)
                                return shipStats2!!.battles - shipStats!!.battles
                            }
                        })
                        if (seasons.size > 0) {
                            aShipsTop.visibility = View.VISIBLE
                            if (ivShipsArea.visibility == View.GONE) {
                                aShips.visibility = View.VISIBLE
                            } else {
                                aShips.visibility = View.GONE
                            }
                            llShips.removeAllViews()

                            val shipViewTitle: View = LayoutInflater.from(context)
                                .inflate(R.layout.list_ranked_ships_title, llShips, false)
                            llShips.addView(shipViewTitle)

                            for (s: Ship in seasonsShips) {
                                val id: String = info.seasonNum + s.shipId
                                val shipStats: SeasonStats? = seasonMap.get(id)
                                val shipInfo: ShipInfo? =
                                    infoManager!!.getShipInfo(requireContext()).get(s.shipId)
                                if (shipInfo != null && shipStats!!.battles > 0) {
                                    val shipView: View = LayoutInflater.from(context)
                                        .inflate(R.layout.list_ranked_ships, llShips, false)
                                    val title: TextView =
                                        shipView.findViewById(R.id.list_ranked_ships_title)
                                    val one: TextView =
                                        shipView.findViewById(R.id.list_ranked_ships_1)
                                    val two: TextView =
                                        shipView.findViewById(R.id.list_ranked_ships_2)
                                    val three: TextView =
                                        shipView.findViewById(R.id.list_ranked_ships_3)

                                    title.text = shipInfo.name
                                    one.text = shipStats.battles.toString() + ""

                                    val formatter: DecimalFormat = oneDepthDecimalFormatter

                                    two.text = formatter.format(
                                        ((shipStats.wins / shipStats.battles
                                            .toFloat()) * 100).toDouble()
                                    ) + "%"
                                    three.text = formatter.format(
                                        ((shipStats.survived / shipStats.battles
                                            .toFloat()) * 100).toDouble()
                                    ) + "%"

                                    shipView.isClickable = true
                                    shipView.tag = s.shipId
                                    shipView.setOnClickListener(object : View.OnClickListener {
                                        override fun onClick(v: View) {
                                            val s: Long? = v.tag as Long?
                                            if (s != null) {
                                                eventBus.post(ShipClickedEvent(s))
                                            }
                                        }
                                    })

                                    llShips.addView(shipView)
                                }
                            }
                        } else {
                            aShipsTop.visibility = View.GONE
                            aShips.visibility = View.GONE
                        }
                    }
                }
            } else {
                aNo.visibility = View.VISIBLE
                aHas.visibility = View.GONE
                tvNoInfo.text = getString(R.string.no_data_for_season) + info.seasonNum
                setUpCard(view, R.id.list_season_no_info_card)
            }
            container.addView(view)
        }
    }

    private fun createShipList() {
        aShips!!.removeAllViews()
    }

    @Subscribe
    fun onReceive(event: CaptainReceivedEvent?) {
        initView()
    }

    @Subscribe
    fun onRefresh(event: RefreshEvent?) {
        //clear out elements
        refreshing(true)
        aSeasons!!.removeAllViews()
    }

    @Subscribe
    fun onProgressEvent(event: ProgressEvent) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout!!.isRefreshing = event.isRefreshing
        }
    }

    companion object {
        fun createBatteryString(stats: BatteryStats): String {
            val sb: StringBuilder = StringBuilder()
            sb.append(stats.frags)
            if (stats.shots > 0) {
                sb.append("\n")
                sb.append(oneDepthDecimalFormatter.format(((stats.hits.toFloat() / stats.shots.toFloat()) * 100).toDouble()) + "%")
            }
            return sb.toString()
        }
    }
}
