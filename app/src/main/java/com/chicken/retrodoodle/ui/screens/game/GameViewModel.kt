package com.chicken.retrodoodle.ui.screens.game

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicken.retrodoodle.core.model.Collectible
import com.chicken.retrodoodle.core.model.Enemy
import com.chicken.retrodoodle.core.model.GameStatus
import com.chicken.retrodoodle.core.model.Platform
import com.chicken.retrodoodle.core.model.PlatformType
import com.chicken.retrodoodle.core.model.Player
import com.chicken.retrodoodle.core.model.PlayerSize
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

private const val PLATFORM_SPACING = 120f
private const val HORIZONTAL_SPEED = 380f
private const val GRAVITY = 1200f
private const val JUMP_VELOCITY = 620f
private const val HORIZONTAL_PADDING = 12f

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
        val startPlatformY = worldHeight - 120f
        val startPlatform = Platform(
            id = 0,
            position = Offset(worldWidth / 2f, startPlatformY),
            width = 96f,
            height = 18f,
            type = PlatformType.Static
        )
        val firstPlatforms = generatePlatforms(startPlatform, worldWidth, count = 6)
        val playerStart = Player(
            position = Offset(worldWidth / 2f, startPlatformY - PlayerSize.value / 2f - 4f),
            velocity = Offset.Zero,
            skin = _uiState.value.player.skin
        )
        _uiState.value = GameUiState(
            status = GameStatus.Playing,
            player = playerStart,
            platforms = listOf(startPlatform) + firstPlatforms,
            collectibles = emptyList(),
            enemies = emptyList(),
            score = 0,
            eggs = 0,
            cameraOffset = startPlatformY - worldHeight * 0.4f,
            bestScore = _uiState.value.bestScore,
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
        if (state.status != GameStatus.Playing) return

        val player = state.player
        val width = state.worldWidth
        val height = state.worldHeight
        if (width == 0f || height == 0f) return

        val newVelocity = Offset(
            x = (player.velocity.x + (-state.tiltX * HORIZONTAL_SPEED * delta)).coerceIn(-HORIZONTAL_SPEED, HORIZONTAL_SPEED),
            y = player.velocity.y + GRAVITY * delta
        )

        val newPosition = player.position + newVelocity * delta
        val wrappedX = when {
            newPosition.x < -PlayerSize.value -> width + newPosition.x
            newPosition.x > width -> newPosition.x - width
            else -> newPosition.x
        }
        var adjustedPosition = Offset(wrappedX, newPosition.y)
        var adjustedVelocity = newVelocity
        var platforms = state.platforms.toMutableList()
        val prevBottom = player.position.y + PlayerSize.value / 2f
        val newBottom = adjustedPosition.y + PlayerSize.value / 2f

        val collided = platforms.firstOrNull { platform ->
            val top = platform.position.y - platform.height / 2f
            prevBottom <= top && newBottom >= top && adjustedVelocity.y > 0f &&
                abs(adjustedPosition.x - platform.position.x) < platform.width / 2f + PlayerSize.value / 2f - 6f
        }

        if (collided != null) {
            val updatedVel = Offset(adjustedVelocity.x, -JUMP_VELOCITY)
            adjustedVelocity = updatedVel
            adjustedPosition = adjustedPosition.copy(y = collided.position.y - collided.height / 2f - PlayerSize.value / 2f)
            if (collided.type == PlatformType.Cracked) {
                platforms.remove(collided)
            }
        }

        val enemies = state.enemies.toMutableList()
        val collectibles = state.collectibles.toMutableList()

        val enemyHit = enemies.firstOrNull { enemy ->
            abs(enemy.position.x - adjustedPosition.x) < 18f &&
                abs(enemy.position.y - adjustedPosition.y) < 18f
        }

        if (enemyHit != null) {
            if (adjustedVelocity.y > 0f) {
                enemies.remove(enemyHit)
                adjustedVelocity = adjustedVelocity.copy(y = -JUMP_VELOCITY * 1.05f)
                eggsCollected += 1
            } else {
                endGame()
                return
            }
        }

        val eggHit = collectibles.firstOrNull { egg ->
            abs(egg.position.x - adjustedPosition.x) < 16f &&
                abs(egg.position.y - adjustedPosition.y) < 16f
        }

        var eggsCollected = state.eggs
        if (eggHit != null) {
            eggsCollected += 1
            collectibles.remove(eggHit)
        }

        val updatedPlatforms = movePlatforms(platforms, delta, width)
        val updatedEnemies = moveEnemies(enemies, delta, width)

        val highestY = min(state.highestY, adjustedPosition.y)
        val scoreValue = ((state.worldHeight - highestY)).toInt()
        val camera = min(adjustedPosition.y - height * 0.4f, state.cameraOffset)

        val extendedPlatforms = extendPlatforms(updatedPlatforms, width, highestY)
        val extendedCollectibles = spawnCollectiblesIfNeeded(collectibles, extendedPlatforms)
        val extendedEnemies = spawnEnemiesIfNeeded(updatedEnemies, extendedPlatforms)

        if (adjustedPosition.y - state.cameraOffset > height + PlayerSize.value) {
            endGame()
            return
        }

        _uiState.value = state.copy(
            player = player.copy(position = adjustedPosition, velocity = adjustedVelocity),
            platforms = extendedPlatforms,
            enemies = extendedEnemies,
            collectibles = extendedCollectibles,
            eggs = eggsCollected,
            highestY = highestY,
            score = scoreValue,
            cameraOffset = camera,
            tiltX = state.tiltX
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

    private fun extendPlatforms(platforms: List<Platform>, width: Float, highestY: Float): List<Platform> {
        val mutable = platforms.toMutableList()
        val currentMinY = mutable.minOfOrNull { it.position.y } ?: highestY
        var nextY = currentMinY - PLATFORM_SPACING
        var nextId = (mutable.maxOfOrNull { it.id } ?: 0) + 1
        while (nextY > highestY - 800f) {
            val type = when (Random.nextFloat()) {
                in 0f..0.15f -> PlatformType.Moving
                in 0.15f..0.3f -> PlatformType.Cracked
                else -> PlatformType.Static
            }
            mutable.add(
                Platform(
                    id = nextId++,
                    position = Offset(Random.nextFloat() * (width - HORIZONTAL_PADDING * 2) + HORIZONTAL_PADDING, nextY),
                    width = 96f,
                    height = 18f,
                    type = type,
                    direction = if (Random.nextBoolean()) 1f else -1f
                )
            )
            nextY -= PLATFORM_SPACING
        }
        return mutable.filter { it.position.y - _uiState.value.cameraOffset < _uiState.value.worldHeight + 100f }
    }

    private fun movePlatforms(platforms: List<Platform>, delta: Float, width: Float): List<Platform> {
        return platforms.map { platform ->
            if (platform.type != PlatformType.Moving) platform else {
                val newX = platform.position.x + platform.direction * 50f * delta
                val wraps = when {
                    newX < HORIZONTAL_PADDING -> HORIZONTAL_PADDING
                    newX > width - HORIZONTAL_PADDING -> width - HORIZONTAL_PADDING
                    else -> newX
                }
                val newDirection = when {
                    newX < HORIZONTAL_PADDING -> 1f
                    newX > width - HORIZONTAL_PADDING -> -1f
                    else -> platform.direction
                }
                platform.copy(position = platform.position.copy(x = wraps), direction = newDirection)
            }
        }
    }

    private fun moveEnemies(enemies: List<Enemy>, delta: Float, width: Float): List<Enemy> {
        return enemies.map { enemy ->
            val x = enemy.position.x + enemy.speed * enemy.direction * delta
            val newDirection = when {
                x < HORIZONTAL_PADDING -> 1f
                x > width - HORIZONTAL_PADDING -> -1f
                else -> enemy.direction
            }
            enemy.copy(position = enemy.position.copy(x = x.coerceIn(HORIZONTAL_PADDING, width - HORIZONTAL_PADDING)), direction = newDirection)
        }
    }

    private fun spawnCollectiblesIfNeeded(existing: List<Collectible>, platforms: List<Platform>): List<Collectible> {
        if (existing.size > 6) return existing
        val withEggs = existing.toMutableList()
        platforms.filter { Random.nextFloat() < 0.08f && it.type == PlatformType.Static }.forEach { platform ->
            if (withEggs.none { abs(it.position.y - platform.position.y) < 10f }) {
                withEggs.add(
                    Collectible(
                        id = platform.id,
                        position = platform.position.copy(y = platform.position.y - 24f)
                    )
                )
            }
        }
        return withEggs
    }

    private fun spawnEnemiesIfNeeded(existing: List<Enemy>, platforms: List<Platform>): List<Enemy> {
        if (existing.size > 3) return existing
        val withBugs = existing.toMutableList()
        platforms.filter { Random.nextFloat() < 0.05f }.forEach { platform ->
            if (withBugs.none { abs(it.position.y - platform.position.y) < 40f }) {
                withBugs.add(
                    Enemy(
                        id = platform.id,
                        position = platform.position.copy(y = platform.position.y - 32f),
                        direction = if (Random.nextBoolean()) 1f else -1f
                    )
                )
            }
        }
        return withBugs
    }

    private fun generatePlatforms(base: Platform, width: Float, count: Int): List<Platform> {
        return (1..count).map { index ->
            val y = base.position.y - PLATFORM_SPACING * index
            val type = when {
                index % 5 == 0 -> PlatformType.Moving
                index % 3 == 0 -> PlatformType.Cracked
                else -> PlatformType.Static
            }
            Platform(
                id = index,
                position = Offset(Random.nextFloat() * (width - HORIZONTAL_PADDING * 2) + HORIZONTAL_PADDING, y),
                width = 96f,
                height = 18f,
                type = type,
                direction = if (Random.nextBoolean()) 1f else -1f
            )
        }
    }

    private fun endGame() {
        val current = _uiState.value
        viewModelScope.launch {
            settingsRepository.saveBestScore(current.score)
        }
        _uiState.value = current.copy(status = GameStatus.GameOver)
    }

    override fun onCleared() {
        super.onCleared()
        bestScoreJob?.cancel()
        skinJob?.cancel()
    }
}

private operator fun Offset.times(value: Float): Offset = Offset(x * value, y * value)

private data class GameUiState(
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
