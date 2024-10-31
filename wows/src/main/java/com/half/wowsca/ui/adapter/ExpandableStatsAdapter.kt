package com.half.wowsca.ui.adapter

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.formatter.YAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ViewPortHandler
import com.half.wowsca.CAApp.Companion.getTextColor
import com.half.wowsca.CAApp.Companion.getTheme
import com.half.wowsca.CAApp.Companion.isColorblind
import com.half.wowsca.R
import com.half.wowsca.model.enums.EncyclopediaType
import com.half.wowsca.model.listModels.EncyclopediaChild
import java.text.DecimalFormat

/**
 * Created by slai4 on 12/1/2015.
 */
class ExpandableStatsAdapter(
    private val headers: List<String>,
    private val values: Map<String, List<EncyclopediaChild>>,
    private val ctx: Context
) : BaseExpandableListAdapter() {
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View? {
        var convertView = convertView
        val header = getGroup(groupPosition) as String

        if (convertView == null) {
            convertView = LayoutInflater.from(ctx).inflate(R.layout.list_group_stats, parent, false)
        }

        //        ImageView img = (ImageView) convertView.findViewById(R.id.list_group_stats_img);
        val text = convertView?.findViewById<TextView>(R.id.list_group_stats_text)

        //        if(isExpanded){
//            img.setImageResource(R.drawable.expander_close_holo_dark);
//        } else {
//            img.setImageResource(R.drawable.expander_open_holo_dark);
//        }
        text?.text = header

        return convertView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View? {
        val header = getGroup(groupPosition) as String
        val premium = header == ctx.getString(R.string.premium_ship_stats)
        val child = getChild(groupPosition, childPosition) as EncyclopediaChild
        val layoutId = R.layout.list_child_bar

        val view = LayoutInflater.from(ctx).inflate(layoutId, parent, false)

        val text = view.findViewById<TextView>(R.id.list_child_title)
        val chart = view.findViewById<View>(R.id.list_child_graph)
        if (premium) {
            val size = child.values.size * 18

            val params = LinearLayout.LayoutParams(chart.layoutParams)
            params.height = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                size.toFloat(),
                ctx.resources.displayMetrics
            ).toInt()
            chart.layoutParams = params
        }

        createBarChart(chart as HorizontalBarChart, child)

        text.text = child.title

        return view
    }

    private fun createBarChart(chart: HorizontalBarChart, child: EncyclopediaChild) {
        val textColor = getTextColor(chart.context)
        val colorblind = isColorblind(chart.context)
        val accentColor =
            if (!colorblind) (if (getTheme(chart.context) == "ocean") ContextCompat.getColor(
                chart.context,
                R.color.graph_line_color
            ) else ContextCompat.getColor(chart.context, R.color.top_background))
            else ContextCompat.getColor(chart.context, R.color.white)
        chart.setDrawBarShadow(false)
        chart.setDrawValueAboveBar(false)
        chart.setPinchZoom(false)
        chart.isDoubleTapToZoomEnabled = false
        chart.setDrawGridBackground(false)
        chart.setDrawValueAboveBar(true)
        chart.setTouchEnabled(false)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = textColor
        xAxis.setDrawGridLines(true)

        val yAxis = chart.axisRight
        yAxis.setLabelCount(6, false)
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.textColor = textColor
        yAxis.isEnabled = false

        val yAxis2 = chart.axisLeft
        yAxis2.setLabelCount(6, false)
        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis2.textColor = textColor
        if (child.type == EncyclopediaType.LARGE_NUMBER) {
            yAxis2.valueFormatter = LargeValueFormatter()
        } else if (child.type == EncyclopediaType.PERCENT) {
            yAxis2.valueFormatter = MyYFormatter()
        }

        val l = chart.legend
        l.isEnabled = false
        val xVals: MutableList<String> = ArrayList()
        val colorList: MutableList<Int> = ArrayList()
        for (i in child.titles.indices) {
            xVals.add(child.titles[i])
            val key = child.types[i]
            if (key.equals("cruiser", ignoreCase = true)) {
                colorList.add(Color.parseColor("#4CAF50"))
            } else if (key.equals("battleship", ignoreCase = true)) {
                colorList.add(Color.parseColor("#F44336"))
            } else if (key.equals("aircarrier", ignoreCase = true)) {
                colorList.add(Color.parseColor("#673AB7"))
            } else if (key.equals("destroyer", ignoreCase = true)) {
                colorList.add(Color.parseColor("#FDD835"))
            }
        }

        val yVals: MutableList<BarEntry> = ArrayList()
        for (i in child.values.indices) {
            yVals.add(BarEntry(child.values[i], i))
        }
        if (yVals.size > 0) {
            val set1 = BarDataSet(yVals, "")
            set1.colors = colorList
            set1.barSpacePercent = 20f
            set1.valueTextColor = textColor

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)

            val data = BarData(xVals, dataSets)
            data.setValueTextSize(10f)
            data.setValueTextColor(textColor)
            if (child.type == EncyclopediaType.LARGE_NUMBER) {
                data.setValueFormatter(LargeValueFormatter())
            } else if (child.type == EncyclopediaType.PERCENT) {
                data.setValueFormatter(MyFormatter())
            }

            chart.setDescription("")
            chart.data = data
            chart.requestLayout()

            chart.animateY(1000)
        }
    }

    override fun getGroupCount(): Int {
        return try {
            headers.size
        } catch (e: Exception) {
            0
        }
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return try {
            values[headers[groupPosition]]!!.size
        } catch (e: Exception) {
            0
        }
    }

    override fun getGroup(groupPosition: Int): Any {
        return headers[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return values[headers[groupPosition]]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return 0
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return false
    }


    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }

    private inner class MyYFormatter : YAxisValueFormatter {
        private var mFormat: DecimalFormat

        init {
            this.mFormat = DecimalFormat("##.##%")
        }

        fun change(tag: String?) {
            this.mFormat = DecimalFormat("##.##%")
        }

        override fun getFormattedValue(value: Float, yAxis: YAxis): String {
            return mFormat.format(value.toDouble())
        }
    }

    private inner class MyFormatter : ValueFormatter {
        private val mFormat = DecimalFormat("##.#%")

        override fun getFormattedValue(
            value: Float,
            entry: Entry,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler
        ): String {
            return mFormat.format(value.toDouble())
        }
    }
}
