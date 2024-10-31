package com.half.wowsca.ui.encyclopedia.tabs

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.CAApp.Companion.isDarkTheme
import com.half.wowsca.R
import com.half.wowsca.alerts.Alert.createGeneralAlert
import com.half.wowsca.model.FlagClickedEvent
import com.half.wowsca.model.encyclopedia.items.ExteriorItem
import com.half.wowsca.ui.CAFragment
import com.half.wowsca.ui.adapter.FlagsAdapter
import org.greenrobot.eventbus.Subscribe
import java.util.Collections

/**
 * Created by slai4 on 4/25/2016.
 */
class FlagsFragment : CAFragment() {
    var recyclerView: RecyclerView? = null
    var layoutManager: GridLayoutManager? = null
    var adapter: FlagsAdapter? = null

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
        val holder = infoManager!!.getExteriorItems(requireContext())
        if (holder.items != null && recyclerView!!.adapter == null) {
            layoutManager =
                GridLayoutManager(context, resources.getInteger(R.integer.shipopedia_upgrade_grid))
            layoutManager!!.orientation = GridLayoutManager.VERTICAL
            recyclerView!!.layoutManager = layoutManager

            val flags = holder.items?.values?.toMutableList() ?: mutableListOf()

            flags.sortWith { lhs, rhs ->
                lhs?.name?.compareTo(
                    rhs?.name ?: "",
                    ignoreCase = true
                ) ?: 0
            }
            flags.sortWith { lhs, rhs ->
                rhs?.type?.compareTo(
                    lhs?.type ?: "",
                    ignoreCase = true
                ) ?: 0
            }

            adapter = FlagsAdapter(flags.filterNotNull(), requireContext())
            recyclerView!!.adapter = adapter
        }
    }

    override fun onPause() {
        super.onPause()
        eventBus.unregister(this)
    }

    @Subscribe
    fun flagClickedEvent(event: FlagClickedEvent) {
        val holder = infoManager!!.getExteriorItems(requireContext())
        val item = holder[event.id]
        if (item != null) {
            val sb = StringBuilder()
            sb.append(item.description)
            if (item.coef != null && !item.coef!!.isEmpty()) {
                sb.append("\n\n")
                val iter: MutableIterator<String?> = item.coef!!.keys.iterator()
                while (iter.hasNext()) {
                    val key = iter.next()
                    val pair = item.coef!![key]
                    sb.append(pair!!.first)
                    if (iter.hasNext()) {
                        sb.append("\n")
                    }
                }
            }
            val d = ContextCompat.getDrawable(requireContext(), R.drawable.ic_flags)
            if (!isDarkTheme(context)) d!!.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.top_background
                ), PorterDuff.Mode.MULTIPLY
            )
            createGeneralAlert(activity, item.name, sb.toString(), getString(R.string.dismiss), d)
        } else {
            Toast.makeText(context, R.string.resources_error, Toast.LENGTH_SHORT).show()
        }
    }
}