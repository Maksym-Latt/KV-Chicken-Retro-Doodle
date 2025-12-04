package com.chicken.retrodoodle.ui.screens.skins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicken.retrodoodle.core.model.PlayerSkin
import com.chicken.retrodoodle.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SkinsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _selected = MutableStateFlow(PlayerSkin.Classic)
    val selected: StateFlow<PlayerSkin> = _selected

    init {
        viewModelScope.launch {
            settingsRepository.selectedSkinFlow.collect { skin ->
                _selected.value = skin
            }
        }
    }

    fun selectSkin(skin: PlayerSkin) {
        viewModelScope.launch {
            settingsRepository.selectSkin(skin)
        }
    }
}
