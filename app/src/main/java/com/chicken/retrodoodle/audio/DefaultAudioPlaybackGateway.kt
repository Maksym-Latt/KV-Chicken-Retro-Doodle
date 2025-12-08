package com.chicken.retrodoodle.audio

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import com.chicken.retrodoodle.R
import com.chicken.retrodoodle.data.settings.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Singleton
class DefaultAudioPlaybackGateway
@Inject
constructor(
    @ApplicationContext private val context: Context,
    settingsGateway: SettingsRepository
) : AudioPlaybackGateway {

    private var musicLevel: Float = 100.toVolume()
    private var effectsLevel: Float = 100.toVolume()

    init {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            settingsGateway.musicLevel.collect { value ->
                musicLevel = value.toVolume()
                updateMusicVolume()
            }
        }

        CoroutineScope(Dispatchers.Main.immediate).launch {
            settingsGateway.effectsLevel.collect { value ->
                effectsLevel = value.toVolume()
                updateSoundVolume()
            }
        }
    }

    private fun updateMusicVolume() {
        mainChannel?.setVolume(musicLevel, musicLevel)
    }

    private fun updateSoundVolume() {
        fxChannels.forEach { (player, cue) ->
            val adjusted = cue.normalizedSoundVolume()
            player.setVolume(adjusted, adjusted)
        }
    }

    private var activeTrack: BgmChannel? = null
    private var mainChannel: MediaPlayer? = null
    private val fxChannels = mutableSetOf<SfxInstance>()

    // ---------------------- PUBLIC API ----------------------

    override fun launchMenuTrack() {
        playMusic(BgmChannel.LobbyLoop)
    }

    override fun launchSessionTrack() {
        playMusic(BgmChannel.ActionLoop)
    }

    override fun haltAllStreams() {
        mainChannel?.run {
            stop()
            release()
        }
        mainChannel = null
        activeTrack = null
    }

    override fun suspendStream() {
        mainChannel?.takeIf { it.isPlaying }?.pause()
    }

    override fun resumeStream() {
        mainChannel?.let { player ->
            activeTrack?.let { track ->
                player.setVolume(track.normalizedMusicVolume(), track.normalizedMusicVolume())
            }
            player.start()
        }
    }

    override fun adjustMusicLevel(percent: Int) {
        musicLevel = percent.toVolume()
        activeTrack?.let { track ->
            val adjusted = track.normalizedMusicVolume()
            mainChannel?.setVolume(adjusted, adjusted)
        }
    }

    override fun adjustEffectsLevel(percent: Int) {
        effectsLevel = percent.toVolume()
        updateSoundVolume()
    }

    override fun triggerVictoryCue() {
        playSound(FxSignal.WinSting)
    }

    override fun triggerFailureCue() {
        playSound(FxSignal.FailSting)
    }


    override fun triggerPickupCue() {
        playSound(FxSignal.LootPing)
    }

    override fun triggerJumpCue() {
        playSound(FxSignal.LiftBurst)
    }

    // ---------------------- INTERNAL IMPLEMENTATION ----------------------

    private fun playMusic(track: BgmChannel) {
        if (activeTrack == track && mainChannel != null) {
            val adjusted = track.normalizedMusicVolume()
            mainChannel?.setVolume(adjusted, adjusted)
            if (mainChannel?.isPlaying != true) {
                mainChannel?.start()
            }
            return
        }

        haltAllStreams()

        mainChannel =
            MediaPlayer.create(context, track.resId).apply {
                isLooping = true
                val adjusted = track.normalizedMusicVolume()
                setVolume(adjusted, adjusted)
                setOnCompletionListener(null)
                start()
            }
        activeTrack = track
    }

    private fun playSound(effect: FxSignal) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val player = MediaPlayer.create(context, effect.resId)
                if (player != null) {
                    player.isLooping = false
                    val adjusted = effect.normalizedSoundVolume()
                    player.setVolume(adjusted, adjusted)
                    player.setOnCompletionListener {
                        it.release()
                        fxChannels.removeIf { instance -> instance.player === it }
                    }
                    player.start()
                    fxChannels.add(SfxInstance(player, effect))
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun Int.toVolume(): Float = (this.coerceIn(0, 100) / 100f).coerceIn(0f, 1f)

    private fun BgmChannel.normalizedMusicVolume(): Float =
        musicLevel.adjustWith(MUSIC_NORMALIZATION[this])

    private fun FxSignal.normalizedSoundVolume(): Float =
        effectsLevel.adjustWith(SOUND_NORMALIZATION[this])

    private fun Float.adjustWith(gain: Float?): Float = (this * (gain ?: 1f)).coerceIn(0f, 1f)

    // ---------------------- ENUMS ПОД res/raw ----------------------

    private enum class BgmChannel(@RawRes val resId: Int) {
        LobbyLoop(R.raw.music_menu_loop),
        ActionLoop(R.raw.music_game_loop)
    }

    private enum class FxSignal(@RawRes val resId: Int) {
        WinSting(R.raw.sfx_victory_fanfare),
        FailSting(R.raw.sfx_victory_fanfare),
        LootPing(R.raw.sfx_chicken_collect_egg),
        LiftBurst(R.raw.sfx_chicken_jump)
    }

    companion object {
        private val MUSIC_NORMALIZATION =
            mapOf(
                BgmChannel.LobbyLoop to 0.8f,
                BgmChannel.ActionLoop to 0.75f,
            )

        private val SOUND_NORMALIZATION =
            mapOf(
                FxSignal.WinSting to 0.9f,
                FxSignal.FailSting to 0.9f,
                FxSignal.LootPing to 0.7f,
                FxSignal.LiftBurst to 2f,
            )

        private data class SfxInstance(val player: MediaPlayer, val cue: FxSignal)
    }
}
