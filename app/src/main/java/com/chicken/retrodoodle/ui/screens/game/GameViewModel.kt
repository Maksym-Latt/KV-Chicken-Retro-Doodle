package com.chicken.retrodoodle.ui.screens.game

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicken.retrodoodle.core.model.Collectible
import com.chicken.retrodoodle.core.model.Enemy
import com.chicken.retrodoodle.core.model.GameConfig
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

    fun startGame(worldW: Float, worldH: Float) {

        val startPlatformY = worldH - 200f

        val basePlatform = Platform(
            id = 0,
            position = Offset(worldW / 2f, startPlatformY),
            width = GameScaling.platformWidth,
            height = GameScaling.platformHeight,
            type = PlatformType.Static
        )

        val platforms = mutableListOf(basePlatform)

        val playerSize = GameScaling.playerSize
        val playerStartY = startPlatformY - basePlatform.height / 2f - playerSize / 2f

        var y = startPlatformY
        var id = 1
        while (y > startPlatformY - 3500f) {
            y -= (150..230).random()
            val typeRoll = Random.nextFloat()
            val type = when {
                typeRoll < 0.15f -> PlatformType.Moving
                typeRoll < 0.35f -> PlatformType.Cracked
                else -> PlatformType.Static
            }
            val dir = if (Random.nextBoolean()) 1f else -1f
            platforms += Platform(
                id = id++,
                position = Offset(
                    x = (GameScaling.platformWidth.toInt()..(worldW - GameScaling.platformWidth).toInt()).random().toFloat(),
                    y = y
                ),
                width = GameScaling.platformWidth,
                height = GameScaling.platformHeight,
                type = type,
                direction = dir,
            )
        }

        _ui.value = GameUiState(
            status = GameStatus.Playing,
            worldWidth = worldW,
            worldHeight = worldH,
            cameraOffset = 0f,
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

        val updatedPlatforms = s.platforms.map { pl ->
            if (pl.type == PlatformType.Moving && !pl.isBroken) {
                val candidateX = pl.position.x + pl.direction * GameConfig.movingPlatformSpeed * dt
                val halfWidth = pl.width / 2f
                val (newX, newDir) = when {
                    candidateX - halfWidth < 0f -> halfWidth to 1f
                    candidateX + halfWidth > s.worldWidth -> s.worldWidth - halfWidth to -1f
                    else -> candidateX to pl.direction
                }
                pl.copy(position = pl.position.copy(x = newX), direction = newDir)
            } else pl
        }

        val vx = (p.velocity.x + (-s.tiltX * GameConfig.tiltAcceleration * dt))
            .coerceIn(-GameConfig.maxHorizontalSpeed, GameConfig.maxHorizontalSpeed)

        val vy = p.velocity.y + GameConfig.gravity * dt

        var pos = p.position + Offset(vx * dt, vy * dt)
        var vel = Offset(vx, vy)

        if (pos.x < -50f) pos = pos.copy(x = s.worldWidth + pos.x)
        if (pos.x > s.worldWidth) pos = pos.copy(x = pos.x - s.worldWidth)

        val playerHalf = GameScaling.playerSize / 2f
        val collisionHalfWidth = GameScaling.playerCollisionRadius
        val platformBuffer = GameScaling.platformCollisionBuffer
        val platformsAfterCollision = updatedPlatforms.toMutableList()

        updatedPlatforms.forEachIndexed { index, pl ->
            if (pl.isBroken) return@forEachIndexed
            val top = pl.position.y - pl.height / 2f
            val previousBottom = p.position.y + playerHalf
            val currentBottom = pos.y + playerHalf
            val horizontalOverlap = abs(pos.x - pl.position.x) <= pl.width / 2f + collisionHalfWidth - platformBuffer

            if (previousBottom <= top && currentBottom >= top && vel.y > 0 && horizontalOverlap) {
                vel = vel.copy(y = -GameConfig.jumpForce)
                pos = pos.copy(y = top - playerHalf)

                if (pl.type == PlatformType.Cracked) {
                    platformsAfterCollision[index] = pl.copy(isBroken = true)
                }
            }
        }

        val alivePlatforms = platformsAfterCollision.filterNot { it.type == PlatformType.Cracked && it.isBroken }

        var cam = s.cameraOffset
        val playerScreenY = pos.y - cam

        if (playerScreenY < s.worldHeight * 0.4f) {
            cam = pos.y - s.worldHeight * 0.4f
        }

        if (playerScreenY > s.worldHeight + 100f) {
            endGame(); return
        }

        val highest = min(s.highestY, pos.y)

        _ui.value = s.copy(
            player = p.copy(position = pos, velocity = vel),
            platforms = alivePlatforms,
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
