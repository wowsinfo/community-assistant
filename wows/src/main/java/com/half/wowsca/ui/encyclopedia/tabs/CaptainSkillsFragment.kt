package com.half.wowsca.ui.encyclopedia.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.R
import com.half.wowsca.alerts.Alert.createGeneralAlert
import com.half.wowsca.model.CaptainSkillClickedEvent
import com.half.wowsca.model.encyclopedia.items.CaptainSkill
import com.half.wowsca.ui.CAFragment
import com.half.wowsca.ui.adapter.CaptainSkillsAdapter
import org.greenrobot.eventbus.Subscribe
import java.util.Collections

/**
 * Created by slai4 on 4/25/2016.
 */
class CaptainSkillsFragment : CAFragment() {
    var recyclerView: RecyclerView? = null
    var layoutManager: GridLayoutManager? = null
    var adapter: CaptainSkillsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.view_recycler_view, container, false)
        recyclerView = view as RecyclerView
        return view
    }

    override fun onResume() {
        super.onResume()
        eventBus.register(this)
        val holder = infoManager!!.getCaptainSkills(requireContext())
        if (holder.items != null && recyclerView!!.adapter == null) {
            layoutManager =
                GridLayoutManager(context, resources.getInteger(R.integer.shipopedia_upgrade_grid))
            layoutManager!!.orientation = GridLayoutManager.VERTICAL
            recyclerView!!.layoutManager = layoutManager

            val skills = holder.items?.values?.toMutableList() ?: mutableListOf()
            skills.sortWith { lhs, rhs ->
                lhs?.name!!.compareTo(
                    rhs?.name!!,
                    ignoreCase = true
                )
            }
            skills.sortWith { lhs, rhs -> lhs?.tier?.minus(rhs?.tier ?: 0) ?: 0 }
            val okSkills = skills.filterNotNull()
            adapter = CaptainSkillsAdapter(okSkills, requireContext())
            recyclerView!!.adapter = adapter
        }
    }

    override fun onPause() {
        super.onPause()
        eventBus.unregister(this)
    }

    @Subscribe
    fun captainClickedEvent(event: CaptainSkillClickedEvent) {
        val holder = infoManager!!.getCaptainSkills(requireContext())
        val skill = holder[event.id.toString() + ""]
        if (skill != null) {
            val sb = StringBuilder()
            sb.append(getString(R.string.encyclopedia_tier_start) + " " + skill.tier + "\n\n")
            if (skill.getAbilities() != null && !skill.getAbilities()!!.isEmpty()) {
                for (i in skill.getAbilities()!!.indices) {
                    sb.append(skill.getAbilities()!![i])
                    if (i < (skill.getAbilities()!!.size - 1)) {
                        sb.append("\n")
                    }
                }
            }
            createGeneralAlert(
                activity,
                skill.name,
                sb.toString(),
                getString(R.string.dismiss),
                R.drawable.ic_captain_skills
            )
        } else {
            Toast.makeText(context, R.string.resources_error, Toast.LENGTH_SHORT).show()
        }
    }
}
