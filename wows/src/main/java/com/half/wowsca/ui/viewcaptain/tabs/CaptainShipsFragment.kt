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
class CaptainShipsFragment : CAFragment() {
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
    ): View {
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
            captain = (activity as ICaptain?)!!.getCaptain(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (captain != null && captain.ships != null) {
            refreshing(false)
            sSorter!!.isEnabled = true
            etSearch!!.isEnabled = true

            if (recyclerView!!.adapter == null) {
                recyclerView!!.setHasFixedSize(false)

                layoutManager =
                    GridLayoutManager(context, resources.getInteger(R.integer.ship_rows))
                layoutManager!!.setOrientation(GridLayoutManager.VERTICAL)
                recyclerView!!.setLayoutManager(layoutManager)

                adapter = context?.let { ShipsAdapter(captain.ships, it) }
                recyclerView!!.setAdapter(adapter)
            } else if (adapter != null) {
                val pref: Prefs = Prefs(recyclerView!!.context)
                adapter!!.notifyDataSetChanged()
            }

            if (recyclerView!!.adapter != null && CAApp.lastShipPos != 0) {
                recyclerView!!.scrollToPosition(CAApp.lastShipPos)
            }
            setUpSearching()
            setUpSorting()
        } else {
            if (captain != null && captain.ships == null) refreshing(true)
            sSorter!!.isEnabled = false
            etSearch!!.isEnabled = false
        }
    }

    private fun setUpSorting() {
        if (sSorter!!.adapter == null) {
            spinnerCheck = false
            val sortAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
                requireActivity(),
                R.array.ship_sorting,
                R.layout.ca_spinner_item_trans
            )
            sortAdapter.setDropDownViewResource(if (!isDarkTheme(sSorter!!.context)) R.layout.ca_spinner_item else R.layout.ca_spinner_item_dark)
            sSorter!!.adapter = sortAdapter
            refreshSortingChoice(true)
            sSorter!!.isEnabled = true
            sSorter!!.isEnabled = true
            sSorter!!.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                    spinnerCheck = true
                    return false
                }
            })

            sSorter!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                        val prefs: Prefs = Prefs(context)
                        prefs.setString(SAVED_SORT, sortType)
                        try {
                            if (adapter == null) adapter =
                                recyclerView!!.adapter as ShipsAdapter?
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if (adapter != null) {
                            adapter!!.sort(sortType)
                            CAApp.lastShipPos = 0
                            sSorter!!.isEnabled = false
                        } else {
                            Toast.makeText(
                                context,
                                "Oops something went wrong. Refresh the view to fix.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        } else {
            refreshSortingChoice(false)
        }
    }

    private fun refreshSortingChoice(clearCheck: Boolean) {
        val prefs: Prefs = Prefs(context)
        val savedSort: String? = prefs.getString(SAVED_SORT, "")
        if (!TextUtils.isEmpty(savedSort)) {
            val sortTypes: Array<String> = resources.getStringArray(R.array.ship_sorting)
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
                if (!TextUtils.isEmpty(etSearch!!.text.toString())) {
                    sSorter!!.isEnabled = false
                    delete!!.visibility = View.VISIBLE
                } else {
                    sSorter!!.isEnabled = true
                    delete!!.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        if (!TextUtils.isEmpty(etSearch!!.text.toString())) {
            sSorter!!.isEnabled = false
            delete!!.visibility = View.VISIBLE
        } else {
            sSorter!!.isEnabled = true
            delete!!.visibility = View.GONE
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
                sSorter!!.isEnabled = true
            }
        })
    }

    @Subscribe
    fun onRefresh(event: RefreshEvent?) {
        refreshing(true)
        adapter = null
        recyclerView!!.setAdapter(null)
        etSearch!!.setText("")
        sSorter!!.adapter = null
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
            mSwipeRefreshLayout!!.isRefreshing = event.isRefreshing
        }
    }

    companion object {
        val SAVED_SORT: String = "savedSort"
    }
}