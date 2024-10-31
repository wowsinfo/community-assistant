package com.half.wowsca.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.half.wowsca.ui.views.ShipModuleView

/**
 * Created by slai4 on 6/29/2017.
 */
class ShipModuleCompareAdapter(private var ids: List<Long>, private val ctx: Context) :
    RecyclerView.Adapter<ShipModuleCompareAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ShipModuleView(ctx)
        view.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val holder: ViewHolder = ViewHolder(
            ShipModuleView(
                ctx
            )
        )
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val id = ids[position]
        holder.view.shipID = id
        holder.view.initView()
    }

    override fun getItemCount(): Int {
        return try {
            ids.size
        } catch (e: Exception) {
            0
        }
    }

    fun setIds(ids: List<Long>) {
        this.ids = ids
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view: ShipModuleView = itemView as ShipModuleView
    }
}
