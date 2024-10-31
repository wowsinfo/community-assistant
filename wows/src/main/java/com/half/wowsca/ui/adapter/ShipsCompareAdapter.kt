package com.half.wowsca.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.half.wowsca.CAApp.Companion.getTextColor
import com.half.wowsca.R

/**
 * Created by slai4 on 7/29/2017.
 */
class ShipsCompareAdapter : RecyclerView.Adapter<ShipsCompareAdapter.ViewHolder>() {
    private var ships: List<Map<String, Float>>? = null
    var graphNames: List<String>? = null
    var shipColors: Map<String, Int>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val convertView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_compare_ship_graph, parent, false)
        val holder = ViewHolder(convertView)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chart = holder.barChart

        val info = ships!![position]

        holder.tvTitle.text = graphNames!![position]

        val textColor = getTextColor(chart.context)

        chart.setDrawBarShadow(false)
        chart.setDrawGridBackground(false)
        chart.setDrawValueAboveBar(true)
        chart.isDoubleTapToZoomEnabled = false

        setupXAxis(textColor, chart)

        setupYAxis(textColor, chart)

        setupYAxis2(textColor, chart)

        val l = chart.legend
        l.isEnabled = false

        val xVals = ArrayList<String>()
        val itea = info.keys.iterator()
        while (itea.hasNext()) {
            xVals.add(itea.next())
        }
        val colors = IntArray(xVals.size)

        for (i in xVals.indices) {
            val shipName = xVals[i]
            colors[i] = shipColors!![shipName]!!
        }

        val yVals1 = ArrayList<BarEntry>()
        for (i in xVals.indices) {
            val value = info[xVals[i]]!!
            yVals1.add(BarEntry(value, i))
        }

        val set1 = BarDataSet(yVals1, "")
        set1.barSpacePercent = 20f
        set1.setColors(colors)
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set1)

        val data = BarData(xVals, dataSets)
        data.setValueTextSize(10f)
        data.setValueTextColor(textColor)

        chart.setDescription("")

        chart.data = data
        chart.requestLayout()
    }

    private fun setupYAxis2(textColor: Int, chart: BarChart) {
        val yAxis2 = chart.axisLeft
        yAxis2.setLabelCount(6, true)
        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis2.textColor = textColor
    }

    private fun setupYAxis(textColor: Int, chart: BarChart) {
        val yAxis = chart.axisRight
        yAxis.setLabelCount(4, true)
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.textColor = textColor
        yAxis.isEnabled = false
    }

    private fun setupXAxis(textColor: Int, chart: BarChart) {
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = textColor
        xAxis.setDrawGridLines(true)
        xAxis.setLabelsToSkip(0)
        xAxis.labelRotationAngle = 25f
    }

    override fun getItemCount(): Int {
        return ships!!.size
    }

    fun setShips(ships: List<Map<String, Float>>?) {
        this.ships = ships
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var barChart: BarChart = itemView.findViewById(R.id.list_compare_ship_graph)
        var tvTitle: TextView = itemView.findViewById(R.id.list_compare_ship_graph_text)
    }
}