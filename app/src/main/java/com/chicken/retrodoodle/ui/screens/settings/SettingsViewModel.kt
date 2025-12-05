package com.chicken.retrodoodle.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicken.retrodoodle.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.musicVolumeFlow,
        settingsRepository.soundVolumeFlow
    ) { music, sound ->
        SettingsUiState(
            musicEnabled = music > 0,
            soundsEnabled = sound > 0,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    fun toggleMusic() {
        val enable = !uiState.value.musicEnabled
        viewModelScope.launch {
            settingsRepository.setMusicVolume(if (enable) 100 else 0)
        }
    }

    fun toggleSounds() {
        val enable = !uiState.value.soundsEnabled
        viewModelScope.launch {
            settingsRepository.setSoundVolume(if (enable) 100 else 0)
        }
    }
}

data class SettingsUiState(
    val musicEnabled: Boolean = true,
    val soundsEnabled: Boolean = true,
)
