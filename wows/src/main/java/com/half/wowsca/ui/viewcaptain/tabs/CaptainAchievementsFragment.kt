package com.half.wowsca.ui.viewcaptain.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.R
import com.half.wowsca.interfaces.ICaptain
import com.half.wowsca.managers.CaptainManager.createCapIdStr
import com.half.wowsca.managers.StorageManager.getPlayerAchievements
import com.half.wowsca.model.Achievement
import com.half.wowsca.model.Captain
import com.half.wowsca.model.CaptainReceivedEvent
import com.half.wowsca.model.ProgressEvent
import com.half.wowsca.model.RefreshEvent
import com.half.wowsca.ui.CAFragment
import com.half.wowsca.ui.adapter.AchievementsAdapter
import org.greenrobot.eventbus.Subscribe
import java.util.Collections

/**
 * Created by slai4 on 9/15/2015.
 */
class CaptainAchievementsFragment : CAFragment() {
    private var battleGrid: GridView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_captain_achievements, container, false)
        bindView(view)
        return view
    }

    private fun bindView(view: View) {
        battleGrid = view.findViewById(R.id.achievement_battle_grid)
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
        if (captain != null && captain.achievements != null) {
            refreshing(false)
            val achievementsHolder = infoManager!!.getAchievements(requireContext())
            val achs: MutableList<Achievement> = ArrayList()
            val captainAchievements: MutableMap<String?, Int> = HashMap()
            for (achievement in captain.achievements) {
                captainAchievements[achievement.name] = achievement.number
            }

            if (achievementsHolder.items != null) {
                for (info in achievementsHolder.items!!.values) {
                    val ach = Achievement()
                    if (info != null) {
                        ach.name = info.id
                    }
                    val number = captainAchievements[info?.id]
                    ach.number = (number ?: 0)
                    achs.add(ach)
                }
                Collections.sort(achs) { lhs, rhs -> rhs.number - lhs.number }

                val adapter = AchievementsAdapter(context, R.layout.list_achievement, achs)
                battleGrid!!.adapter = adapter

                battleGrid!!.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        val ctx = view.context
                        val achievementName = view.tag as String
                        val info = infoManager!!.getAchievements(requireContext())[achievementName]
                        if (info != null) {
                            val builder = AlertDialog.Builder(ctx)
                            builder.setTitle(info.name)
                            builder.setMessage(info.description)
                            builder.setPositiveButton(R.string.dismiss) { dialog, which -> dialog.dismiss() }
                            builder.show()
                        } else {
                            Toast.makeText(
                                view.context,
                                "Achievement Information not found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                getOldAchievements(createCapIdStr(captain.server, captain.id))
            } else {
//                Toast.makeText(getContext(), getString(R.string.resources_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun getOldAchievements(accountId: String) {
        val t = Thread {
            try {
                val achievements = getPlayerAchievements(
                    requireContext(), accountId
                )
                if (achievements.savedAchievements != null && achievements.savedAchievements.size > 1 && battleGrid!!.adapter != null) {
                    val adapter = battleGrid!!.adapter as AchievementsAdapter
                    val achis = achievements.savedAchievements[1]
                    val mapAchi: MutableMap<String?, Int> = HashMap()
                    for (achievement in achis) {
                        mapAchi[achievement.name] = achievement.number
                    }
                    adapter.setSavedAchievements(mapAchi)
                }
            } catch (e: Exception) {
            }
        }
        t.start()
    }

    @Subscribe
    fun onReceive(event: CaptainReceivedEvent?) {
        initView()
    }

    @Subscribe
    fun onRefresh(event: RefreshEvent?) {
        refreshing(true)
        battleGrid!!.adapter = null
    }

    @Subscribe
    fun onProgressEvent(event: ProgressEvent) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout!!.isRefreshing = event.isRefreshing
        }
    }
}
