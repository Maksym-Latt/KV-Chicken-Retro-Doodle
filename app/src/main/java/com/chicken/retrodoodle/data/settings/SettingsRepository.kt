package com.chicken.retrodoodle.data.settings

import com.chicken.retrodoodle.core.model.PlayerSkin
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val musicVolumeFlow: Flow<Int>
    val soundVolumeFlow: Flow<Int>
    val bestScoreFlow: Flow<Int>
    val selectedSkinFlow: Flow<PlayerSkin>
    val eggsFlow: Flow<Int>
    val ownedSkinsFlow: Flow<Set<PlayerSkin>>

    suspend fun setMusicVolume(percent: Int)
    suspend fun setSoundVolume(percent: Int)
    suspend fun saveBestScore(score: Int)
    suspend fun selectSkin(skin: PlayerSkin)
    suspend fun addEggs(amount: Int)
    suspend fun spendEggs(amount: Int): Boolean
    suspend fun unlockSkin(skin: PlayerSkin)
}
