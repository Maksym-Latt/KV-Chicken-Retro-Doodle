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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.chicken.retrodoodle.R
import com.chicken.retrodoodle.audio.AudioController
import com.chicken.retrodoodle.core.model.GameConfig
import com.chicken.retrodoodle.core.model.GameStatus
import com.chicken.retrodoodle.core.model.GameScaling
import com.chicken.retrodoodle.core.model.PlatformType
import com.chicken.retrodoodle.ui.components.GameHud
import com.chicken.retrodoodle.ui.screens.settings.SettingsViewModel
import kotlinx.coroutines.isActive
import kotlin.math.roundToInt
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign

@Composable
fun GameScreen(
    navController: NavHostController,
    audio: AudioController,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settings by settingsViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val density = LocalDensity.current

    LaunchedEffect(Unit) { audio.playGameMusic() }

    BoxWithConstraints(Modifier.fillMaxSize()) {

        val worldWidthPx = with(density) { maxWidth.toPx() }
        val worldHeightPx = with(density) { maxHeight.toPx() }

        Image(
            painter = painterResource(id = R.drawable.bg_game),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        val plateGreen = ImageBitmap.imageResource(R.drawable.plate_green)
        val plateBlue = ImageBitmap.imageResource(R.drawable.plate_blue)
        val plateBrown = ImageBitmap.imageResource(R.drawable.plate_brown)
        val eggBmp = ImageBitmap.imageResource(R.drawable.item_gold_egg)
        val bugBmp = ImageBitmap.imageResource(R.drawable.item_bug)
        val playerBmp = ImageBitmap.imageResource(state.player.skin.sprite)

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

        DisposableEffect(state.status) {
            val mgr = context.getSystemService(SensorManager::class.java)
            val sensor = mgr?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

            val listener = object : SensorEventListener {
                override fun onSensorChanged(e: SensorEvent?) {
                    val rawTilt = (e?.values?.getOrNull(0) ?: 0f) / GameConfig.tiltInputScale
                    val clampedTilt = rawTilt.coerceIn(-GameConfig.tiltInputMax, GameConfig.tiltInputMax)
                    val curvedTilt = sign(clampedTilt) * abs(clampedTilt).pow(0.8f)
                    viewModel.updateTilt(curvedTilt.coerceIn(-1f, 1f))
                }
                override fun onAccuracyChanged(s: Sensor?, a: Int) {}
            }

            if (state.status == GameStatus.Playing)
                mgr?.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)

            onDispose { mgr?.unregisterListener(listener) }
        }

        Canvas(Modifier.fillMaxSize()) {

            val cam = state.cameraOffset

            state.platforms.forEach { p ->
                if (p.isBroken) return@forEach
                val bmp = when (p.type) {
                    PlatformType.Static -> plateGreen
                    PlatformType.Moving -> plateBlue
                    PlatformType.Cracked -> plateBrown
                }

                val dstOffset = IntOffset(
                    (p.position.x - p.width / 2f).roundToInt(),
                    (p.position.y - cam - p.height / 2f).roundToInt()
                )

                drawImage(
                    image = bmp,
                    srcOffset = IntOffset.Zero,
                    srcSize = IntSize(bmp.width, bmp.height),
                    dstOffset = dstOffset,
                    dstSize = IntSize(p.width.roundToInt(), p.height.roundToInt())
                )
            }

            state.collectibles.forEach { c ->
                val collectibleWidth = GameScaling.collectibleWidth
                val collectibleHeight = GameScaling.collectibleHeight
                val collectibleHalfWidth = GameScaling.collectibleHalfWidth
                val collectibleHalfHeight = GameScaling.collectibleHalfHeight
                drawImage(
                    image = eggBmp,
                    srcOffset = IntOffset.Zero,
                    srcSize = IntSize(eggBmp.width, eggBmp.height),
                    dstOffset = IntOffset(
                        (c.position.x - collectibleHalfWidth).roundToInt(),
                        (c.position.y - cam - collectibleHalfHeight).roundToInt()
                    ),
                    dstSize = IntSize(
                        collectibleWidth.roundToInt(),
                        collectibleHeight.roundToInt()
                    )
                )
            }

            state.enemies.forEach { e ->
                val bugHalfWidth = GameScaling.enemyWidth / 2f
                val bugHalfHeight = GameScaling.enemyHeight / 2f
                val enemyPivot = Offset(e.position.x, e.position.y - cam)
                val bugTopLeft = Offset(
                    e.position.x - bugHalfWidth,
                    e.position.y - cam - bugHalfHeight
                )
                val bugScaleX = if (e.direction > 0f) -1f else 1f

                withTransform({ scale(scaleX = bugScaleX, scaleY = 1f, pivot = enemyPivot) }) {
                    drawImage(
                        image = bugBmp,
                        srcOffset = IntOffset.Zero,
                        srcSize = IntSize(bugBmp.width, bugBmp.height),
                        dstOffset = IntOffset(
                            bugTopLeft.x.roundToInt(),
                            bugTopLeft.y.roundToInt()
                        ),
                        dstSize = IntSize(
                            GameScaling.enemyWidth.roundToInt(),
                            GameScaling.enemyHeight.roundToInt()
                        )
                    )
                }
            }

            val playerWidthPx = GameScaling.playerWidth
            val playerHeightPx = GameScaling.playerHeight
            val playerDstOffset = IntOffset(
                (state.player.position.x - GameScaling.playerHalfWidth).roundToInt(),
                (state.player.position.y - cam - GameScaling.playerHalfHeight).roundToInt()
            )

            val playerPivot = Offset(state.player.position.x, state.player.position.y - cam)
            val playerScaleX = if (state.player.velocity.x < -0.1f) -1f else 1f

            withTransform({ scale(scaleX = playerScaleX, scaleY = 1f, pivot = playerPivot) }) {
                drawImage(
                    image = playerBmp,
                    srcOffset = IntOffset.Zero,
                    srcSize = IntSize(playerBmp.width, playerBmp.height),
                    dstOffset = playerDstOffset,
                    dstSize = IntSize(playerWidthPx.roundToInt(), playerHeightPx.roundToInt())
                )
            }

            if (GameConfig.debugCollisionOverlay) {
                state.platforms.forEach { platform ->
                    if (platform.isBroken) return@forEach
                    val collisionWidth = platform.width - GameScaling.platformCollisionBuffer * 2f
                    drawRect(
                        color = Color.Red.copy(alpha = 0.3f),
                        topLeft = Offset(
                            platform.position.x - collisionWidth / 2f,
                            platform.position.y - cam - platform.height / 2f
                        ),
                        size = Size(collisionWidth, 12f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                    )
                }

                val playerCollisionHalfWidth = GameScaling.playerCollisionHalfWidth
                val playerCollisionHalfHeight = GameScaling.playerCollisionHalfHeight
                drawRect(
                    color = Color.Green.copy(alpha = 0.4f),
                    topLeft = Offset(
                        state.player.position.x - playerCollisionHalfWidth,
                        state.player.position.y - cam - playerCollisionHalfHeight
                    ),
                    size = Size(
                        playerCollisionHalfWidth * 2f,
                        playerCollisionHalfHeight * 2f
                    ),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                )

                state.enemies.forEach { enemy ->
                    drawRect(
                        color = Color.Magenta.copy(alpha = 0.4f),
                        topLeft = Offset(
                            enemy.position.x - GameScaling.enemyCollisionHalfWidth,
                            enemy.position.y - cam - GameScaling.enemyCollisionHalfHeight
                        ),
                        size = Size(
                            GameScaling.enemyCollisionHalfWidth * 2f,
                            GameScaling.enemyCollisionHalfHeight * 2f
                        ),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                    )
                }

                state.collectibles.forEach { collectible ->
                    drawRect(
                        color = Color.Cyan.copy(alpha = 0.4f),
                        topLeft = Offset(
                            collectible.position.x - GameScaling.collectibleCollisionHalfWidth,
                            collectible.position.y - cam - GameScaling.collectibleCollisionHalfHeight
                        ),
                        size = Size(
                            GameScaling.collectibleCollisionHalfWidth * 2f,
                            GameScaling.collectibleCollisionHalfHeight * 2f
                        ),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                    )
                }
            }
        }

        GameHud(
            score = state.score,
            levelEggs = state.levelEggs,
            onPause = { viewModel.pauseGame() },
        )

        when (state.status) {

            GameStatus.Idle -> GameIntroOverlay(
                onStart = {
                    if (worldWidthPx > 0f && worldHeightPx > 0f) {
                        viewModel.startGame(worldWidthPx, worldHeightPx)
                    }
                },
                modifier = Modifier.align(Alignment.Center)
            )

            GameStatus.Paused -> GamePauseOverlay(
                musicOn = settings.musicEnabled,
                soundsOn = settings.soundsEnabled,
                onToggleMusic = { settingsViewModel.toggleMusic() },
                onToggleSounds = { settingsViewModel.toggleSounds() },
                onResume = { viewModel.resumeGame() },
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