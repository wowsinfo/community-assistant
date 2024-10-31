package com.half.wowsca.ui.compare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.half.wowsca.R
import com.half.wowsca.ui.CAFragment

/**
 * Created by slai47 on 3/5/2017.
 */
class ShipCompareDifFragment : CAFragment() {
    /**
     * this holds all the comparison for every ship
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_compare_ships_dif, container, false)
        onBind(view)
        return view
    }

    private fun onBind(view: View) {
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView() {
    }
}
