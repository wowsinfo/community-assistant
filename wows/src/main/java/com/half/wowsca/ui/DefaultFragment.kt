package com.half.wowsca.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.half.wowsca.R

/**
 * Created by slai4 on 9/15/2015.
 */
class DefaultFragment : CAFragment() {
    var tvText: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_default, container, false)
        bindView(view)
        return view
    }

    private fun bindView(view: View) {
        tvText = view.findViewById(R.id.textView)
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView() {
        tvText!!.text = getString(R.string.patch_notes) + getString(R.string.update_notes_achieve)
    }

    override fun onPause() {
        super.onPause()
    }
}
