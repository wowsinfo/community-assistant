package com.half.wowsca.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.R
import com.half.wowsca.model.UpgradeClickEvent
import com.half.wowsca.model.encyclopedia.items.EquipmentInfo
import com.half.wowsca.ui.adapter.UpgradesAdapter.UpgradesHolder
import com.squareup.picasso.Picasso

/**
 * Created by slai4 on 4/26/2016.
 */
class UpgradesAdapter(private val items: List<EquipmentInfo>?, private val ctx: Context) :
    RecyclerView.Adapter<UpgradesHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpgradesHolder {
        val convertView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_captain_skill, parent, false)
        val holder = UpgradesHolder(convertView)
        return holder
    }

    override fun onBindViewHolder(holder: UpgradesHolder, position: Int) {
        val exteriorItem = items!![position]
        holder.id = exteriorItem.id
        holder.tvName.text = exteriorItem.name
        Picasso.get().load(exteriorItem.image).error(R.drawable.ic_missing_image)
            .into(holder.ivIcon)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    class UpgradesHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var tvName: TextView = itemView.findViewById(R.id.list_captain_skill_text)
        var ivIcon: ImageView = itemView.findViewById(R.id.list_captain_skill_image)

        var id: Long = 0

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            eventBus.post(UpgradeClickEvent(id))
        }
    }
}
