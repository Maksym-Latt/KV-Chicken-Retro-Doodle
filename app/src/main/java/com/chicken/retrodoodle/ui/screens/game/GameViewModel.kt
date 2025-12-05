package com.chicken.retrodoodle.ui.screens.game

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicken.retrodoodle.core.model.Collectible
import com.chicken.retrodoodle.core.model.Enemy
import com.chicken.retrodoodle.core.model.GameStatus
import com.chicken.retrodoodle.core.model.GameScaling
import com.chicken.retrodoodle.core.model.Platform
import com.chicken.retrodoodle.core.model.PlatformType
import com.chicken.retrodoodle.core.model.Player
import com.chicken.retrodoodle.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class GameViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _ui = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _ui.asStateFlow()

    private val gravity = 1800f       // ускорение вниз
    private val jumpForce = 900f      // сила прыжка (как Doodle Jump)
    private val tiltAccel = 900f      // реакция на наклон — резкая
    private val maxSpeedX = 550f      // как Doodle Jump

    fun startGame(worldW: Float, worldH: Float) {

        val startPlatformY = worldH - 200f  // Платформа не слишком низко

        val basePlatform = Platform(
            id = 0,
            position = Offset(worldW / 2f, startPlatformY),
            width = 140f,
            height = 36f,
            type = PlatformType.Static
        )

        val platforms = mutableListOf(basePlatform)

        // Игрок должен стоять ПРЯМО НА платформе
        val playerSize = 64f
        val playerStartY = startPlatformY - basePlatform.height / 2f - playerSize / 2f

        // Генерация остальных платформ
        var y = startPlatformY
        var id = 1
        while (y > startPlatformY - 3500f) {
            y -= (150..230).random()
            platforms += Platform(
                id = id++,
                position = Offset(
                    x = (30..(worldW - 30).toInt()).random().toFloat(),
                    y = y
                ),
                width = 140f,
                height = 36f,
                type = PlatformType.Static
            )
        }

        _ui.value = GameUiState(
            status = GameStatus.Playing,
            worldWidth = worldW,
            worldHeight = worldH,
            cameraOffset = 0f,  // камера ровно снизу
            highestY = playerStartY,
            platforms = platforms,
            player = Player(
                position = Offset(worldW / 2f, playerStartY),
                velocity = Offset(0f, 0f),
                skin = _ui.value.player.skin
            )
        )
    }


    fun updateTilt(tilt: Float) {
        _ui.value = _ui.value.copy(tiltX = tilt)
    }

    fun updateFrame(dt: Float) {
        var s = _ui.value
        if (s.status != GameStatus.Playing) return

        var p = s.player

        // ——— ДВИЖЕНИЕ ПО X (резкое как Doodle Jump)
        val vx = (p.velocity.x + (-s.tiltX * tiltAccel * dt))
            .coerceIn(-maxSpeedX, maxSpeedX)

        // ——— ДВИЖЕНИЕ ПО Y
        val vy = p.velocity.y + gravity * dt

        var pos = p.position + Offset(vx * dt, vy * dt)
        var vel = Offset(vx, vy)

        // ——— ПЕТЛЯ ПО X
        if (pos.x < -50f) pos = pos.copy(x = s.worldWidth + pos.x)
        if (pos.x > s.worldWidth) pos = pos.copy(x = pos.x - s.worldWidth)

        // ——— СТОЛКНОВЕНИЕ С ПЛАТФОРМАМИ (как Doodle Jump)
        s.platforms.forEach { pl ->
            val top = pl.position.y - pl.height / 2f
            val previousY = p.position.y + 32
            val currentY = pos.y + 32

            if (previousY <= top && currentY >= top && vel.y > 0 &&
                kotlin.math.abs(pos.x - pl.position.x) < pl.width / 2 + 25
            ) {
                vel = vel.copy(y = -jumpForce)
                pos = pos.copy(y = top - 32f)
            }
        }

        // ——— КАМЕРА (самое важное!)
        var cam = s.cameraOffset

        // пока игрок НИЖЕ половины — камера не двигается
        val playerScreenY = pos.y - cam

        if (playerScreenY < s.worldHeight * 0.4f) {
            // двигаем камеру вверх
            cam = pos.y - s.worldHeight * 0.4f
        }

        // ——— если игрок упал ниже экрана → конец игры
        if (playerScreenY > s.worldHeight + 100f) {
            endGame(); return
        }

        // ——— обновляем лучший Y
        val highest = min(s.highestY, pos.y)

        _ui.value = s.copy(
            player = p.copy(position = pos, velocity = vel),
            cameraOffset = cam,
            highestY = highest,
            score = ((s.worldHeight - highest) / 10).toInt()
        )
    }

    private fun endGame() {
        val s = _ui.value
        _ui.value = s.copy(status = GameStatus.GameOver)
    }

    fun pauseGame() {
        if (_ui.value.status == GameStatus.Playing)
            _ui.value = _ui.value.copy(status = GameStatus.Paused)
    }

    fun resumeGame() {
        if (_ui.value.status == GameStatus.Paused)
            _ui.value = _ui.value.copy(status = GameStatus.Playing)
    }
}


private operator fun Offset.times(value: Float): Offset = Offset(x * value, y * value)

// Ui state for the Doodle Jump inspired gameplay loop
data class GameUiState(
    val status: GameStatus = GameStatus.Idle,
    val player: Player = Player(),
    val platforms: List<Platform> = emptyList(),
    val enemies: List<Enemy> = emptyList(),
    val collectibles: List<Collectible> = emptyList(),
    val score: Int = 0,
    val bestScore: Int = 0,
    val eggs: Int = 0,
    val highestY: Float = Float.MAX_VALUE,
    val tiltX: Float = 0f,
    val cameraOffset: Float = 0f,
    val worldWidth: Float = 0f,
    val worldHeight: Float = 0f,
)
