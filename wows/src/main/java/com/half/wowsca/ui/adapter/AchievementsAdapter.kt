package com.half.wowsca.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.R
import com.half.wowsca.model.Achievement
import com.squareup.picasso.Picasso

/**
 * Created by slai4 on 9/23/2015.
 */
class AchievementsAdapter(context: Context?, resource: Int, objects: List<Achievement?>?) :
    ArrayAdapter<Achievement?>(
        context!!, resource, objects!!
    ) {
    private var savedAchievements: Map<String?, Int>? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView =
                LayoutInflater.from(context).inflate(R.layout.list_achievement, parent, false)
        }
        val achievement = getItem(position)
        val info = infoManager!!.getAchievements(context)[achievement!!.name]
        val iv = convertView!!.findViewById<ImageView>(R.id.list_achievement_icon)
        val tvNumber = convertView.findViewById<TextView>(R.id.list_achievement_text)
        val tvDiff = convertView.findViewById<TextView>(R.id.list_achievement_difference)

        if (info != null) {
            Picasso.get().load(info.image).error(R.drawable.ic_missing_image).into(iv)
        }
        if (achievement.number == 0) {
            iv.imageAlpha = 125
            tvNumber.text = ""
        } else {
            iv.imageAlpha = 255
            tvNumber.text = achievement.number.toString() + ""
        }

        if (savedAchievements != null) {
            val achi = savedAchievements!![achievement.name]
            if (achi != null) {
                val difference = achievement.number - achi
                tvDiff.text = "+$difference"
                tvDiff.setTextColor(ContextCompat.getColor(context, R.color.average_up))
                if (difference > 0) {
                    tvDiff.visibility = View.VISIBLE
                } else {
                    tvDiff.visibility = View.GONE
                }
            } else {
                tvDiff.visibility = View.GONE
            }
        } else {
            tvDiff.visibility = View.GONE
        }
        convertView.tag = achievement.name
        return convertView
    }

    fun setSavedAchievements(savedAchievements: Map<String?, Int>?) {
        this.savedAchievements = savedAchievements
    }
}