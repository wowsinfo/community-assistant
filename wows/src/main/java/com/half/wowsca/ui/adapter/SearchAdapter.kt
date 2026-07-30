package com.half.wowsca.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.half.wowsca.R
import com.half.wowsca.listener.AddRemoveListener
import com.half.wowsca.managers.CaptainManager
import com.half.wowsca.managers.CaptainManager.createCapIdStr
import com.half.wowsca.model.Captain

class SearchAdapter(
    context: Context?,
    resource: Int,
    objects: List<Captain?>?,
    private val onCaptainChanged: (Captain, Boolean) -> Unit,
) : ArrayAdapter<Captain?>(context!!, resource, objects!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_search, parent, false)
        }
        val c = getItem(position)!!
        val name = convertView!!.findViewById<TextView>(R.id.list_search_name)
        val box = convertView.findViewById<CheckBox>(R.id.list_search_checkbox)
        name.text = c!!.name
        box.isChecked = CaptainManager.getCaptains(context)?.get(createCapIdStr(c.server, c.id)) != null
        box.isClickable = false
        val checkBoxArea = convertView.findViewById<View>(R.id.list_search_checkbox_area)
        checkBoxArea.setOnClickListener(AddRemoveListener(c, context, box, onCaptainChanged))
        return convertView
    }
}
