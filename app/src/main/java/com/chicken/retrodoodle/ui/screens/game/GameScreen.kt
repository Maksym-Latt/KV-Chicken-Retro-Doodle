package com.chicken.retrodoodle.ui.screens.game

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.chicken.retrodoodle.R
import com.chicken.retrodoodle.audio.AudioController
import com.chicken.retrodoodle.core.config.GameDimensions
import com.chicken.retrodoodle.core.model.GameStatus
import com.chicken.retrodoodle.core.model.PlatformType
import com.chicken.retrodoodle.core.model.PlayerSize
import com.chicken.retrodoodle.ui.components.GameHud
import kotlinx.coroutines.isActive

@Composable
fun GameScreen(
    navController: NavHostController,
    audio: AudioController,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) { audio.playGameMusic() }

    var lastEggs by remember { mutableStateOf(state.eggs) }
    LaunchedEffect(state.eggs) {
        if (state.eggs > lastEggs) {
            audio.playCollectEgg()
            lastEggs = state.eggs
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val worldWidth = maxWidth.value
        val worldHeight = maxHeight.value

        LaunchedEffect(worldWidth, worldHeight) {
            if (state.status == GameStatus.Idle && worldWidth > 0f && worldHeight > 0f) {
                viewModel.startGame(worldWidth, worldHeight)
            }
        }

        LaunchedEffect(state.status) {
            var lastTime = 0L
            while (isActive && viewModel.uiState.value.status == GameStatus.Playing) {
                withFrameNanos { frameTime ->
                    if (lastTime != 0L) {
                        val delta = (frameTime - lastTime) / 1_000_000_000f
                        viewModel.updateFrame(delta)
                    }
                    lastTime = frameTime
                }
            }
        }

        DisposableEffect(state.status) {
            val manager = context.getSystemService(SensorManager::class.java)
            val sensor = manager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    val tilt = -(event?.values?.getOrNull(0) ?: 0f) / 4f
                    viewModel.updateTilt(tilt)
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }
            if (state.status == GameStatus.Playing) {
                manager?.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
            }
            onDispose { manager?.unregisterListener(listener) }
        }

        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0E1C2A)) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.bg_game),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.65f
                )

                val cameraOffset = state.cameraOffset

                state.platforms.forEach { platform ->
                    val drawable = when (platform.type) {
                        PlatformType.Static -> R.drawable.plate_green
                        PlatformType.Moving -> R.drawable.plate_blue
                        PlatformType.Cracked -> R.drawable.plate_brown
                    }
                    Image(
                        painter = painterResource(id = drawable),
                        contentDescription = null,
                        modifier = Modifier
                            .offset(
                                x = (platform.position.x - platform.width / 2f).dp,
                                y = (platform.position.y - cameraOffset).dp
                            )
                            .size(width = platform.width.dp, height = platform.height.dp),
                        contentScale = ContentScale.FillBounds
                    )
                }

                val collectibleSize = GameDimensions.collectibleSize
                state.collectibles.forEach { egg ->
                    Image(
                        painter = painterResource(id = R.drawable.item_gold_egg),
                        contentDescription = null,
                        modifier = Modifier
                            .offset(
                                x = (egg.position.x - collectibleSize / 2f).dp,
                                y = (egg.position.y - cameraOffset - collectibleSize / 2f).dp
                            )
                            .size(collectibleSize.dp)
                    )
                }

                val enemySize = GameDimensions.enemySize
                state.enemies.forEach { enemy ->
                    Image(
                        painter = painterResource(id = R.drawable.item_bug),
                        contentDescription = null,
                        modifier = Modifier
                            .offset(
                                x = (enemy.position.x - enemySize / 2f).dp,
                                y = (enemy.position.y - cameraOffset - enemySize / 2f).dp
                            )
                            .size(enemySize.dp)
                    )
                }

                Image(
                    painter = painterResource(id = state.player.skin.sprite),
                    contentDescription = null,
                    modifier = Modifier
                        .offset(
                            x = (state.player.position.x - PlayerSize.value / 2f).dp,
                            y = (state.player.position.y - cameraOffset - PlayerSize.value / 2f).dp
                        )
                        .size(PlayerSize),
                    contentScale = ContentScale.Fit
                )

                GameHud(
                    score = state.score,
                    eggs = state.eggs,
                    onPause = { viewModel.pauseGame() },
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                when (state.status) {
                    GameStatus.Idle -> GameIntroOverlay(
                        onStart = { viewModel.startGame(worldWidth, worldHeight) },
                        modifier = Modifier.align(Alignment.Center)
                    )

                    GameStatus.Paused -> GamePauseOverlay(
                        onContinue = { viewModel.resumeGame() },
                        onRestart = { viewModel.startGame(worldWidth, worldHeight) },
                        onMenu = { navController.navigateUp() },
                        modifier = Modifier.align(Alignment.Center)
                    )

                    GameStatus.GameOver -> GameOverOverlay(
                        score = state.score,
                        bestScore = maxOf(state.bestScore, state.score),
                        onRetry = { viewModel.startGame(worldWidth, worldHeight) },
                        onMenu = { navController.navigateUp() },
                        modifier = Modifier.align(Alignment.Center)
                    )

                    GameStatus.Playing -> Unit
                }
            }
        }
    }
}
