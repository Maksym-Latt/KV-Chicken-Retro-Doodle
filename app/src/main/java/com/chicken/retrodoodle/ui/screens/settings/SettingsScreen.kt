package com.chicken.retrodoodle.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.chicken.retrodoodle.R
import com.chicken.retrodoodle.audio.AudioController
import com.chicken.retrodoodle.ui.components.GlossyButton
import com.chicken.retrodoodle.ui.components.GradientText
import com.chicken.retrodoodle.ui.components.OverlayPanel
import com.chicken.retrodoodle.ui.screens.game.AudioSettingsSection

@Composable
fun SettingsScreen(
    navController: NavHostController,
    audio: AudioController,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { audio.playMenuMusic() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E1C2A))
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_game),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        GlossyButton(
            iconRes = R.drawable.ic_launcher_foreground,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
                .size(60.dp)
                .align(Alignment.TopStart),
            cornerRadius = 16.dp,
            onClick = { navController.navigateUp() }
        )

        OverlayPanel(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .align(Alignment.Center)
        ) {
            GradientText(
                text = "Settings",
                size = 32.sp,
                stroke = 10f,
            )

            Spacer(modifier = Modifier.height(20.dp))

            AudioSettingsSection(
                musicOn = settings.musicEnabled,
                soundsOn = settings.soundsEnabled,
                onToggleMusic = { viewModel.toggleMusic() },
                onToggleSounds = { viewModel.toggleSounds() },
            )
        }
    }
}
