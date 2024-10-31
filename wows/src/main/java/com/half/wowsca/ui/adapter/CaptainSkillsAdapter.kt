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
import com.half.wowsca.model.CaptainSkillClickedEvent
import com.half.wowsca.model.encyclopedia.items.CaptainSkill
import com.half.wowsca.ui.adapter.CaptainSkillsAdapter.SkillsViewHolder
import com.squareup.picasso.Picasso

/**
 * Created by slai4 on 4/25/2016.
 */
class CaptainSkillsAdapter(private val skills: List<CaptainSkill>?, private val ctx: Context) :
    RecyclerView.Adapter<SkillsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillsViewHolder {
        val convertView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_captain_skill, parent, false)
        val holder = SkillsViewHolder(convertView)
        return holder
    }

    override fun onBindViewHolder(holder: SkillsViewHolder, position: Int) {
        val skill = skills!![position]
        holder.id = skill.id
        holder.tvName.text = skill.name
        Picasso.get().load(skill.image).error(R.drawable.ic_missing_image).into(holder.ivIcon)
    }

    override fun getItemCount(): Int {
        return skills?.size ?: 0
    }

    class SkillsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var tvName: TextView = itemView.findViewById(R.id.list_captain_skill_text)
        var ivIcon: ImageView = itemView.findViewById(R.id.list_captain_skill_image)

        var id: Long = 0

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            eventBus.post(CaptainSkillClickedEvent(id))
        }
    }
}
