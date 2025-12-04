package com.chicken.retrodoodle.ui.screens.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.chicken.retrodoodle.R
import com.chicken.retrodoodle.audio.AudioController
import com.chicken.retrodoodle.core.model.GameStatus
import com.chicken.retrodoodle.core.model.PlatformType
import com.chicken.retrodoodle.ui.components.HudBadge
import com.chicken.retrodoodle.ui.components.OverlayPanel
import com.chicken.retrodoodle.ui.components.PixelButton
import com.chicken.retrodoodle.ui.theme.components.GradientText
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun GameScreen(
    navController: NavHostController,
    audio: AudioController,
    viewModel: GameViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    TiltObserver { viewModel.updateTilt(it) }

    LaunchedEffect(Unit) {
        viewModel.startNewGame()
        audio.playGameMusic()
    }

    LaunchedEffect(state.status) {
        when (state.status) {
            GameStatus.Paused, GameStatus.GameOver -> audio.pauseMusic()
            GameStatus.Playing -> audio.playGameMusic()
            else -> Unit
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                GameEvent.Jump -> audio.playChickenJump()
                GameEvent.Hit -> audio.playChickenHit()
                GameEvent.Collect -> audio.playCollectEgg()
                GameEvent.Win -> audio.playGameWin()
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0A1420)) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.bg_game),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val density = LocalDensity.current
                val heightPx = with(density) { maxHeight.toPx() }
                val camera = state.cameraHeight

                fun worldYToOffset(y: Float): Dp = with(density) {
                    (heightPx - (y - camera) - 48f).coerceIn(-200f, heightPx + 200f).toDp()
                }

                state.platforms.forEach { platform ->
                    val platformPainter = when (platform.type) {
                        PlatformType.Static -> R.drawable.plate_green
                        PlatformType.Moving -> R.drawable.plate_blue
                        PlatformType.Cracked -> R.drawable.plate_brown
                    }
                    Image(
                        painter = painterResource(id = platformPainter),
                        contentDescription = null,
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(
                                x = with(density) { (platform.position.x - 32f).toDp() },
                                y = worldYToOffset(platform.position.y)
                            )
                    )
                }

                state.enemies.forEach { enemy ->
                    Image(
                        painter = painterResource(id = R.drawable.item_bug),
                        contentDescription = null,
                        modifier = Modifier
                            .size(42.dp)
                            .offset(
                                x = with(density) { (enemy.position.x - 20f).toDp() },
                                y = worldYToOffset(enemy.position.y)
                            )
                    )
                }

                state.eggs.forEach { egg ->
                    Image(
                        painter = painterResource(id = R.drawable.item_gold_egg),
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .offset(
                                x = with(density) { (egg.position.x - 8f).toDp() },
                                y = worldYToOffset(egg.position.y)
                            )
                    )
                }

                Image(
                    painter = painterResource(id = state.selectedSkin.sprite),
                    contentDescription = null,
                    modifier = Modifier
                        .size(42.dp)
                        .offset(
                            x = with(density) { (state.player.position.x - 16f).toDp() },
                            y = worldYToOffset(state.player.position.y)
                        )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HudBadge(title = "Height", value = state.score.toString())
                    HudBadge(title = "Eggs", value = state.eggsCollected.toString())
                }
                IconButton(onClick = { viewModel.pauseGame() }) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xAA0C1521)),
                        contentAlignment = Alignment.Center
                    ) {
                        GradientText(text = "II", expand = false, size = 22.sp, stroke = 5f)
                    }
                }
            }

            AnimatedVisibility(visible = state.status == GameStatus.Paused) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x88000000)),
                    contentAlignment = Alignment.Center
                ) {
                    OverlayPanel {
                        GradientText(text = "Paused", size = 28.sp, stroke = 6f)
                        Spacer(modifier = Modifier.height(12.dp))
                        PixelButton(text = "Continue") { viewModel.resumeGame() }
                        Spacer(modifier = Modifier.height(8.dp))
                        PixelButton(text = "Restart") { viewModel.startNewGame() }
                        Spacer(modifier = Modifier.height(8.dp))
                        PixelButton(text = "Menu") { navController.navigateUp() }
                    }
                }
            }

            AnimatedVisibility(visible = state.status == GameStatus.GameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x88000000)),
                    contentAlignment = Alignment.Center
                ) {
                    OverlayPanel {
                        GradientText(text = "Fell Down!", size = 32.sp, stroke = 7f)
                        Spacer(modifier = Modifier.height(8.dp))
                        GradientText(text = "Score: ${state.score}", size = 22.sp, stroke = 5f)
                        GradientText(text = "Best: ${state.bestScore}", size = 20.sp, stroke = 5f)
                        Spacer(modifier = Modifier.height(12.dp))
                        PixelButton(text = "Try again") { viewModel.startNewGame() }
                        Spacer(modifier = Modifier.height(8.dp))
                        PixelButton(text = "Menu") { navController.navigateUp() }
                    }
                }
            }
        }
    }
}
