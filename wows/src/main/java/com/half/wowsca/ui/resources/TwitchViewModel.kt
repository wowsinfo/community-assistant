package com.half.wowsca.ui.resources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.half.wowsca.data.repository.TwitchRepository
import com.half.wowsca.model.TwitchObj
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TwitchViewModel @Inject constructor(
    private val twitchRepository: TwitchRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TwitchUiState>(TwitchUiState.Idle)
    val uiState: StateFlow<TwitchUiState> = _uiState.asStateFlow()

    fun loadTwitchInfo(channelNames: List<String>) {
        _uiState.value = TwitchUiState.Loading
        viewModelScope.launch {
            val results = channelNames.map { name ->
                twitchRepository.getTwitchInfo(name)
            }
            _uiState.value = TwitchUiState.Success(results)
        }
    }
}

sealed interface TwitchUiState {
    data object Idle : TwitchUiState
    data object Loading : TwitchUiState
    data class Success(val streamers: List<TwitchObj>) : TwitchUiState
    data class Error(val message: String) : TwitchUiState
}
