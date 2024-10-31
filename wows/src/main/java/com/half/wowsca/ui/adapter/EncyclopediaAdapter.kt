package com.half.wowsca.ui.adapter

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.R
import com.half.wowsca.managers.CompareManager.addShipID
import com.half.wowsca.managers.CompareManager.getSHIPS
import com.half.wowsca.managers.CompareManager.removeShipID
import com.half.wowsca.model.ShipCompareEvent
import com.half.wowsca.model.encyclopedia.items.ShipInfo
import com.half.wowsca.ui.UIUtils.setUpCard
import com.half.wowsca.ui.encyclopedia.ShipProfileActivity
import com.squareup.picasso.Picasso
import com.utilities.logging.Dlog.d
import java.util.Locale

/**
 * Created by slai4 on 10/31/2015.
 */
class EncyclopediaAdapter(ships: List<ShipInfo?>, context: Context) :
    RecyclerView.Adapter<EncyclopediaAdapter.ShipViewHolder>() {
    private val backupShips: ArrayList<ShipInfo>
    private val ctx: Context
    private var ships: List<ShipInfo>?


    init {
        this.ships = ships.filterNotNull()
        backupShips = ships as ArrayList<ShipInfo>
        this.ctx = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShipViewHolder {
        val convertView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_encyclopedia_ship, parent, false)
        setUpCard(convertView, 0)
        val holder = ShipViewHolder(convertView)
        return holder
    }

    override fun onBindViewHolder(holder: ShipViewHolder, position: Int) {
        val info = ships!![position]
        holder.shipId = info.shipId

        Picasso.get().load(info.image).error(R.drawable.ic_missing_image).into(holder.img)
        //        String nation = info.getNation();
//        if (nation.equals("ussr")) { // TODO translations
//            nation = "Russia";
//        } else if (nation.equals("germany")) {
//            nation = "Germany";
//        } else if (nation.equals("usa")) {
//            nation = "USA";
//        } else if (nation.equals("poland")) {
//            nation = "Poland";
//        } else if (nation.equals("japan")) {
//            nation = "Japan";
//        } else if (nation.equals("uk")) {
//            nation = "UK";
//        }
        if (getSHIPS()!!.contains(info.shipId)) {
            holder.area.setBackgroundResource(R.drawable.compare_top_grid)
        } else {
            val outValue = TypedValue()
            ctx.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            holder.area.setBackgroundResource(outValue.resourceId)
        }
        holder.name.text = info.name
        holder.shipName = info.name
    }

    override fun getItemCount(): Int {
        return if (ships != null) ships!!.size
        else 0
    }

    fun filter(s: CharSequence, nation: Int, tier: Int) {
        var tier = tier
        d("Filter", "s = $s nation = $nation tier = $tier")
        var numberOfChecks = 0
        if (!TextUtils.isEmpty(s)) numberOfChecks++

        if (nation >= 0) numberOfChecks++

        if (tier >= 0) numberOfChecks++
        d("Filter", "checks = $numberOfChecks")
        if (numberOfChecks > 0) {
            val filteredList: MutableList<ShipInfo> = ArrayList()
            val local = Locale.getDefault()
            val contraint = s.toString().lowercase(local)
            val nationList = arrayOf(
                "usa",
                "uk",
                "ussr",
                "japan",
                "germany",
                "pan_asia",
                "poland",
                "france",
                "commonwealth"
            ) // update this when updating R.id.search_nation
            var nationStr: String? = null
            if (nation >= 0) nationStr = nationList[nation]
            // push tier up by one because of the position being off by one
            if (tier >= 0) tier = tier + 1
            for (item in backupShips) {
                var accepted = false
                accepted = (item.name.lowercase(local).contains(contraint)
                        && (nation == EMPTY_FILTER || item.nation.lowercase(local).contains(
                    nationStr!!
                ))
                        && (tier == EMPTY_FILTER || item.tier == tier))

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

    class ShipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img: ImageView = itemView.findViewById(R.id.list_encyclopedia_ship_image)
        var name: TextView = itemView.findViewById(R.id.list_encyclopedia_ship_name)
        var area: View = itemView.findViewById(R.id.list_encyclopedia_area)

        var shipId: Long = 0
        var shipName: String? = null

        init {
            area.setOnClickListener { //send to encyclopedia page
                val i = Intent(img.context, ShipProfileActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.putExtra(ShipProfileActivity.SHIP_ID, shipId)
                ShipProfileActivity.MODULE_LIST = null
                img.context.startActivity(i)
            }
            area.setOnLongClickListener { //go to activity
                if (!getSHIPS()!!.contains(shipId)) addShipID(shipId)
                else removeShipID(shipId)
                eventBus.post(ShipCompareEvent(shipId))
                true
            }
        }
    }

    companion object {
        const val EMPTY_FILTER: Int = -1
    }
}