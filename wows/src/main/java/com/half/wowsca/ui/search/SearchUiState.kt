package com.half.wowsca.ui.search

import com.half.wowsca.model.Captain

/**
 * Represents the UI state for the player search screen.
 */
sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(val results: List<Captain>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}
