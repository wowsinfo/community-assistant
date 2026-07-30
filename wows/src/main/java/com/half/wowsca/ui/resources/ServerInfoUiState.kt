package com.half.wowsca.ui.resources

import com.half.wowsca.model.result.ServerResult

sealed interface ServerInfoUiState {
    data object Idle : ServerInfoUiState
    data object Loading : ServerInfoUiState
    data class Success(val serverResult: ServerResult) : ServerInfoUiState
    data class Error(val message: String) : ServerInfoUiState
}
