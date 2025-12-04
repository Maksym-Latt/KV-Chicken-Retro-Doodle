package com.chicken.retrodoodle.data.settings

import com.chicken.retrodoodle.core.model.PlayerSkin
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val musicVolumeFlow: Flow<Int>
    val soundVolumeFlow: Flow<Int>
    val bestScoreFlow: Flow<Int>
    val selectedSkinFlow: Flow<PlayerSkin>

    suspend fun setMusicVolume(percent: Int)
    suspend fun setSoundVolume(percent: Int)
    suspend fun saveBestScore(score: Int)
    suspend fun selectSkin(skin: PlayerSkin)
}
