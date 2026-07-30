package com.half.wowsca.ui.resources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.half.wowsca.data.repository.ServerInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServerInfoViewModel @Inject constructor(
    private val serverInfoRepository: ServerInfoRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ServerInfoUiState>(ServerInfoUiState.Idle)
    val uiState: StateFlow<ServerInfoUiState> = _uiState.asStateFlow()

    fun loadServerInfo() {
        _uiState.value = ServerInfoUiState.Loading
        viewModelScope.launch {
            val result = serverInfoRepository.getServerInfo()
            _uiState.value = result.fold(
                onSuccess = { ServerInfoUiState.Success(it) },
                onFailure = { ServerInfoUiState.Error(it.message ?: "Failed to load server info") },
            )
        }
    }
}
