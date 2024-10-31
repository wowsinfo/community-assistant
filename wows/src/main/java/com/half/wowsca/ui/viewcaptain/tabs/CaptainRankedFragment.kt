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
class CaptainRankedFragment() : CAFragment() {
    private var aSeasons: LinearLayout? = null

    private var aShips: LinearLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            captain = (getActivity() as ICaptain?)!!.getCaptain(getContext())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if ((captain != null) && (captain.getRankedSeasons() != null) && (captain.getShips() != null)) {
            wtf("Ranked", "seasons = " + captain.getRankedSeasons())
            refreshing(false)
            createSeasonList(aSeasons, captain.getRankedSeasons(), captain.getShips())
        } else {
            aSeasons!!.removeAllViews()
            val view: View =
                LayoutInflater.from(getContext()).inflate(R.layout.list_season, aSeasons, false)

            val aHas: View = view.findViewById(R.id.list_season_has_info)
            val aNo: View = view.findViewById(R.id.list_season_no_info)
            val tvNoInfo: TextView = view.findViewById(R.id.list_season_no_info_text)
            aNo.setVisibility(View.VISIBLE)
            aHas.setVisibility(View.GONE)
            tvNoInfo.setText(getString(R.string.search_no_results))
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
                if (lhs.getSeasonInt() != null && rhs.getSeasonInt() != null) return rhs.getSeasonInt()
                    .compareTo(lhs.getSeasonInt())
                else return rhs.getSeasonNum().compareTo(lhs.getSeasonNum(), ignoreCase = true)
            }
        })
        val shipMap: MutableMap<String, MutableList<Ship>> = HashMap()
        val seasonMap: MutableMap<String, SeasonStats> = HashMap()
        for (info: RankedInfo in seasons) {
            shipMap.put(info.getSeasonNum(), ArrayList())
        }

        for (s: Ship in ships!!) {
            if (s.getRankedInfo() != null) {
                for (info: SeasonInfo in s.getRankedInfo()) {
                    if (info.getSolo() != null) {
                        var seasonShips: MutableList<Ship>? = shipMap.get(info.getSeasonNum())
                        if (seasonShips == null) {
                            seasonShips = ArrayList()
                            shipMap.put(info.getSeasonNum(), seasonShips)
                        }
                        seasonShips.add(s)
                        seasonMap.put(info.getSeasonNum() + s.getShipId(), info.getSolo())
                    }
                }
            }
        }

        for (info: RankedInfo in seasons) {
            val view: View =
                LayoutInflater.from(getContext()).inflate(R.layout.list_season, aSeasons, false)

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

            aShips.setTag(ivShipsArea)
            aShipsTop.setTag(aShips)
            if (ivShipsArea.getVisibility() == View.VISIBLE) aShipsTop.setOnClickListener(object :
                View.OnClickListener {
                override fun onClick(v: View) {
                    val ships: View = v.getTag() as View
                    val iv: ImageView = ships.getTag() as ImageView
                    if (ships.getVisibility() == View.VISIBLE) {
                        iv.setImageResource(R.drawable.ic_expand)
                        ships.setVisibility(View.GONE)
                    } else {
                        iv.setImageResource(R.drawable.ic_collapse)
                        ships.setVisibility(View.VISIBLE)
                    }
                }
            })

            if (info.getSolo() != null) {
                aHas.setVisibility(View.VISIBLE)
                aNo.setVisibility(View.GONE)

                setUpCard(view, R.id.list_season_info_card)

                tvRank.setText(info.getRank().toString() + "")

                tvMaxRank.setText(" / " + info.getStartRank())

                llStars.removeAllViews()
                for (i in 0 until info.getStars()) {
                    val iv: ImageView = ImageView(getContext())
                    val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(30, 30)
                    iv.setLayoutParams(params)
                    iv.setImageResource(R.drawable.ic_star)
                    iv.setColorFilter(
                        PorterDuffColorFilter(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.premium_shade
                            ), PorterDuff.Mode.MULTIPLY
                        )
                    )
                    llStars.addView(iv)
                }

                tvTitle.setText(getString(R.string.ranked_season) + " " + info.getSeasonNum())


                val stats: SeasonStats = info.getSolo()
                val battles: Float = stats.getBattles().toFloat()
                if (battles > 0) {
                    val winrate: Float = stats.getWins() / battles * 100
                    val survival: Float = stats.getSurvived() / battles * 100
                    val avgDmg: Float = stats.getDamage() / battles
                    val avgCaps: Float = stats.getCapPts() / battles
                    val avgResets: Float = stats.getDrpCapPts() / battles
                    val avgKills: Float = stats.getFrags() / battles
                    val avgPlanes: Float = stats.getPlanesKilled() / battles
                    val avgXP: Float = stats.getXp() / battles

                    val format: DecimalFormat = DecimalFormat("###,###,###")
                    tvTopDamage.setText(format.format(stats.getMaxDamage().toLong()))
                    tvTopExp.setText(stats.getMaxXP().toString() + "")

                    tvBattles.setText((battles.toInt()).toString() + "")

                    tvWinRate.setText(defaultDecimalFormatter.format(winrate.toDouble()) + "%")
                    tvSurvivalRate.setText(defaultDecimalFormatter.format(survival.toDouble()) + "%")

                    tvAvgDmg.setText(oneDepthDecimalFormatter.format(avgDmg.toDouble()))
                    tvAvgKills.setText(oneDepthDecimalFormatter.format(avgKills.toDouble()))
                    tvAvgCaps.setText(oneDepthDecimalFormatter.format(avgCaps.toDouble()))
                    tvAvgResets.setText(oneDepthDecimalFormatter.format(avgResets.toDouble()))
                    tvAvgPlanes.setText(oneDepthDecimalFormatter.format(avgPlanes.toDouble()))
                    tvAvgXP.setText(oneDepthDecimalFormatter.format(avgXP.toDouble()))

                    tvBatteryMain.setText(createBatteryString(stats.getMain()))
                    tvBatteryTorps.setText(createBatteryString(stats.getTorps()))
                    tvBatteryAircraft.setText(createBatteryString(stats.getAircraft()))
                    val otherTotal: Int =
                        stats.getFrags() - stats.getMain().frags - stats.getAircraft().frags - stats.getTorps().frags
                    tvBatteryOther.setText(otherTotal.toString() + "")

                    if (ships != null) {
                        val seasonsShips: List<Ship> = (shipMap.get(info.getSeasonNum()))!!
                        val seasonName: String = info.getSeasonNum()
                        Collections.sort<Ship>(seasonsShips, object : Comparator<Ship> {
                            override fun compare(lhs: Ship, rhs: Ship): Int {
                                val id: String = seasonName + lhs.getShipId()
                                val shipStats: SeasonStats? = seasonMap.get(id)
                                val id2: String = seasonName + rhs.getShipId()
                                val shipStats2: SeasonStats? = seasonMap.get(id2)
                                return shipStats2!!.getBattles() - shipStats!!.getBattles()
                            }
                        })
                        if (seasons.size > 0) {
                            aShipsTop.setVisibility(View.VISIBLE)
                            if (ivShipsArea.getVisibility() == View.GONE) {
                                aShips.setVisibility(View.VISIBLE)
                            } else {
                                aShips.setVisibility(View.GONE)
                            }
                            llShips.removeAllViews()

                            val shipViewTitle: View = LayoutInflater.from(getContext())
                                .inflate(R.layout.list_ranked_ships_title, llShips, false)
                            llShips.addView(shipViewTitle)

                            for (s: Ship in seasonsShips) {
                                val id: String = info.getSeasonNum() + s.getShipId()
                                val shipStats: SeasonStats? = seasonMap.get(id)
                                val shipInfo: ShipInfo? =
                                    infoManager!!.getShipInfo(requireContext()).get(s.getShipId())
                                if (shipInfo != null && shipStats!!.getBattles() > 0) {
                                    val shipView: View = LayoutInflater.from(getContext())
                                        .inflate(R.layout.list_ranked_ships, llShips, false)
                                    val title: TextView =
                                        shipView.findViewById(R.id.list_ranked_ships_title)
                                    val one: TextView =
                                        shipView.findViewById(R.id.list_ranked_ships_1)
                                    val two: TextView =
                                        shipView.findViewById(R.id.list_ranked_ships_2)
                                    val three: TextView =
                                        shipView.findViewById(R.id.list_ranked_ships_3)

                                    title.setText(shipInfo.getName())
                                    one.setText(shipStats.getBattles().toString() + "")

                                    val formatter: DecimalFormat = oneDepthDecimalFormatter

                                    two.setText(
                                        formatter.format(
                                            ((shipStats.getWins() / shipStats.getBattles()
                                                .toFloat()) * 100).toDouble()
                                        ) + "%"
                                    )
                                    three.setText(
                                        formatter.format(
                                            ((shipStats.getSurvived() / shipStats.getBattles()
                                                .toFloat()) * 100).toDouble()
                                        ) + "%"
                                    )

                                    shipView.setClickable(true)
                                    shipView.setTag(s.getShipId())
                                    shipView.setOnClickListener(object : View.OnClickListener {
                                        override fun onClick(v: View) {
                                            val s: Long? = v.getTag() as Long?
                                            if (s != null) {
                                                eventBus.post(ShipClickedEvent(s))
                                            }
                                        }
                                    })

                                    llShips.addView(shipView)
                                }
                            }
                        } else {
                            aShipsTop.setVisibility(View.GONE)
                            aShips.setVisibility(View.GONE)
                        }
                    }
                }
            } else {
                aNo.setVisibility(View.VISIBLE)
                aHas.setVisibility(View.GONE)
                tvNoInfo.setText(getString(R.string.no_data_for_season) + info.getSeasonNum())
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
            mSwipeRefreshLayout!!.setRefreshing(event.isRefreshing)
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
