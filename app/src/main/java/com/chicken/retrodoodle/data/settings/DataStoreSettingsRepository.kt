package com.chicken.retrodoodle.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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
    private val eggsKey = intPreferencesKey("eggs_balance")

    override val musicLevel: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[musicKey] ?: 80
    }

    override val effectsLevel: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[soundKey] ?: 80
    }

    override val topRecord: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[bestScoreKey] ?: 0
    }

    override val activeAppearance: Flow<PlayerSkin> = context.dataStore.data.map { prefs ->
        PlayerSkin.values().getOrNull(prefs[skinKey] ?: 0) ?: PlayerSkin.Classic
    }

    override val eggsFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[eggsKey] ?: 0
    }

    override val ownedSkinsFlow: Flow<Set<PlayerSkin>> = context.dataStore.data.map { prefs ->
        PlayerSkin.values().filter { skin ->
            prefs[ownedKey(skin)] ?: (skin == PlayerSkin.Classic)
        }.toSet()
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

    override suspend fun addEggs(amount: Int) {
        if (amount <= 0) return
        context.dataStore.edit { prefs ->
            val current = prefs[eggsKey] ?: 0
            prefs[eggsKey] = (current + amount).coerceAtLeast(0)
        }
    }

    override suspend fun spendEggs(amount: Int): Boolean {
        if (amount <= 0) return true
        var success = false
        context.dataStore.edit { prefs ->
            val current = prefs[eggsKey] ?: 0
            if (current >= amount) {
                prefs[eggsKey] = current - amount
                success = true
            }
        }
        return success
    }

    override suspend fun unlockSkin(skin: PlayerSkin) {
        context.dataStore.edit { prefs ->
            prefs[ownedKey(skin)] = true
        }
    }

    private fun ownedKey(skin: PlayerSkin) = booleanPreferencesKey("skin_owned_${skin.name}")
}
