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
import com.half.wowsca.model.UpgradeClickEvent
import com.half.wowsca.model.encyclopedia.items.EquipmentInfo
import com.half.wowsca.ui.CAFragment
import com.half.wowsca.ui.adapter.UpgradesAdapter
import com.half.wowsca.ui.encyclopedia.ShipProfileActivity
import org.greenrobot.eventbus.Subscribe
import java.text.DecimalFormat
import java.util.Collections

/**
 * Created by slai4 on 4/26/2016.
 */
class UpgradesFragment : CAFragment() {
    var recyclerView: RecyclerView? = null
    var layoutManager: GridLayoutManager? = null
    var adapter: UpgradesAdapter? = null

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
        val holder = infoManager!!.getUpgrades(requireContext())
        if (holder.items != null && recyclerView!!.adapter == null) {
            layoutManager =
                GridLayoutManager(context, resources.getInteger(R.integer.shipopedia_upgrade_grid))
            layoutManager!!.orientation = GridLayoutManager.VERTICAL
            recyclerView!!.layoutManager = layoutManager

            val upgrades = holder.items?.values?.toMutableList() ?: mutableListOf()

            upgrades.sortWith { lhs, rhs ->
                lhs?.name?.compareTo(
                    rhs?.name ?: "", ignoreCase = true
                ) ?: 0
            }
            upgrades.sortWith(Comparator { lhs, rhs -> lhs!!.price - rhs!!.price })

            adapter = UpgradesAdapter(upgrades.filterNotNull(), requireContext())
            recyclerView!!.adapter = adapter
        }
    }

    override fun onPause() {
        super.onPause()
        eventBus.unregister(this)
    }

    @Subscribe
    fun upgradeClicked(event: UpgradeClickEvent) {
        val holder = infoManager!!.getUpgrades(requireContext())
        val info = holder[event.id]
        if (info != null) {
            val formatter = DecimalFormat(ShipProfileActivity.Companion.PATTERN)
            createGeneralAlert(
                activity,
                info.name,
                info.description + getString(R.string.encyclopedia_upgrade_cost) + formatter.format(
                    info.price.toLong()
                ),
                getString(R.string.dismiss),
                R.drawable.ic_upgrade
            )
        } else {
            Toast.makeText(context, R.string.resources_error, Toast.LENGTH_SHORT).show()
        }
    }
}
