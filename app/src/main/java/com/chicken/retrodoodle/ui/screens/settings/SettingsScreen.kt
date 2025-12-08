package com.chicken.retrodoodle.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.chicken.retrodoodle.R
import com.chicken.retrodoodle.audio.AudioPlaybackGateway
import com.chicken.retrodoodle.ui.components.GlossyButton
import com.chicken.retrodoodle.ui.components.AccentGlowTitle
import com.chicken.retrodoodle.ui.navigation.AppDestination
import com.chicken.retrodoodle.ui.screens.game.AudioSettingsSection

@Composable
fun SettingsScreen(
    navController: NavHostController,
    audio: AudioPlaybackGateway,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { audio.launchMenuTrack() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000)),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.bg_game_retro),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000)),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier.fillMaxWidth(0.7f),
                contentAlignment = Alignment.TopEnd
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color(0xff84e4fa),
                                    Color(0xff2d6b78)
                                )
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .border(6.dp, Color.Black, RoundedCornerShape(28.dp))
                        .padding(horizontal = 26.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    AccentGlowTitle(
                        text = "Settings",
                        size = 46.sp,
                        borderSize = 15f,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    AudioSettingsSection(
                        musicOn = settings.musicEnabled,
                        soundsOn = settings.soundsEnabled,
                        onToggleMusic = { viewModel.toggleMusic() },
                        onToggleSounds = { viewModel.toggleSounds() },
                    )
                }

                GlossyButton(
                    iconRes = R.drawable.ic_close,
                    iconScale = 1.3f,
                    cornerRadius = 20.dp,
                    modifier = Modifier
                        .offset(x = 20.dp, y = (-20).dp)
                        .size(64.dp),
                    onClick = { navController.navigate(AppDestination.Menu) }
                )
            }
        }
    }
}
