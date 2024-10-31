package com.half.wowsca.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.getSelectedId
import com.half.wowsca.CAApp.Companion.getServerType
import com.half.wowsca.CAApp.Companion.isDarkTheme
import com.half.wowsca.CAApp.Companion.setSelectedId
import com.half.wowsca.CAApp.Companion.setServerType
import com.half.wowsca.R
import com.half.wowsca.alerts.Alert.generalNoInternetDialogAlert
import com.half.wowsca.backend.SearchTask
import com.half.wowsca.managers.CaptainManager.createCapIdStr
import com.half.wowsca.managers.CaptainManager.deleteTemp
import com.half.wowsca.managers.CaptainManager.getCaptains
import com.half.wowsca.managers.CaptainManager.removeCaptain
import com.half.wowsca.managers.CaptainManager.saveCaptain
import com.half.wowsca.managers.CompareManager.addCaptain
import com.half.wowsca.managers.CompareManager.clear
import com.half.wowsca.managers.CompareManager.getCaptains
import com.half.wowsca.managers.CompareManager.isAlreadyThere
import com.half.wowsca.managers.CompareManager.removeCaptain
import com.half.wowsca.managers.CompareManager.size
import com.half.wowsca.model.AddRemoveEvent
import com.half.wowsca.model.Captain
import com.half.wowsca.model.enums.Server
import com.half.wowsca.model.queries.SearchQuery
import com.half.wowsca.model.result.SearchResults
import com.half.wowsca.ui.UIUtils.createBookmarkingDialogIfNeeded
import com.half.wowsca.ui.adapter.CompareAdapter
import com.half.wowsca.ui.adapter.SearchAdapter
import com.half.wowsca.ui.compare.CompareActivity
import com.half.wowsca.ui.viewcaptain.ViewCaptainActivity
import com.utilities.Utils.hasInternetConnection
import com.utilities.logging.Dlog.wtf
import com.utilities.views.SwipeBackLayout
import org.greenrobot.eventbus.Subscribe
import java.util.Locale

class SearchActivity : CABaseActivity() {

    private var etSearch: EditText? = null
    private var delete: View? = null

    private var sServers: Spinner? = null

    private var progress: View? = null

    private var listView: ListView? = null

    private var tvError: TextView? = null

    private var searching = false

    private var bCompare: Button? = null
    private var tvCompare: TextView? = null

    private var compareAdapter: CompareAdapter? = null

    private var savedSearch: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        bindView()
        if (savedInstanceState != null) {
            savedSearch = savedInstanceState.getString("search")
        }
    }

    private fun bindView() {
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar?
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        etSearch = findViewById<View>(R.id.search_et) as EditText?
        delete = findViewById<View>(R.id.search_et_delete)
        sServers = findViewById<View>(R.id.search_server_spinner) as Spinner?
        listView = findViewById<View>(R.id.search_listview) as ListView?
        progress = findViewById<View>(R.id.progressBar)
        tvError = findViewById<View>(R.id.search_error_text) as TextView?

        bCompare = findViewById<View>(R.id.search_compare_button) as Button?
        tvCompare = findViewById<View>(R.id.search_compare_text) as TextView?
        swipeBackLayout!!.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("search", etSearch!!.text.toString())
    }

    override fun onResume() {
        super.onResume()
        eventBus.register(this)
        initView()
        deleteTemp(applicationContext)
    }

    override fun onPause() {
        super.onPause()
        eventBus.unregister(this)
    }

    private fun initView() {
        initCompare()

        initSearch()

        initServerSpinner()

        initListOnClick()

        initAutoSearchOnRotate()

        autoPlaceSavedCaptains()
    }

    private fun initAutoSearchOnRotate() {
        if (!TextUtils.isEmpty(savedSearch)) {
            etSearch!!.setText(savedSearch)
            search()
        }
        if (listView!!.adapter != null) {
            try {
                val adapter1 = listView!!.adapter as SearchAdapter
                adapter1.notifyDataSetChanged()
            } catch (e: Exception) {
            }
        }
    }

    private fun autoPlaceSavedCaptains() {
        if (etSearch!!.text.toString().trim { it <= ' ' }.length == 0) {
            val captains: MutableList<Captain?> = ArrayList()
            val savedCaptains: Map<String?, Captain?>? = getCaptains(
                applicationContext
            )
            val selectedId = getSelectedId(applicationContext)
            for (c in savedCaptains!!.values) {
                if (createCapIdStr(c!!.server, c.id) != selectedId) {
                    captains.add(c)
                }
            }
            if (captains.size > 0) {
                val defaultSearch =
                    SearchAdapter(applicationContext, R.layout.list_search, captains)
                listView!!.adapter = defaultSearch
            }
        }
    }

    private fun initCompare() {
        bCompare!!.isEnabled = size() > 1
        refreshCompareSection()
        bCompare!!.setOnClickListener {
            val act: Activity = this@SearchActivity
            val builder = AlertDialog.Builder(act)
            builder.setTitle(getString(R.string.compare_list))
            compareAdapter = CompareAdapter(act, R.layout.list_compare, getCaptains())
            builder.setAdapter(compareAdapter) { dialog, which ->
                val c = compareAdapter!!.getItem(which)
                removeCaptain(c!!.server, c.id)
                compareAdapter!!.remove(c)
                compareAdapter!!.notifyDataSetChanged()
                refreshCompareSection()
            }
            builder.setPositiveButton(getString(R.string.compare)) { dialog, which ->
                if (size() > 1) {
                    val i = Intent(applicationContext, CompareActivity::class.java)
                    startActivity(i)
                } else {
                    Toast.makeText(
                        applicationContext,
                        R.string.not_enough_captains,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()
            }
            builder.setNegativeButton(getString(R.string.clear_list)) { dialog, which ->
                clear()
                refreshCompareSection()
                dialog.dismiss()
            }
            builder.setNeutralButton(getString(R.string.dismiss)) { dialog, which -> dialog.dismiss() }
            builder.show()
        }
    }

    private fun initSearch() {
        etSearch!!.setOnEditorActionListener { textView, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_SEARCH && !searching) {
                search()
            }
            false
        }

        etSearch!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER) && !searching) {
                search()
                return@OnKeyListener true
            }
            false
        })
        delete!!.setOnClickListener { etSearch!!.setText("") }
        etSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (etSearch!!.text.toString().length > 0) {
                    delete!!.visibility = View.VISIBLE
                } else {
                    delete!!.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun initListOnClick() {
        listView!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val adapter = listView!!.adapter as SearchAdapter
            if (adapter != null) {
                val cap = adapter.getItem(position)
                val i = Intent(applicationContext, ViewCaptainActivity::class.java)
                i.putExtra(ViewCaptainActivity.EXTRA_ID, cap!!.id)
                i.putExtra(ViewCaptainActivity.EXTRA_NAME, cap.name)
                i.putExtra(ViewCaptainActivity.EXTRA_SERVER, cap.server.toString())
                startActivity(i)
            } else {
            }
        }
        // on long click to add or remove
        listView!!.onItemLongClickListener = OnItemLongClickListener { parent, view, position, id ->
            val adapter = listView!!.adapter as SearchAdapter
            if (adapter != null) {
                val cap = adapter.getItem(position)
                val bSize = size()
                if (!isAlreadyThere(cap!!.server, cap.id)) {
                    val wasAdded = addCaptain(cap, false)
                    if (bSize == 0) {
                        val selectedId = getSelectedId(view.context)
                        if (!TextUtils.isEmpty(selectedId)) {
                            val captains: Map<String?, Captain?>? = getCaptains(view.context)
                            val selected = captains!![selectedId]
                            if (selected != null && !isAlreadyThere(selected.server, selected.id)) {
                                val act: Activity = this@SearchActivity
                                val builder = AlertDialog.Builder(act)
                                builder.setTitle(getString(R.string.compare_add_title))
                                builder.setMessage(getString(R.string.compare_dialog_message))
                                builder.setPositiveButton(R.string.yes) { dialog, which ->
                                    val selectedId = getSelectedId(
                                        applicationContext
                                    )
                                    val captains: Map<String?, Captain?>? = getCaptains(
                                        applicationContext
                                    )
                                    val selected = captains!![selectedId]
                                    addCaptain(selected!!, true)
                                    refreshCompareSection()
                                    dialog.dismiss()
                                }
                                builder.setNegativeButton(R.string.no) { dialog, which -> dialog.dismiss() }
                                builder.show()
                            } else {
                                setSelectedId(view.context, null)
                            }
                        }
                    }
                    wtf("SearchActivity", "size = $bSize wasAdded = $wasAdded")
                    if (wasAdded) {
                        refreshCompareSection()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            R.string.compare_max_message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    removeCaptain(cap.server, cap.id)
                    refreshCompareSection()
                }
            } else {
            }
            true
        }
    }

    private fun initServerSpinner() {
        val servers: MutableList<String> = ArrayList()
        val current = getServerType(applicationContext)
        servers.add(current.toString().uppercase(Locale.getDefault()))
        for (s in Server.entries) {
            if (current.ordinal != s.ordinal) {
                servers.add(s.toString().uppercase(Locale.getDefault()))
            }
        }
        val adapter = ArrayAdapter(applicationContext, R.layout.ca_spinner_item_trans, servers)
        adapter.setDropDownViewResource(if (!isDarkTheme(sServers!!.context)) R.layout.ca_spinner_item else R.layout.ca_spinner_item_dark)
        sServers!!.adapter = adapter
        sServers!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val servers: MutableList<Server> = ArrayList()
                val current = getServerType(applicationContext)
                servers.add(current)
                for (s in Server.entries) {
                    if (current.ordinal != s.ordinal) {
                        servers.add(s)
                    }
                }
                val server = servers[position]
                if (server != current) {
                    setServerType(applicationContext, server)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun refreshCompareSection() {
        val sb = StringBuilder()
        if (size() == 3) {
            for (i in 0..2) {
                val c = getCaptains()[i]
                sb.append(c.name)
                if (i == 0) sb.append(", ")
                else if (i == 1) sb.append(" and ")
            }
            sb.append(getString(R.string.compare_bottom_text_max))
            bCompare!!.isEnabled = true
            bCompare!!.isEnabled = true
        } else if (size() == 2) {
            for (i in 0..1) {
                val c = getCaptains()[i]
                sb.append(c.name)
                if (i == 0) sb.append(" & ")
            }
            sb.append(getString(R.string.compare_bottom_text_middle))
            bCompare!!.isEnabled = true
        } else if (size() == 1) {
            for (c in getCaptains()) {
                sb.append(c.name)
            }
            sb.append(getString(R.string.compare_bottom_text_single))
            bCompare!!.isEnabled = false
        } else {
            sb.append(getString(R.string.search_compare_default_text))
            bCompare!!.isEnabled = false
        }
        tvCompare!!.text = sb.toString()
    }

    private fun search() {
        val connected = hasInternetConnection(this)
        if (connected) {
            val searchTerm = etSearch!!.text.toString()
            if (!TextUtils.isEmpty(searchTerm)) {
                searching = true
                closeKeyboard(this)
                progress!!.visibility = View.VISIBLE
                listView!!.adapter = null
                tvError!!.visibility = View.GONE
                val query = SearchQuery()
                query.server = getServerType(applicationContext)
                query.search = searchTerm
                val task = SearchTask()
                task.execute(query)
            } else {
                Toast.makeText(applicationContext, R.string.no_text_error, Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            generalNoInternetDialogAlert(
                this,
                getString(R.string.no_internet_title),
                getString(R.string.no_internet_message),
                getString(R.string.no_internet_neutral_text)
            )
        }
    }

    @Subscribe
    fun onSearchRecieved(result: SearchResults?) {
        listView!!.post {
            progress!!.visibility = View.GONE
            searching = false
            if (result != null) {
                if (result.captains != null) {
                    if (result.captains.size > 0) {
                        val adapter =
                            SearchAdapter(applicationContext, R.layout.list_search, result.captains)
                        listView!!.adapter = adapter
                        tvError!!.visibility = View.GONE
                    } else {
                        tvError!!.visibility = View.VISIBLE
                    }
                } else {
                    tvError!!.visibility = View.VISIBLE
                }
            } else {
                tvError!!.visibility = View.VISIBLE
            }
        }
    }

    @Subscribe
    fun onAddRemove(event: AddRemoveEvent) {
        if (!event.isRemove) {
            createBookmarkingDialogIfNeeded(this, event.captain!!)
            saveCaptain(applicationContext, event.captain)
        } else {
            removeCaptain(
                applicationContext, createCapIdStr(
                    event.captain!!.server, event.captain!!.id
                )
            )
        }
    }

    fun closeKeyboard(activity: Activity) {
        val imm = activity
            .getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearch!!.windowToken, 0)
    }
}
