package com.half.wowsca.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.half.wowsca.data.repository.CaptainRepository
import com.half.wowsca.model.enums.Server
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val captainRepository: CaptainRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    /**
     * Searches for players by name on the given server.
     * Updates [uiState] with loading/success/error states.
     */
    fun search(query: String, server: Server) {
        if (query.isBlank()) return

        _uiState.value = SearchUiState.Loading

        viewModelScope.launch {
            val result = captainRepository.searchPlayers(query, server)
            _uiState.value = result.fold(
                onSuccess = { captains ->
                    if (captains.isEmpty()) {
                        SearchUiState.Error("No players found")
                    } else {
                        SearchUiState.Success(captains)
                    }
                },
                onFailure = { error ->
                    SearchUiState.Error(error.message ?: "Search failed")
                },
            )
        }
    }

    /**
     * Resets the UI state to idle (e.g., when clearing search).
     */
    fun resetState() {
        _uiState.value = SearchUiState.Idle
    }
}
