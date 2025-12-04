package com.chicken.retrodoodle.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.chicken.retrodoodle.core.model.PlayerSkin
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val STORE_NAME = "retro_doodle_prefs"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = STORE_NAME)

@Singleton
class DataStoreSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val musicKey = intPreferencesKey("music_volume")
    private val soundKey = intPreferencesKey("sound_volume")
    private val bestScoreKey = intPreferencesKey("best_score")
    private val skinKey = intPreferencesKey("selected_skin")

    override val musicVolumeFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[musicKey] ?: 80
    }

    override val soundVolumeFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[soundKey] ?: 80
    }

    override val bestScoreFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[bestScoreKey] ?: 0
    }

    override val selectedSkinFlow: Flow<PlayerSkin> = context.dataStore.data.map { prefs ->
        PlayerSkin.values().getOrNull(prefs[skinKey] ?: 0) ?: PlayerSkin.Classic
    }

    override suspend fun setMusicVolume(percent: Int) {
        context.dataStore.edit { prefs ->
            prefs[musicKey] = percent.coerceIn(0, 100)
        }
    }

    override suspend fun setSoundVolume(percent: Int) {
        context.dataStore.edit { prefs ->
            prefs[soundKey] = percent.coerceIn(0, 100)
        }
    }

    override suspend fun saveBestScore(score: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[bestScoreKey] ?: 0
            prefs[bestScoreKey] = maxOf(current, score)
        }
    }

    override suspend fun selectSkin(skin: PlayerSkin) {
        context.dataStore.edit { prefs ->
            prefs[skinKey] = skin.ordinal
        }
    }
}
