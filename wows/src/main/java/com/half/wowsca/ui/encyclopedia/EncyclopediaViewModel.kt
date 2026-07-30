package com.half.wowsca.ui.encyclopedia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.half.wowsca.data.repository.EncyclopediaRepository
import com.half.wowsca.model.enums.Server
import com.half.wowsca.model.result.InfoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EncyclopediaViewModel @Inject constructor(
    private val encyclopediaRepository: EncyclopediaRepository,
) : ViewModel() {

    private val _encyclopediaState = MutableStateFlow<EncyclopediaState>(EncyclopediaState.Idle)
    val encyclopediaState: StateFlow<EncyclopediaState> = _encyclopediaState.asStateFlow()

    fun loadEncyclopedia(server: Server, language: String = "en") {
        if (_encyclopediaState.value is EncyclopediaState.Loading) return
        _encyclopediaState.value = EncyclopediaState.Loading

        viewModelScope.launch {
            val ships = encyclopediaRepository.getShips(server, language)
            val achievements = encyclopediaRepository.getAchievements(server, language)
            val upgrades = encyclopediaRepository.getUpgrades(server, language)
            val skills = encyclopediaRepository.getCrewSkills(server, language)
            val flags = encyclopediaRepository.getFlags(server, language)

            val result = InfoResult()
            ships.onSuccess { result.shipsInfo = it }
            achievements.onSuccess { result.achievements = it }
            upgrades.onSuccess { result.upgrades = it }
            skills.onSuccess { result.skills = it }
            flags.onSuccess { result.flags = it }

            _encyclopediaState.value = EncyclopediaState.Success(result)
        }
    }
}

sealed interface EncyclopediaState {
    data object Idle : EncyclopediaState
    data object Loading : EncyclopediaState
    data class Success(val infoResult: InfoResult) : EncyclopediaState
    data class Error(val message: String) : EncyclopediaState
}
