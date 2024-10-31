package com.half.wowsca.ui.encyclopedia.tabs

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.getTheme
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.CAApp.Companion.isDarkTheme
import com.half.wowsca.R
import com.half.wowsca.managers.CompareManager.getSHIPS
import com.half.wowsca.managers.InfoManager
import com.half.wowsca.model.ShipCompareEvent
import com.half.wowsca.model.encyclopedia.items.ShipInfo
import com.half.wowsca.ui.CAFragment
import com.half.wowsca.ui.adapter.EncyclopediaAdapter
import org.greenrobot.eventbus.Subscribe
import java.util.Collections

/**
 * Created by slai4 on 12/1/2015.
 */
class EncyclopediaShipsFragment : CAFragment() {
    private var listView: RecyclerView? = null
    private var adapter: EncyclopediaAdapter? = null
    private var layoutManager: GridLayoutManager? = null

    private var topArea: View? = null
    private var etSearch: EditText? = null
    private var delete: View? = null

    private var sTier: Spinner? = null
    private var sNation: Spinner? = null

    private var searchText: String? = null
    private var searchTier = EncyclopediaAdapter.EMPTY_FILTER
    private var searchNation = EncyclopediaAdapter.EMPTY_FILTER

    private var tvCompareText: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_encyclopedia_list, container, false)
        bindView(view)
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH)
            searchTier = savedInstanceState.getInt(TIER)
            if (searchTier == 0) {
                searchTier = EncyclopediaAdapter.EMPTY_FILTER
            }
            searchNation = savedInstanceState.getInt(NATION)
            if (searchNation == 0) {
                searchNation = EncyclopediaAdapter.EMPTY_FILTER
            }
        }
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH, etSearch!!.text.toString())
        outState.putInt(NATION, searchNation)
        outState.putInt(TIER, searchTier)
    }

    private fun bindView(view: View) {
        topArea = view.findViewById(R.id.encyclopedia_list_top_area)

        listView = view.findViewById(R.id.encyclopedia_list_listview)
        etSearch = view.findViewById(R.id.encyclopedia_list_et)
        delete = view.findViewById(R.id.encyclopedia_list_et_delete)

        sTier = view.findViewById(R.id.encyclopedia_list_tier_selector)
        sNation = view.findViewById(R.id.encyclopedia_list_nation_selector)

        tvCompareText = view.findViewById(R.id.encyclopedia_list_compare_text)
    }

    override fun onResume() {
        super.onResume()
        eventBus.register(this)
        initView()
    }

    override fun onPause() {
        super.onPause()
        eventBus.unregister(this)
    }

    private fun initView() {
        if (listView!!.adapter == null) {
            try {
                val ships: MutableList<ShipInfo?> = ArrayList()
                infoManager?.getShipInfo(listView!!.context)?.items?.values?.let { ships.addAll(it) }
                ships.sortWith { lhs, rhs ->
                    lhs?.name?.compareTo(
                        rhs?.name ?: "",
                        ignoreCase = true
                    ) ?: 0
                }
                ships.sortWith { lhs, rhs -> rhs!!.tier - lhs!!.tier }
                adapter = EncyclopediaAdapter(ships, listView!!.context)

                listView!!.setHasFixedSize(true)

                layoutManager = GridLayoutManager(
                    listView!!.context,
                    resources.getInteger(R.integer.shipopedia_grid)
                )
                layoutManager!!.orientation = GridLayoutManager.VERTICAL
                listView!!.layoutManager = layoutManager

                listView!!.adapter = adapter

                if (!TextUtils.isEmpty(etSearch!!.text.toString())) {
                    filter()
                }
            } catch (e: Exception) {
                Toast.makeText(context, R.string.resources_error, Toast.LENGTH_SHORT).show()
            }
        } else {
            if (!TextUtils.isEmpty(etSearch!!.text.toString())) {
                filter()
            }
        }
        setUpFiltering()

        setupCompareFeature()
    }

    private fun setupCompareFeature() {
        if (getTheme(listView!!.context) == "ocean") {
            tvCompareText!!.setBackgroundColor(
                ContextCompat.getColor(
                    listView!!.context,
                    R.color.bottom_background
                )
            )
        } else {
            tvCompareText!!.setBackgroundColor(
                ContextCompat.getColor(
                    listView!!.context,
                    R.color.material_background_dark
                )
            )
        }
        setCompareText()
    }

    private fun setUpFiltering() {
        if (!TextUtils.isEmpty(searchText)) {
            etSearch!!.setText(searchText)
        }
        delete!!.setOnClickListener { etSearch!!.setText("") }
        etSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (adapter != null) {
                    adapter!!.filter(s, searchNation, searchTier)
                }
                if (!TextUtils.isEmpty(etSearch!!.text.toString())) {
                    delete!!.visibility = View.VISIBLE
                } else {
                    delete!!.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        initNationSpinner()

        initTierSpinner()

        if (!TextUtils.isEmpty(etSearch!!.text.toString())) {
            delete!!.visibility = View.VISIBLE
        } else {
            delete!!.visibility = View.GONE
        }
    }

    private fun initNationSpinner() {
        val nationList: MutableList<String> = ArrayList()
        nationList.add(getString(R.string.encyclopedia_all))
        Collections.addAll(nationList, *resources.getStringArray(R.array.search_nation))
        val adapter = ArrayAdapter(requireContext(), R.layout.ca_spinner_item_trans, nationList)
        adapter.setDropDownViewResource(if (!isDarkTheme(sNation!!.context)) R.layout.ca_spinner_item else R.layout.ca_spinner_item_dark)
        sNation!!.adapter = adapter
        if (searchNation > EncyclopediaAdapter.EMPTY_FILTER) {
            sNation!!.setSelection(searchNation + 1)
        } else {
            searchNation = EncyclopediaAdapter.EMPTY_FILTER
        }
        sNation!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                searchNation = position - 1
                filter()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun initTierSpinner() {
        val tiersList: MutableList<String> = ArrayList()
        tiersList.add(getString(R.string.encyclopedia_all))
        for (i in 1..10) {
            tiersList.add(i.toString() + "")
        }
        val adapter2 = ArrayAdapter(requireContext(), R.layout.ca_spinner_item_trans, tiersList)
        adapter2.setDropDownViewResource(if (!isDarkTheme(sTier!!.context)) R.layout.ca_spinner_item else R.layout.ca_spinner_item_dark)
        sTier!!.adapter = adapter2
        if (searchTier > EncyclopediaAdapter.EMPTY_FILTER) {
            sTier!!.setSelection(searchTier + 1)
        } else {
            searchTier = EncyclopediaAdapter.EMPTY_FILTER
        }
        sTier!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                searchTier = position - 1
                filter()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun filter() {
        if (adapter != null) {
            adapter!!.filter(etSearch!!.text.toString(), searchNation, searchTier)
        }
    }

    @Subscribe
    fun onShipCompare(event: ShipCompareEvent?) {
        setCompareText()
        adapter!!.notifyDataSetChanged()
    }

    private fun setCompareText() {
        val holder = InfoManager().getShipInfo(tvCompareText!!.context)
        val size = getSHIPS()!!.size
        if (size > 0) {
            val sb = StringBuilder()
            var i = 0
            for (id in getSHIPS()!!) {
                val info = holder[id]
                if (info != null) {
                    sb.append(info.name + (if (i + 1 < size) ", " else ""))
                }
                i++
            }
            tvCompareText!!.text = sb.toString()
        } else {
            tvCompareText!!.setText(R.string.long_click_to_add_ship_to_compare_list)
        }
    }

    companion object {
        const val SEARCH: String = "search"
        const val TIER: String = "tier"
        const val NATION: String = "nation"
    }
}