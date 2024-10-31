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
import com.half.wowsca.model.FlagClickedEvent
import com.half.wowsca.model.encyclopedia.items.ExteriorItem
import com.half.wowsca.ui.adapter.FlagsAdapter.FlagsViewHolder
import com.squareup.picasso.Picasso

/**
 * Created by slai4 on 4/25/2016.
 */
class FlagsAdapter(private val exteriorItems: List<ExteriorItem>?, private val ctx: Context) :
    RecyclerView.Adapter<FlagsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlagsViewHolder {
        val convertView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_captain_skill, parent, false)
        val holder = FlagsViewHolder(convertView)
        return holder
    }

    override fun onBindViewHolder(holder: FlagsViewHolder, position: Int) {
        val exteriorItem = exteriorItems!![position]
        holder.id = exteriorItem.id
        holder.tvName.text = exteriorItem.name
        Picasso.get().load(exteriorItem.image).error(R.drawable.ic_missing_image)
            .into(holder.ivIcon)
    }

    override fun getItemCount(): Int {
        return exteriorItems?.size ?: 0
    }

    class FlagsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var tvName: TextView = itemView.findViewById(R.id.list_captain_skill_text)
        var ivIcon: ImageView = itemView.findViewById(R.id.list_captain_skill_image)

        var id: Long = 0

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            eventBus.post(FlagClickedEvent(id))
        }
    }
}
