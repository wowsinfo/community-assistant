package com.half.wowsca.ui.encyclopedia

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.half.wowsca.backend.GetNeededInfoTask
import com.half.wowsca.model.enums.Server
import com.half.wowsca.model.queries.InfoQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.os.AsyncTask
import javax.inject.Inject

@HiltViewModel
class EncyclopediaViewModel @Inject constructor() : ViewModel() {

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadEncyclopedia(context: Context, server: Server) {
        if (_isLoading.value) return
        _isLoading.value = true

        val query = InfoQuery()
        query.server = server

        val task = GetNeededInfoTask()
        task.ctx = context
        task.onResult = { _ ->
            _isLoading.value = false
            _isLoaded.value = true
        }
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query)
    }
}
