package com.half.wowsca.ui.compare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import com.half.wowsca.util.AppEventBus
import kotlinx.coroutines.launch
import com.half.wowsca.R
import com.half.wowsca.managers.CompareManager.getSHIPS
import com.half.wowsca.ui.CAFragment
import com.half.wowsca.ui.adapter.ShipModuleCompareAdapter
import com.utilities.logging.Dlog.d

/**
 * Created by slai47 on 5/21/2017.
 */
class ShipModuleListFragment : CAFragment() {
    private var gridView: RecyclerView? = null

    private var adapter: ShipModuleCompareAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_compare_module_list, container, false)
        onBind(view)
        return view
    }

    private fun onBind(view: View) {
        gridView = view.findViewById(R.id.fragment_compare_module_list)
    }

    override fun onResume() {
        super.onResume()
        // register removed
        observeAppEvents()
        initView()
    }

    override fun onPause() {
        super.onPause()
        // unregister removed
    }

    private fun initView() {
        if (adapter == null) {
            val manager = GridLayoutManager(context, resources.getInteger(R.integer.ship_rows))
            gridView!!.layoutManager = manager
            adapter = ShipModuleCompareAdapter(getSHIPS()!!, context!!)
            gridView!!.adapter = adapter
        } else {
        }
    }

    private fun observeAppEvents() {
        lifecycleScope.launch {
            AppEventBus.shipIdEvents.collect { shipId ->
                onShipIdReceived(shipId)
            }
        }
    }

    private fun onShipIdReceived(shipId: Long) {
        d("ShiModuleListFragment", "onRefresh = $shipId")
        if (adapter != null) {
            var i = 0
            while (i < getSHIPS()!!.size) {
                if (getSHIPS()!![i] === shipId) break
                i++
            }
            adapter!!.notifyDataSetChanged()
        }
    }
}