package com.chicken.retrodoodle.ui.screens.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicken.retrodoodle.core.model.PlayerSkin
import com.chicken.retrodoodle.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MenuViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
) : ViewModel() {

    val uiState: StateFlow<MenuUiState> = settingsRepository.selectedSkinFlow
        .map { skin -> MenuUiState(selectedSkin = skin) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MenuUiState(),
        )
}

data class MenuUiState(
    val selectedSkin: PlayerSkin = PlayerSkin.Classic,
)
