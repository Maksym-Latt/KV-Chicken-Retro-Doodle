package com.chicken.retrodoodle.ui.screens.game

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class GameViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private var nextPlatformId = 0
    private var nextEnemyId = 0
    private val spacingRange = 150..230
    private val generationDepth = 3200f
    private val offscreenCullBuffer = 200f
    private val enemySpawnChance = 0.2f

    private val _ui = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _ui.asStateFlow()

    fun startGame(worldW: Float, worldH: Float) {
        nextPlatformId = 0
        nextEnemyId = 0

        val startPlatformY = worldH - 200f

        val basePlatform = Platform(
            id = nextPlatformId++,
            position = Offset(worldW / 2f, startPlatformY),
            width = GameScaling.platformWidth,
            height = GameScaling.platformHeight,
            type = PlatformType.Static
        )

        val platforms = mutableListOf(basePlatform)
        val enemies = mutableListOf<Enemy>()

        val playerSize = GameScaling.playerSize
        val playerStartY = startPlatformY - basePlatform.height / 2f - playerSize / 2f

        generatePlatforms(platforms, enemies, worldW, startPlatformY)

        _ui.value = GameUiState(
            status = GameStatus.Playing,
            worldWidth = worldW,
            worldHeight = worldH,
            cameraOffset = 0f,
            highestY = playerStartY,
            platforms = platforms,
            enemies = enemies,
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

        val updatedEnemies = s.enemies.map { enemy ->
            val candidateX = enemy.position.x + enemy.direction * GameConfig.bugSpeed * dt
            val halfWidth = GameScaling.enemyWidth / 2f
            val (newX, newDir) = when {
                candidateX - halfWidth < 0f -> halfWidth to 1f
                candidateX + halfWidth > s.worldWidth -> s.worldWidth - halfWidth to -1f
                else -> candidateX to enemy.direction
            }
            enemy.copy(position = enemy.position.copy(x = newX), direction = newDir)
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

        var alivePlatforms = platformsAfterCollision.filterNot { it.type == PlatformType.Cracked && it.isBroken }
        var aliveEnemies = updatedEnemies

        var cam = s.cameraOffset
        val playerScreenY = pos.y - cam

        if (playerScreenY < s.worldHeight * 0.4f) {
            cam = pos.y - s.worldHeight * 0.4f
        }

        if (playerScreenY > s.worldHeight + 100f) {
            endGame(); return
        }

        if (isTouchingEnemy(pos, aliveEnemies)) {
            endGame(); return
        }

        val highest = min(s.highestY, pos.y)

        val platformPool = alivePlatforms.filter { platform ->
            platform.position.y - cam <= s.worldHeight + offscreenCullBuffer
        }.toMutableList()
        val enemyPool = aliveEnemies.filter { enemy ->
            enemy.position.y - cam <= s.worldHeight + offscreenCullBuffer
        }.toMutableList()

        generatePlatforms(platformPool, enemyPool, s.worldWidth, cam)
        alivePlatforms = platformPool
        aliveEnemies = enemyPool

        _ui.value = s.copy(
            player = p.copy(position = pos, velocity = vel),
            platforms = alivePlatforms,
            enemies = aliveEnemies,
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

    private fun generatePlatforms(
        platforms: MutableList<Platform>,
        enemies: MutableList<Enemy>,
        worldWidth: Float,
        cameraOffset: Float,
    ) {
        val minY = platforms.minOfOrNull { it.position.y } ?: cameraOffset
        var currentY = minY

        while (currentY > cameraOffset - generationDepth) {
            currentY -= spacingRange.random()
            val newPlatform = createPlatform(currentY, worldWidth)
            platforms += newPlatform

            if (Random.nextFloat() < enemySpawnChance) {
                val enemy = Enemy(
                    id = nextEnemyId++,
                    position = Offset(
                        x = (GameScaling.enemyWidth.toInt()..(worldWidth - GameScaling.enemyWidth).toInt())
                            .random()
                            .toFloat(),
                        y = newPlatform.position.y - GameScaling.enemyHeight
                    ),
                    speed = GameConfig.bugSpeed,
                    direction = if (Random.nextBoolean()) 1f else -1f
                )
                enemies.add(enemy)
            }
        }
    }

    private fun createPlatform(y: Float, worldWidth: Float): Platform {
        val typeRoll = Random.nextFloat()
        val type = when {
            typeRoll < 0.15f -> PlatformType.Moving
            typeRoll < 0.35f -> PlatformType.Cracked
            else -> PlatformType.Static
        }
        val dir = if (Random.nextBoolean()) 1f else -1f
        val xRangeStart = GameScaling.platformWidth / 2f
        val xRangeEnd = worldWidth - GameScaling.platformWidth / 2f

        return Platform(
            id = nextPlatformId++,
            position = Offset(
                x = Random.nextDouble(xRangeStart.toDouble(), xRangeEnd.toDouble()).toFloat(),
                y = y
            ),
            width = GameScaling.platformWidth,
            height = GameScaling.platformHeight,
            type = type,
            direction = dir,
        )
    }

    private fun isTouchingEnemy(playerPosition: Offset, enemies: List<Enemy>): Boolean {
        val halfPlayer = GameScaling.playerCollisionRadius
        val halfEnemyX = GameScaling.enemyCollisionHalfWidth
        val halfEnemyY = GameScaling.enemyCollisionHalfHeight

        enemies.forEach { enemy ->
            val overlapX = abs(playerPosition.x - enemy.position.x) <= halfPlayer + halfEnemyX
            val overlapY = abs(playerPosition.y - enemy.position.y) <= halfPlayer + halfEnemyY
            if (overlapX && overlapY) return true
        }
        return false
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
