package com.half.wowsca.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.half.wowsca.R
import com.half.wowsca.model.enums.AverageType
import com.half.wowsca.model.listModels.ListAverages
import java.text.DecimalFormat

/**
 * Created by slai4 on 11/8/2015.
 */
class AveragesAdapter(context: Context?, resource: Int, private var objects: List<ListAverages>) :
    ArrayAdapter<ListAverages?>(
        context!!, resource, objects
    ) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView =
                LayoutInflater.from(context).inflate(R.layout.list_averages, parent, false)
        }
        val title = convertView!!.findViewById<TextView>(R.id.list_average_title)
        val text = convertView.findViewById<TextView>(R.id.list_average_avg)
        val midText = convertView.findViewById<TextView>(R.id.list_average_mid_text)

        val avg = getItem(position)

        title.text = avg!!.title

        val formatter = if (avg.type == AverageType.LARGE_NUMBER) {
            DecimalFormat("#")
        } else if (avg.type == AverageType.PERCENT) {
            DecimalFormat("#.#%")
        } else {
            DecimalFormat("#.#")
        }
        val number = avg.average - avg.expected

        midText.text =
            formatter.format(avg.average.toDouble()) + "/" + formatter.format(avg.expected.toDouble())


        val sb = StringBuilder()
        if (number > 0) {
            text.setTextColor(ContextCompat.getColor(context, R.color.average_up))
            sb.append("+")
        } else if (number < 0) {
            text.setTextColor(ContextCompat.getColor(context, R.color.average_down))
        } else {
            text.setTextColor(ContextCompat.getColor(context, R.color.white))
            sb.append("")
        }
        sb.append(formatter.format(number.toDouble()))
        text.text = sb.toString()

        return convertView
    }

    override fun getItem(position: Int): ListAverages? {
        return try {
            objects[position]
        } catch (e: Exception) {
            null
        }
    }

    override fun getCount(): Int {
        return try {
            objects.size
        } catch (e: Exception) {
            0
        }
    }

    fun setObjects(objects: List<ListAverages>) {
        this.objects = objects
        notifyDataSetChanged()
    }
}
