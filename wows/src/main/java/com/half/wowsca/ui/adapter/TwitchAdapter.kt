package com.half.wowsca.ui.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.R
import com.half.wowsca.model.TwitchObj
import com.half.wowsca.model.enums.TwitchStatus
import com.half.wowsca.ui.UIUtils.setUpCard
import com.half.wowsca.ui.adapter.TwitchAdapter.TwitchHolder
import com.squareup.picasso.Picasso
import com.utilities.logging.Dlog.d
import java.util.Collections

/**
 * Created by slai4 on 12/3/2015.
 */
class TwitchAdapter : RecyclerView.Adapter<TwitchHolder>() {
    var twitchObjs: List<TwitchObj>? = null

    var ctx: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TwitchHolder {
        val convertView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_twitch, parent, false)

        val holder = TwitchHolder(convertView)

        return holder
    }

    override fun onBindViewHolder(holder: TwitchHolder, position: Int) {
        val obj = twitchObjs!![position]
        setUpCard(holder.view, R.id.twitch_area)
        holder.title.text = obj.name
        holder.url = obj.url
        holder.name = obj.name

        if (obj.isLive == TwitchStatus.LIVE) {
            holder.alert.text = ctx!!.getString(R.string.live)
        } else if (obj.isLive == TwitchStatus.OFFLINE) {
            holder.alert.text = ctx!!.getString(R.string.offline)
        } else {
            holder.alert.text = ""
        }

        if (!TextUtils.isEmpty(obj.streamName)) holder.status.text = obj.streamName

        if (!TextUtils.isEmpty(obj.logo)) {
            Picasso.get().load(obj.logo).resize(800, 600).centerInside()
                .error(R.drawable.ic_missing_image).into(holder.logo)
        }

        if (!TextUtils.isEmpty(obj.thumbnail)) {
            Picasso.get().load(obj.thumbnail).error(R.drawable.ic_missing_image)
                .into(holder.background)
        }
        //check for youtube only
        if (obj.name == "Jammin411") {
            holder.logo.setImageResource(R.drawable.ic_twitch_wowsreplay_icon)
            holder.background.setImageResource(R.drawable.ic_twitch_wowsreplay)
            holder.url = "http://wowreplays.com/"
            holder.status.text = ""
        } else if (obj.name == "crysantos" || obj.name == "kamisamurai") {
            holder.youtube.visibility = View.GONE
        } else if (obj.name == "notser") {
            holder.twitter.visibility = View.GONE
        } else {
            holder.youtube.visibility = View.VISIBLE
            holder.twitter.visibility = View.VISIBLE
        }
    }


    override fun getItemCount(): Int {
        return try {
            twitchObjs!!.size
        } catch (e: Exception) {
            0
        }
    }

    fun sort() {
        Collections.sort(twitchObjs) { lhs, rhs -> lhs.name.compareTo(rhs.name, ignoreCase = true) }
        Collections.sort(twitchObjs) { lhs, rhs -> lhs.isLive.order - rhs.isLive.order }
        notifyDataSetChanged()
    }

    class TwitchHolder(var view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var title: TextView
        var logo: ImageView

        var background: ImageView

        var status: TextView

        var youtube: View
        var twitter: View

        var name: String? = null
        var url: String? = null

        var alert: TextView

        init {
            val click = view.findViewById<View>(R.id.twitch_view)
            click.setOnClickListener(this)
            title = itemView.findViewById(R.id.twitch_title)
            logo = itemView.findViewById(R.id.twitch_logo)
            background = itemView.findViewById(R.id.twitch_background)
            status = itemView.findViewById(R.id.twitch_status)
            alert = itemView.findViewById(R.id.twitch_live)
            youtube = itemView.findViewById(R.id.twitch_youtube)
            youtube.setOnClickListener {
                val sb = StringBuilder()
                sb.append("https://www.youtube.com/")
                when (name) {
                    "iChaseGaming" -> sb.append("user/ichasegaming")
                    "Mejash" -> sb.append("channel/UCAZ25zYeNWAR-LLXVbq4WTg")
                    "dontrevivemebro" -> sb.append("user/ZoupGaming")
                    "Aerroon" -> sb.append("channel/UCLOQoJ6G4D04d05fxjfPHPQ")
                    "BaronVonGamez" -> sb.append("user/BaronVonGamez")
                    "wda_punisher" -> sb.append("user/WDAxodus")
                    "wargaming" -> sb.append("user/worldofwarshipsCOM")
                    "iEarlGrey" -> sb.append("channel/UCtMGV3SHfVfiAt_w8lnmI8g")
                    "Flamuu" -> sb.append("user/cheesec4t")
                    "clydethamonkey" -> sb.append("user/SillyScandinavians")
                    "Jammin411" -> sb.append("c/Jammin411")
                    "notser" -> sb.append("user/MrNotser")
                }
                eventBus.post(sb.toString())
            }
            twitter = itemView.findViewById(R.id.twitch_twitter)
            twitter.setOnClickListener {
                val sb = StringBuilder()
                sb.append("https://twitter.com/")
                when (name) {
                    "iChaseGaming" -> sb.append("ichasegaming")
                    "Mejash" -> sb.append("mejashtv")
                    "dontrevivemebro" -> sb.append("ZoupGaming")
                    "Aerroon" -> sb.append("Aerroon")
                    "BaronVonGamez" -> sb.append("BaronVonGamez")
                    "wda_punisher" -> sb.append("WDA_Punisher")
                    "wargaming" -> sb.append("WorldofWarships")
                    "iEarlGrey" -> sb.append("iearlgreytv")
                    "Flamuu" -> sb.append("flamuchz")
                    "clydethamonkey" -> sb.append("Clydeypoo")
                    "Jammin411" -> sb.append("Jammin411")
                    "crysantos" -> sb.append("CrysantosTV")
                    "kamisamurai" -> sb.append("KamiSamuraiTV")
                }
                eventBus.post(sb.toString())
            }
        }

        override fun onClick(v: View) {
            d("Twitch", "url = $url")
            eventBus.post(url)
        }
    }
}
