package com.half.wowsca.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.half.wowsca.R
import com.half.wowsca.model.Captain

/**
 * Created by slai4 on 10/14/2015.
 */
class CompareAdapter(context: Context?, resource: Int, objects: List<Captain?>?) :
    ArrayAdapter<Captain?>(
        context!!, resource, objects!!
    ) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_compare, parent, false)
        }
        val c = getItem(position)

        val text = convertView!!.findViewById<TextView>(R.id.list_compare_text)

        text.text = c!!.name

        return convertView
    }
}
