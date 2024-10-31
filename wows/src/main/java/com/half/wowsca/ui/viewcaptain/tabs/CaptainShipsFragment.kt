package com.half.wowsca.ui.viewcaptain.tabs

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.half.wowsca.CAApp
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.isDarkTheme
import com.half.wowsca.R
import com.half.wowsca.interfaces.ICaptain
import com.half.wowsca.model.Captain
import com.half.wowsca.model.CaptainReceivedEvent
import com.half.wowsca.model.ProgressEvent
import com.half.wowsca.model.RefreshEvent
import com.half.wowsca.model.ScrollToEvent
import com.half.wowsca.model.SortingDoneEvent
import com.half.wowsca.ui.CAFragment
import com.half.wowsca.ui.adapter.ShipsAdapter
import com.utilities.logging.Dlog.wtf
import com.utilities.preferences.Prefs
import org.greenrobot.eventbus.Subscribe

/**
 * Created by slai4 on 9/15/2015.
 */
class CaptainShipsFragment() : CAFragment() {
    private var sSorter: Spinner? = null

    private var etSearch: EditText? = null
    private var delete: View? = null

    private var recyclerView: RecyclerView? = null
    private var adapter: ShipsAdapter? = null
    private var layoutManager: GridLayoutManager? = null

    private var spinnerCheck: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_captain_ships, container, false)
        bindView(view)
        return view
    }

    private fun bindView(view: View) {
        sSorter = view.findViewById(R.id.ships_spinner)
        etSearch = view.findViewById(R.id.ships_search)
        delete = view.findViewById(R.id.ships_delete)
        recyclerView = view.findViewById(R.id.ships_list)

        bindSwipe(view)
        initSwipeLayout()
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
        var captain: Captain? = null
        try {
            captain = (getActivity() as ICaptain?)!!.getCaptain(getContext())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (captain != null && captain.getShips() != null) {
            refreshing(false)
            sSorter!!.setEnabled(true)
            etSearch!!.setEnabled(true)

            if (recyclerView!!.getAdapter() == null) {
                recyclerView!!.setHasFixedSize(false)

                layoutManager =
                    GridLayoutManager(getContext(), getResources().getInteger(R.integer.ship_rows))
                layoutManager!!.setOrientation(GridLayoutManager.VERTICAL)
                recyclerView!!.setLayoutManager(layoutManager)

                adapter = context?.let { ShipsAdapter(captain.ships, it) }
                recyclerView!!.setAdapter(adapter)
            } else if (adapter != null) {
                val pref: Prefs = Prefs(recyclerView!!.getContext())
                adapter!!.notifyDataSetChanged()
            }

            if (recyclerView!!.getAdapter() != null && CAApp.lastShipPos != 0) {
                recyclerView!!.scrollToPosition(CAApp.lastShipPos)
            }
            setUpSearching()
            setUpSorting()
        } else {
            if (captain != null && captain.getShips() == null) refreshing(true)
            sSorter!!.setEnabled(false)
            etSearch!!.setEnabled(false)
        }
    }

    private fun setUpSorting() {
        if (sSorter!!.getAdapter() == null) {
            spinnerCheck = false
            val sortAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
                requireActivity(),
                R.array.ship_sorting,
                R.layout.ca_spinner_item_trans
            )
            sortAdapter.setDropDownViewResource(if (!isDarkTheme(sSorter!!.getContext())) R.layout.ca_spinner_item else R.layout.ca_spinner_item_dark)
            sSorter!!.setAdapter(sortAdapter)
            refreshSortingChoice(true)
            sSorter!!.setEnabled(true)
            sSorter!!.setEnabled(true)
            sSorter!!.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                    spinnerCheck = true
                    return false
                }
            })

            sSorter!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    wtf("OnItemSelected", "check = " + spinnerCheck + " pos = " + position)
                    if (!spinnerCheck) {
                    } else {
                        val sortType: String = parent.getItemAtPosition(position) as String
                        val prefs: Prefs = Prefs(getContext())
                        prefs.setString(SAVED_SORT, sortType)
                        try {
                            if (adapter == null) adapter =
                                recyclerView!!.getAdapter() as ShipsAdapter?
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if (adapter != null) {
                            adapter!!.sort(sortType)
                            CAApp.lastShipPos = 0
                            sSorter!!.setEnabled(false)
                        } else {
                            Toast.makeText(
                                getContext(),
                                "Oops something went wrong. Refresh the view to fix.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            })
        } else {
            refreshSortingChoice(false)
        }
    }

    private fun refreshSortingChoice(clearCheck: Boolean) {
        val prefs: Prefs = Prefs(getContext())
        val savedSort: String? = prefs.getString(SAVED_SORT, "")
        if (!TextUtils.isEmpty(savedSort)) {
            val sortTypes: Array<String> = getResources().getStringArray(R.array.ship_sorting)
            for (i in sortTypes.indices) {
                if ((sortTypes.get(i) == savedSort)) {
                    if (clearCheck) spinnerCheck = false // because spinners suck

                    sSorter!!.setSelection(i)
                    break
                }
            }
        }
    }

    private fun setUpSearching() {
        delete!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                etSearch!!.setText("")
            }
        })
        etSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (adapter != null) {
                    adapter!!.filter(s)
                }
                if (!TextUtils.isEmpty(etSearch!!.getText().toString())) {
                    sSorter!!.setEnabled(false)
                    delete!!.setVisibility(View.VISIBLE)
                } else {
                    sSorter!!.setEnabled(true)
                    delete!!.setVisibility(View.GONE)
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        if (!TextUtils.isEmpty(etSearch!!.getText().toString())) {
            sSorter!!.setEnabled(false)
            delete!!.setVisibility(View.VISIBLE)
        } else {
            sSorter!!.setEnabled(true)
            delete!!.setVisibility(View.GONE)
        }
    }

    @Subscribe
    fun onReceive(event: CaptainReceivedEvent?) {
        initView()
    }

    @Subscribe
    fun onSortDone(event: SortingDoneEvent?) {
        sSorter!!.post(object : Runnable {
            override fun run() {
                sSorter!!.setEnabled(true)
            }
        })
    }

    @Subscribe
    fun onRefresh(event: RefreshEvent?) {
        refreshing(true)
        adapter = null
        recyclerView!!.setAdapter(null)
        etSearch!!.setText("")
        sSorter!!.setAdapter(null)
    }

    @Subscribe
    fun onScrollEvent(event: ScrollToEvent) {
        wtf("Onscroll", "pos = " + event.position)
        try {
            layoutManager!!.scrollToPositionWithOffset(event.position, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Subscribe
    fun onProgressEvent(event: ProgressEvent) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout!!.setRefreshing(event.isRefreshing)
        }
    }

    companion object {
        val SAVED_SORT: String = "savedSort"
    }
}