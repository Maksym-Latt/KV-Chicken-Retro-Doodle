package com.chicken.retrodoodle.ui.screens.game

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicken.retrodoodle.core.config.GameDimensions
import com.chicken.retrodoodle.core.model.Collectible
import com.chicken.retrodoodle.core.model.Enemy
import com.chicken.retrodoodle.core.model.GameStatus
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

private val BASE_PLATFORM_SPACING = 110f * GameDimensions.sizeScale
private const val HORIZONTAL_TILT_ACCEL = 320f
private const val MAX_HORIZONTAL_SPEED = 420f
private const val GRAVITY = 1250f
private const val JUMP_FORCE = 680f
private val EDGE_PADDING = 12f * GameDimensions.sizeScale
private val WORLD_BUFFER = 900f * GameDimensions.sizeScale

@HiltViewModel
class GameViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var bestScoreJob: Job? = null
    private var skinJob: Job? = null

    init {
        bestScoreJob = viewModelScope.launch {
            settingsRepository.bestScoreFlow.collect { best ->
                _uiState.value = _uiState.value.copy(bestScore = best)
            }
        }

        skinJob = viewModelScope.launch {
            settingsRepository.selectedSkinFlow.collect { skin ->
                _uiState.value = _uiState.value.copy(
                    player = _uiState.value.player.copy(skin = skin)
                )
            }
        }
    }

    fun startGame(worldWidth: Float, worldHeight: Float) {
        val startY = worldHeight - 120f * GameDimensions.sizeScale
        val playerSize = GameDimensions.playerSize
        val basePlatform = Platform(
            id = 0,
            position = Offset(worldWidth / 2f, startY),
            width = GameDimensions.basePlatformWidth,
            height = GameDimensions.basePlatformHeight,
            type = PlatformType.Static
        )
        val platforms = buildList {
            add(basePlatform)
            addAll(generatePlatforms(worldWidth, startY))
        }

        _uiState.value = GameUiState(
            status = GameStatus.Playing,
            player = Player(
                position = Offset(worldWidth / 2f, startY - playerSize / 2f - 6f),
                velocity = Offset.Zero,
                skin = _uiState.value.player.skin
            ),
            platforms = platforms,
            collectibles = emptyList(),
            enemies = emptyList(),
            score = 0,
            bestScore = _uiState.value.bestScore,
            eggs = 0,
            highestY = startY,
            tiltX = 0f,
            cameraOffset = (startY - playerSize / 2f - 6f - worldHeight * 0.8f).coerceAtLeast(0f),
            worldWidth = worldWidth,
            worldHeight = worldHeight
        )
    }

    fun updateTilt(x: Float) {
        if (_uiState.value.status == GameStatus.Playing) {
            _uiState.value = _uiState.value.copy(tiltX = x)
        }
    }

    fun updateFrame(delta: Float) {
        val state = _uiState.value
        if (state.status != GameStatus.Playing || delta <= 0f) return
        if (state.worldWidth == 0f || state.worldHeight == 0f) return

        val player = state.player
        val playerSize = GameDimensions.playerSize
        val playerHalf = playerSize / 2f
        val playerCollisionHalf = playerHalf * GameDimensions.collisionScale

        val newVelocity = Offset(
            x = (player.velocity.x + (-state.tiltX * HORIZONTAL_TILT_ACCEL * delta))
                .coerceIn(-MAX_HORIZONTAL_SPEED, MAX_HORIZONTAL_SPEED),
            y = player.velocity.y + GRAVITY * delta
        )

        val nextPos = player.position + newVelocity * delta
        val wrappedX = when {
            nextPos.x < -playerSize -> state.worldWidth + nextPos.x
            nextPos.x > state.worldWidth -> nextPos.x - state.worldWidth
            else -> nextPos.x
        }
        var updatedPos = Offset(wrappedX, nextPos.y)
        var updatedVelocity = newVelocity

        val platforms = state.platforms.toMutableList()
        val previousBottom = player.position.y + playerHalf
        val newBottom = updatedPos.y + playerHalf

        platforms.firstOrNull { platform ->
            val top = platform.position.y - platform.height / 2f
            previousBottom <= top && newBottom >= top && updatedVelocity.y > 0f &&
                abs(updatedPos.x - platform.position.x) < platform.width / 2f + playerCollisionHalf - 4f * GameDimensions.sizeScale
        }?.let { platform ->
            updatedVelocity = updatedVelocity.copy(y = -JUMP_FORCE)
            updatedPos = updatedPos.copy(y = platform.position.y - platform.height / 2f - playerHalf)
            if (platform.type == PlatformType.Cracked) {
                platforms.remove(platform)
            }
        }

        val enemies = state.enemies.toMutableList()
        val collectibles = state.collectibles.toMutableList()

        val enemyHalf = GameDimensions.enemySize / 2f
        val enemyCollision = enemyHalf * GameDimensions.collisionScale
        enemies.firstOrNull { enemy ->
            abs(enemy.position.x - updatedPos.x) < enemyCollision &&
                abs(enemy.position.y - updatedPos.y) < enemyCollision
        }?.let { hit ->
            if (updatedVelocity.y > 0f) {
                enemies.remove(hit)
                updatedVelocity = updatedVelocity.copy(y = -JUMP_FORCE * 1.08f)
            } else {
                endGame()
                return
            }
        }

        var eggs = state.eggs
        val collectibleHalf = GameDimensions.collectibleSize / 2f * GameDimensions.collisionScale
        collectibles.firstOrNull { egg ->
            abs(egg.position.x - updatedPos.x) < collectibleHalf &&
                abs(egg.position.y - updatedPos.y) < collectibleHalf
        }?.let { found ->
            eggs += 1
            collectibles.remove(found)
        }

        val movedPlatforms = movePlatforms(platforms, delta, state.worldWidth)
        val movedEnemies = moveEnemies(enemies, delta, state.worldWidth)

        val highestY = min(state.highestY, updatedPos.y)
        val cameraOffset = min(updatedPos.y - state.worldHeight * 0.45f, state.cameraOffset)
        val score = (state.worldHeight - highestY).toInt().coerceAtLeast(0)

        val extendedPlatforms = extendPlatforms(movedPlatforms, state.worldWidth, highestY, cameraOffset, state.worldHeight)
        val extendedCollectibles = spawnCollectibles(collectibles, extendedPlatforms)
        val extendedEnemies = spawnEnemies(movedEnemies, extendedPlatforms)

        if (updatedPos.y - cameraOffset > state.worldHeight + playerSize) {
            endGame()
            return
        }

        _uiState.value = state.copy(
            player = player.copy(position = updatedPos, velocity = updatedVelocity),
            platforms = extendedPlatforms,
            enemies = extendedEnemies,
            collectibles = extendedCollectibles,
            eggs = eggs,
            highestY = highestY,
            cameraOffset = cameraOffset,
            score = score
        )
    }

    fun pauseGame() {
        if (_uiState.value.status == GameStatus.Playing) {
            _uiState.value = _uiState.value.copy(status = GameStatus.Paused)
        }
    }

    fun resumeGame() {
        if (_uiState.value.status == GameStatus.Paused) {
            _uiState.value = _uiState.value.copy(status = GameStatus.Playing)
        }
    }

    fun resetGame() {
        _uiState.value = GameUiState(player = _uiState.value.player)
    }

    private fun extendPlatforms(
        platforms: List<Platform>,
        width: Float,
        highestY: Float,
        cameraOffset: Float,
        worldHeight: Float
    ): List<Platform> {
        val mutable = platforms.filter { it.position.y - cameraOffset < worldHeight + 120f }.toMutableList()
        var minY = mutable.minOfOrNull { it.position.y } ?: highestY
        var nextId = (mutable.maxOfOrNull { it.id } ?: 0) + 1

        while (minY > highestY - WORLD_BUFFER) {
            minY -= BASE_PLATFORM_SPACING
            mutable.add(createPlatform(id = nextId++, y = minY, width = width))
        }
        return mutable
    }

    private fun movePlatforms(platforms: List<Platform>, delta: Float, width: Float): List<Platform> {
        return platforms.map { platform ->
            if (platform.type != PlatformType.Moving) platform else {
                val nextX = platform.position.x + platform.direction * 60f * delta
                val direction = when {
                    nextX < EDGE_PADDING -> 1f
                    nextX > width - EDGE_PADDING -> -1f
                    else -> platform.direction
                }
                platform.copy(
                    position = platform.position.copy(x = nextX.coerceIn(EDGE_PADDING, width - EDGE_PADDING)),
                    direction = direction
                )
            }
        }
    }

    private fun moveEnemies(enemies: List<Enemy>, delta: Float, width: Float): List<Enemy> {
        return enemies.map { enemy ->
            val nextX = enemy.position.x + enemy.direction * enemy.speed * delta
            val direction = when {
                nextX < EDGE_PADDING -> 1f
                nextX > width - EDGE_PADDING -> -1f
                else -> enemy.direction
            }
            enemy.copy(
                position = enemy.position.copy(x = nextX.coerceIn(EDGE_PADDING, width - EDGE_PADDING)),
                direction = direction
            )
        }
    }

    private fun spawnCollectibles(existing: List<Collectible>, platforms: List<Platform>): List<Collectible> {
        if (existing.size >= 6) return existing
        val mutable = existing.toMutableList()
        platforms.filter { it.type == PlatformType.Static && Random.nextFloat() < 0.07f }
            .forEach { platform ->
                if (mutable.none { abs(it.position.y - platform.position.y) < 12f }) {
                    mutable.add(
                        Collectible(
                            id = platform.id,
                            position = platform.position.copy(
                                y = platform.position.y - platform.height / 2f - GameDimensions.collectibleSize * 0.6f
                            )
                        )
                    )
                }
            }
        return mutable
    }

    private fun spawnEnemies(existing: List<Enemy>, platforms: List<Platform>): List<Enemy> {
        if (existing.size >= 4) return existing
        val mutable = existing.toMutableList()
        platforms.filter { Random.nextFloat() < 0.05f }
            .forEach { platform ->
                if (mutable.none { abs(it.position.y - platform.position.y) < 48f }) {
                    mutable.add(
                        Enemy(
                            id = platform.id,
                            position = platform.position.copy(
                                y = platform.position.y - platform.height / 2f - GameDimensions.enemySize * 0.75f
                            ),
                            direction = if (Random.nextBoolean()) 1f else -1f,
                            speed = 55f
                        )
                    )
                }
            }
        return mutable
    }

    private fun generatePlatforms(width: Float, startY: Float): List<Platform> {
        var y = startY
        var id = 1
        return buildList {
            while (y > startY - WORLD_BUFFER) {
                y -= BASE_PLATFORM_SPACING
                add(createPlatform(id = id++, y = y, width = width))
            }
        }
    }

    private fun createPlatform(id: Int, y: Float, width: Float): Platform {
        val type = when (Random.nextFloat()) {
            in 0f..0.12f -> PlatformType.Moving
            in 0.12f..0.22f -> PlatformType.Cracked
            else -> PlatformType.Static
        }
        return Platform(
            id = id,
            position = Offset(
                x = Random.nextFloat() * (width - EDGE_PADDING * 2f) + EDGE_PADDING,
                y = y
            ),
            width = GameDimensions.platformWidth,
            height = GameDimensions.platformHeight,
            type = type,
            direction = if (Random.nextBoolean()) 1f else -1f
        )
    }

    private fun endGame() {
        val current = _uiState.value
        viewModelScope.launch { settingsRepository.saveBestScore(current.score) }
        _uiState.value = current.copy(status = GameStatus.GameOver)
    }

    override fun onCleared() {
        super.onCleared()
        bestScoreJob?.cancel()
        skinJob?.cancel()
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
