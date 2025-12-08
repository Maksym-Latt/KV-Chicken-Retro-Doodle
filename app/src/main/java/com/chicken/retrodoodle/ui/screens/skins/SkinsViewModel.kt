package com.chicken.retrodoodle.ui.screens.skins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicken.retrodoodle.core.model.PlayerSkin
import com.chicken.retrodoodle.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class SkinsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SkinsUiState())
    val uiState: StateFlow<SkinsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.eggsFlow,
                settingsRepository.activeAppearance,
                settingsRepository.ownedSkinsFlow,
            ) { eggs, selected, ownedSkins ->
                SkinsUiState(
                    eggs = eggs,
                    skins = PlayerSkin.values().map { skin ->
                        skin.toUiModel(ownedSkins.contains(skin))
                    },
                    selectedSkin = selected,
                )
            }.collect { _uiState.value = it }
        }
    }

    fun selectSkin(skin: PlayerSkin) {
        viewModelScope.launch {
            if (_uiState.value.skins.any { it.skin == skin && it.owned }) {
                settingsRepository.selectSkin(skin)
            }
        }
    }

    fun buySkin(skin: PlayerSkin) {
        viewModelScope.launch {
            val target = _uiState.value.skins.firstOrNull { it.skin == skin } ?: return@launch
            if (target.owned) return@launch

            val purchaseSuccessful = settingsRepository.spendEggs(target.price)
            if (purchaseSuccessful) {
                settingsRepository.unlockSkin(skin)
                settingsRepository.selectSkin(skin)
            }
        }
    }
}

data class SkinsUiState(
    val eggs: Int = 0,
    val skins: List<SkinUiModel> = emptyList(),
    val selectedSkin: PlayerSkin = PlayerSkin.Classic,
)

data class SkinUiModel(
    val skin: PlayerSkin,
    val title: String,
    val image: Int,
    val price: Int,
    val owned: Boolean,
)

private fun PlayerSkin.toUiModel(owned: Boolean) = SkinUiModel(
    skin = this,
    title = title,
    image = image,
    price = price,
    owned = owned,
)
