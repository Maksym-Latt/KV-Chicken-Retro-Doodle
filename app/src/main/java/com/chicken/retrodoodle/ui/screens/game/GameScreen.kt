package com.chicken.retrodoodle.ui.screens.game

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.chicken.retrodoodle.R
import com.chicken.retrodoodle.audio.AudioController
import com.chicken.retrodoodle.core.model.GameStatus
import com.chicken.retrodoodle.core.model.GameScaling
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
    val density = LocalDensity.current

    LaunchedEffect(Unit) { audio.playGameMusic() }

    BoxWithConstraints(Modifier.fillMaxSize()) {

        val worldWidthPx = with(density) { maxWidth.toPx() }
        val worldHeightPx = with(density) { maxHeight.toPx() }

        LaunchedEffect(worldWidthPx, worldHeightPx) {
            if (state.status == GameStatus.Idle && worldHeightPx > 0f) {
                viewModel.startGame(worldWidthPx, worldHeightPx)
            }
        }

        // ---------- ЗАГРУЖАЕМ ВСЕ БИТМАПЫ ЗАРАНЕЕ ----------
        val plateGreen = ImageBitmap.imageResource(R.drawable.plate_green)
        val plateBlue = ImageBitmap.imageResource(R.drawable.plate_blue)
        val plateBrown = ImageBitmap.imageResource(R.drawable.plate_brown)
        val eggBmp = ImageBitmap.imageResource(R.drawable.item_gold_egg)
        val bugBmp = ImageBitmap.imageResource(R.drawable.item_bug)
        val playerBmp = ImageBitmap.imageResource(state.player.skin.sprite)

        // ---------- ГЕЙМ ЛУП ----------
        LaunchedEffect(state.status) {
            var last = 0L
            while (isActive && state.status == GameStatus.Playing) {
                withFrameNanos { time ->
                    if (last != 0L) {
                        val dt = (time - last) / 1_000_000_000f
                        viewModel.updateFrame(dt)
                    }
                    last = time
                }
            }
        }

        // ---------- ACCELEROMETER ----------
        DisposableEffect(state.status) {
            val mgr = context.getSystemService(SensorManager::class.java)
            val sensor = mgr?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

            val listener = object : SensorEventListener {
                override fun onSensorChanged(e: SensorEvent?) {
                    val tilt = -(e?.values?.getOrNull(0) ?: 0f) / 6f
                    viewModel.updateTilt(tilt)
                }
                override fun onAccuracyChanged(s: Sensor?, a: Int) {}
            }

            if (state.status == GameStatus.Playing)
                mgr?.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)

            onDispose { mgr?.unregisterListener(listener) }
        }

        // ---------- CANVAS РЕНДЕР ----------
        Canvas(Modifier.fillMaxSize()) {

            drawRect(color = Color(0xFF0E1C2A))
            val cam = state.cameraOffset

            state.platforms.forEach { p ->
                val bmp = when (p.type) {
                    PlatformType.Static -> plateGreen
                    PlatformType.Moving -> plateBlue
                    PlatformType.Cracked -> plateBrown
                }

                drawImage(
                    image = bmp,
                    topLeft = Offset(
                        p.position.x - p.width / 2f,
                        p.position.y - cam - p.height / 2f
                    )
                )
            }

            state.collectibles.forEach { c ->
                drawImage(
                    image = eggBmp,
                    topLeft = Offset(
                        c.position.x - 16f,
                        c.position.y - cam - 16f
                    )
                )
            }

            state.enemies.forEach { e ->
                drawImage(
                    image = bugBmp,
                    topLeft = Offset(
                        e.position.x - 24f,
                        e.position.y - cam - 24f
                    )
                )
            }

            drawImage(
                image = playerBmp,
                topLeft = Offset(
                    state.player.position.x - 32f,
                    state.player.position.y - cam - 32f
                )
            )
        }

        GameHud(
            score = state.score,
            eggs = state.eggs,
            onPause = { viewModel.pauseGame() },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        when (state.status) {
            GameStatus.Idle -> GameIntroOverlay(
                onStart = { viewModel.startGame(worldWidthPx, worldHeightPx) },
                modifier = Modifier.align(Alignment.Center)
            )
            GameStatus.Paused -> GamePauseOverlay(
                onContinue = { viewModel.resumeGame() },
                onRestart = { viewModel.startGame(worldWidthPx, worldHeightPx) },
                onMenu = { navController.navigateUp() },
                modifier = Modifier.align(Alignment.Center)
            )
            GameStatus.GameOver -> GameOverOverlay(
                score = state.score,
                bestScore = state.bestScore,
                onRetry = { viewModel.startGame(worldWidthPx, worldHeightPx) },
                onMenu = { navController.navigateUp() },
                modifier = Modifier.align(Alignment.Center)
            )
            GameStatus.Playing -> Unit
        }
    }
}