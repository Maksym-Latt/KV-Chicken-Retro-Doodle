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
import com.chicken.retrodoodle.core.model.PlayerSkin
import com.chicken.retrodoodle.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class GameViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState

    private val _events = MutableSharedFlow<GameEvent>()
    val events = _events.asSharedFlow()

    private var loopJob: Job? = null
    private var tiltValue: Float = 0f

    private val gravity = -420f
    private val jumpVelocity = 260f
    private val worldWidth = 360f
    private val spawnInterval = 120f

    init {
        observePreferences()
    }

    fun startNewGame() {
        val initialPlayer = Player(position = Offset(worldWidth / 2f, 40f))
        val basePlatforms = List(6) { index ->
            Platform(
                id = index,
                position = Offset(Random.nextFloat() * (worldWidth - 96f) + 32f, index * 80f),
                type = if (index == 0) PlatformType.Static else randomPlatformType()
            )
        }
        _uiState.value = _uiState.value.copy(
            status = GameStatus.Playing,
            player = initialPlayer,
            platforms = basePlatforms,
            enemies = emptyList(),
            eggs = listOf(Collectible(0, basePlatforms.random().position + Offset(32f, 22f))),
            score = 0,
            eggsCollected = 0,
            cameraHeight = 0f
        )
        loopJob?.cancel()
        loopJob = viewModelScope.launch { runGameLoop() }
    }

    fun updateTilt(horizontal: Float) {
        tiltValue = horizontal.coerceIn(-4f, 4f)
    }

    fun pauseGame() {
        _uiState.value = _uiState.value.copy(status = GameStatus.Paused)
    }

    fun resumeGame() {
        _uiState.value = _uiState.value.copy(status = GameStatus.Playing)
    }

    fun handleGameOver() {
        _uiState.value = _uiState.value.copy(status = GameStatus.GameOver)
        viewModelScope.launch {
            settingsRepository.saveBestScore(_uiState.value.score)
        }
    }

    private suspend fun runGameLoop() {
        var lastTime = System.currentTimeMillis()
        while (true) {
            val now = System.currentTimeMillis()
            val delta = (now - lastTime).coerceAtMost(100).toFloat() / 1000f
            lastTime = now
            tick(delta)
            delay(16L)
        }
    }

    private fun tick(delta: Float) {
        val state = _uiState.value
        if (state.status != GameStatus.Playing) return

        var player = state.player
        val updatedVelocity = player.velocity.copy(
            x = tiltValue * 45f,
            y = (player.velocity.y + gravity * delta)
        )
        var newPosition = player.position + updatedVelocity * delta
        if (newPosition.x < -20f) newPosition = newPosition.copy(x = worldWidth + newPosition.x)
        if (newPosition.x > worldWidth + 20f) newPosition = newPosition.copy(x = newPosition.x - worldWidth)

        val collision = findPlatformCollision(state, player.position, newPosition)
        var platforms = state.platforms
        if (collision != null && updatedVelocity.y <= 0f) {
            newPosition = collision.position.copy(y = collision.position.y + 24f)
            player = player.copy(velocity = Offset(updatedVelocity.x, jumpVelocity))
            emitEvent(GameEvent.Jump)
            if (collision.type == PlatformType.Cracked) {
                platforms = platforms.filterNot { it.id == collision.id }
            }
        } else {
            player = player.copy(velocity = updatedVelocity)
        }

        val enemies = state.enemies.map { enemy ->
            val nextX = (enemy.position.x + enemy.direction * enemy.speed * delta).wrap(worldWidth)
            enemy.copy(position = Offset(nextX, enemy.position.y), direction = enemy.direction)
        }

        val stompedEnemy = enemies.firstOrNull { enemy ->
            overlapsHorizontal(enemy.position.x, newPosition.x) &&
                    kotlin.math.abs(enemy.position.y - newPosition.y) < 16f &&
                    updatedVelocity.y <= 0f
        }

        var eggs = state.eggs
        var eggsCollected = state.eggsCollected
        stompedEnemy?.let {
            emitEvent(GameEvent.Hit)
            player = player.copy(velocity = Offset(updatedVelocity.x, jumpVelocity * 1.1f))
        }

        val collectedEgg = eggs.firstOrNull { egg ->
            overlapsHorizontal(egg.position.x, newPosition.x) &&
                    kotlin.math.abs(egg.position.y - newPosition.y) < 20f
        }
        if (collectedEgg != null) {
            eggsCollected += 1
            eggs = eggs.filterNot { it.id == collectedEgg.id }
            emitEvent(GameEvent.Collect)
        }

        val maxHeight = maxOf(state.score.toFloat(), newPosition.y)
        val cameraHeight = maxOf(state.cameraHeight, newPosition.y - 200f)

        val extendedPlatforms = ensurePlatforms(platforms, maxHeight)
        val extendedEnemies = ensureEnemies(enemies, maxHeight)
        val extendedEggs = ensureEggs(eggs, extendedPlatforms)

        val gameOver = newPosition.y < cameraHeight - 240f
        val newStatus = if (gameOver) GameStatus.GameOver else GameStatus.Playing

        _uiState.value = state.copy(
            status = newStatus,
            player = player.copy(position = newPosition),
            platforms = extendedPlatforms,
            enemies = extendedEnemies,
            eggs = extendedEggs,
            score = maxHeight.toInt(),
            eggsCollected = eggsCollected,
            cameraHeight = cameraHeight
        )

        if (gameOver) {
            emitEvent(GameEvent.Hit)
            viewModelScope.launch { settingsRepository.saveBestScore(maxHeight.toInt()) }
        }
    }

    private fun overlapsHorizontal(a: Float, b: Float): Boolean = kotlin.math.abs(a - b) < 32f

    private fun findPlatformCollision(
        state: GameUiState,
        previous: Offset,
        next: Offset
    ): Platform? {
        return state.platforms.firstOrNull { platform ->
            val goingDown = next.y <= previous.y
            val withinHorizontal = kotlin.math.abs(platform.position.x - next.x) < platform.width / 2
            val crossing = previous.y >= platform.position.y && next.y <= platform.position.y + 12f
            goingDown && withinHorizontal && crossing
        }
    }

    private fun ensurePlatforms(platforms: List<Platform>, targetHeight: Float): List<Platform> {
        val mutable = platforms.toMutableList()
        val highest = mutable.maxOfOrNull { it.position.y } ?: 0f
        var nextY = highest
        var id = (mutable.maxOfOrNull { it.id } ?: 0) + 1
        while (nextY < targetHeight + spawnInterval) {
            nextY += spawnInterval
            val type = randomPlatformType()
            val movingDir = if (type == PlatformType.Moving && Random.nextBoolean()) -1f else 1f
            mutable += Platform(
                id = id++,
                position = Offset(Random.nextFloat() * (worldWidth - 72f) + 36f, nextY),
                type = type,
                direction = movingDir
            )
        }
        return mutable.filter { it.position.y >= (targetHeight - 400f) }
    }

    private fun ensureEnemies(enemies: List<Enemy>, targetHeight: Float): List<Enemy> {
        val mutable = enemies.toMutableList()
        val highest = mutable.maxOfOrNull { it.position.y } ?: 0f
        var nextY = highest
        var id = (mutable.maxOfOrNull { it.id } ?: 0) + 1
        while (nextY < targetHeight + 200f) {
            nextY += 220f
            mutable += Enemy(id = id++, position = Offset(Random.nextFloat() * worldWidth, nextY))
        }
        return mutable.filter { it.position.y >= targetHeight - 420f }
    }

    private fun ensureEggs(eggs: List<Collectible>, platforms: List<Platform>): List<Collectible> {
        val mutable = eggs.toMutableList()
        val existingIds = mutable.map { it.id }.toSet()
        var nextId = (existingIds.maxOrNull() ?: 0) + 1
        platforms.filter { Random.nextInt(0, 4) == 1 }.forEach { platform ->
            if (mutable.none { kotlin.math.abs(it.position.y - platform.position.y) < 8f }) {
                mutable += Collectible(id = nextId++, position = platform.position + Offset(24f, 18f))
            }
        }
        return mutable
    }

    private fun observePreferences() {
        viewModelScope.launch {
            settingsRepository.bestScoreFlow.collect { best ->
                _uiState.value = _uiState.value.copy(bestScore = best)
            }
        }
        viewModelScope.launch {
            settingsRepository.selectedSkinFlow.collect { skin ->
                val player = _uiState.value.player.copy(skin = skin)
                _uiState.value = _uiState.value.copy(selectedSkin = skin, player = player)
            }
        }
    }

    private fun emitEvent(event: GameEvent) {
        viewModelScope.launch { _events.emit(event) }
    }
}

private fun randomPlatformType(): PlatformType =
    listOf(PlatformType.Static, PlatformType.Moving, PlatformType.Cracked).random()

private operator fun Offset.plus(other: Offset) = Offset(x + other.x, y + other.y)
private operator fun Offset.times(value: Float) = Offset(x * value, y * value)
private fun Float.wrap(width: Float): Float {
    var new = this
    if (new < 0f) new += width
    if (new > width) new -= width
    return new
}
