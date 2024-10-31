package com.half.wowsca.ui.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.half.wowsca.CAApp
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.R
import com.half.wowsca.model.Ship
import com.half.wowsca.model.ShipClickedEvent
import com.half.wowsca.model.ShipCompare
import com.half.wowsca.model.SortingDoneEvent
import com.half.wowsca.model.saveobjects.SavedShips
import com.half.wowsca.ui.UIUtils.getNationText
import com.half.wowsca.ui.UIUtils.setShipImage
import com.half.wowsca.ui.UIUtils.setUpCard
import com.utilities.Utils.defaultDecimalFormatter
import com.utilities.logging.Dlog.wtf
import java.util.Collections
import java.util.Locale

/**
 * Created by slai4 on 10/1/2015.
 */
class ShipsAdapter(ships: List<Ship>, ctx: Context) :
    RecyclerView.Adapter<ShipsAdapter.ShipViewHolder>() {
    private val backupShips: ArrayList<Ship>
    private val ctx: Context
    private var ships: List<Ship>?
    var savedShips: SavedShips? = null

    init {
        this.ships = ships
        backupShips = ships as ArrayList<Ship>
        this.ctx = ctx
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShipViewHolder {
        val convertView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_ship, parent, false)
        val holder = ShipViewHolder(convertView)
        return holder
    }

    override fun onBindViewHolder(holder: ShipViewHolder, position: Int) {
        val s = ships!![position]
        val info = infoManager!!.getShipInfo(ctx)[s.shipId]
        val battles = s.battles.toFloat()
        setUpCard(holder.view, R.id.list_ship_area)
        var nameText: String? = ctx.getString(R.string.unknown)
        holder.ship = s.shipId
        holder.pos = position

        if (info != null) {
            nameText = info.name
            setShipImage(holder.icon, info)
            val nation = getNationText(ctx, info.nation)

            holder.nationTier.text = nation + " - " + info.tier
            if (info.isPremium) {
                holder.tvName.setTextColor(ContextCompat.getColor(ctx, R.color.premium_shade))
            } else {
                holder.tvName.setTextColor(ContextCompat.getColor(ctx, R.color.white))
            }
        }
        holder.aCARating.visibility = View.VISIBLE
        holder.tvName.text = nameText
        if (battles > 0) {
            holder.tvBattles.text = "" + (battles.toInt())
            val avgExp = s.totalXP / battles
            holder.tvAverageExp.text = avgExp.toInt().toString() + ""
            val wr = (s.wins / battles) * 100.0f
            holder.tvWinRate.text = defaultDecimalFormatter.format(wr.toDouble()) + "%"
            var kdBattles = battles.toInt()
            if (kdBattles != s.survivedBattles) {
                kdBattles = (battles - s.survivedBattles).toInt()
            }
            val kd = s.frags.toFloat() / kdBattles
            holder.tvAverageKills.text = defaultDecimalFormatter.format(kd.toDouble())
            val avgDamage = (s.totalDamage / battles).toInt()
            holder.tvAverageDamage.text = "" + avgDamage
            holder.tvCARating.text = "" + Math.round(s.caRating)
        } else {
            holder.tvBattles.text = "0"
            holder.tvAverageExp.text = "0"
            holder.tvWinRate.text = "0"
            holder.tvAverageKills.text = "0"
            holder.tvAverageDamage.text = "0"
        }
    }

    override fun getItemCount(): Int {
        return if (ships != null) ships!!.size
        else 0
    }

    fun sort(sortStr: String) {
        wtf("ShipsAdapter", "sort = $sortStr")
        val sortTypes = ctx.resources.getStringArray(R.array.ship_sorting)
        var i = 0
        while (i < sortTypes.size) {
            if (sortTypes[i] == sortStr) {
                break
            }
            i++
        }
        val compare = ShipCompare()

        //        StringBuilder sb = new StringBuilder();
//        for (Ship ship : ships) {
//            sb.append("[" + ship.getShipId()+ "],");
//        }
//        Dlog.d("Sort1", sb.toString());
        compare.shipsHolder = infoManager!!.getShipInfo(ctx)
        try {
            when (i) {
                0 -> Collections.sort(ships, compare.battlesComparator)
                1 -> Collections.sort(ships, compare.namesComparator)
                2 -> Collections.sort(ships, compare.averageExpComparator)
                3 -> Collections.sort(ships, compare.averageDamageComparator)
                4 -> Collections.sort(ships, compare.winRateComparator)
                5 -> Collections.sort(ships, compare.killsDeathComparator)
                6 -> Collections.sort(ships, compare.damageComparator)
                7 -> Collections.sort(ships, compare.killsComparator)
                8 -> Collections.sort(ships, compare.planeKillsComparator)
                9 -> Collections.sort(ships, compare.tierDescendingComparator)
                10 -> Collections.sort(ships, compare.tierAscendingComparator)
                11 -> Collections.sort(ships, compare.CARatingComparator)
                12 -> Collections.sort(ships, compare.accuracyComparator)
                13 -> Collections.sort(ships, compare.accuractTorpsComparator)
            }
            eventBus.post(SortingDoneEvent())

            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun filter(s: CharSequence) {
        if (!TextUtils.isEmpty(s)) {
            val filteredList: MutableList<Ship> = ArrayList()
            val local = Locale.getDefault()
            val contraint = s.toString().lowercase(local)
            val shipsHolder = infoManager!!.getShipInfo(ctx)
            var tierContraints: Int? = null
            try {
                tierContraints = contraint.toInt()
            } catch (e: NumberFormatException) {
            }
            if (tierContraints != null) {
                if (tierContraints > 10 || tierContraints < 1) {
                    tierContraints = null
                }
            }
            for (item in backupShips) {
                val info = shipsHolder[item.shipId]
                var accepted = false
                if (info != null) {
                    accepted = if (tierContraints == null) {
                        info.name.lowercase(local).contains(contraint)
                    } else {
                        info.tier == tierContraints
                    }
                }
                if (accepted) {
                    filteredList.add(item)
                }
            }
            this.ships = filteredList
            notifyDataSetChanged()
        } else {
            ships = backupShips
            notifyDataSetChanged()
        }
    }

    class ShipViewHolder(var view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var tvName: TextView = itemView.findViewById(R.id.snippet_ship_name)
        var nationTier: TextView = itemView.findViewById(R.id.snippet_ship_nation_tier)
        var tvBattles: TextView = itemView.findViewById(R.id.snippet_ship_battles)
        var tvWinRate: TextView = itemView.findViewById(R.id.snippet_ship_win_rate)
        var tvAverageExp: TextView = itemView.findViewById(R.id.snippet_ship_avg_exp)
        var tvAverageKills: TextView = itemView.findViewById(R.id.snippet_ship_avg_kills)
        var tvAverageDamage: TextView = itemView.findViewById(R.id.snippet_ship_avg_damage)
        var tvCARating: TextView = itemView.findViewById(R.id.snippet_ship_ca_rating)

        var aCARating: View = itemView.findViewById(R.id.snippet_ship_ca_rating_area)

        var icon: ImageView = itemView.findViewById(R.id.snippet_ship_icon)

        var ship: Long = 0
        var pos: Int = 0

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            eventBus.post(ShipClickedEvent(ship))
            CAApp.lastShipPos = pos
        }
    }
}
