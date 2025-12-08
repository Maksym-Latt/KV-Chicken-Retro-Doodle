package com.chicken.retrodoodle.ui.screens.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.chicken.retrodoodle.R
import com.chicken.retrodoodle.audio.AudioPlaybackGateway
import com.chicken.retrodoodle.ui.components.GameTitle
import com.chicken.retrodoodle.ui.components.GlossyButton
import com.chicken.retrodoodle.ui.navigation.AppDestination

@Composable
fun MenuScreen(
    navController: NavHostController,
    audio: AudioPlaybackGateway,
    viewModel: MenuViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { audio.launchMenuTrack() }
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E1C2A))
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_game_retro),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.safeDrawing.asPaddingValues()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {


                GlossyButton(
                    iconRes = R.drawable.ic_settings,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp)
                        .size(60.dp),
                    cornerRadius = 16.dp,
                    onClick = { navController.navigate(AppDestination.Settings) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            GameTitle()

            Spacer(modifier = Modifier.weight(0.5f))

            GlossyButton(
                iconRes = R.drawable.ic_play,
                cornerRadius = 20.dp,
                onClick = { navController.navigate(AppDestination.Game) },
                modifier = Modifier.fillMaxWidth(0.2f).aspectRatio(1.2f)
            )

            Spacer(modifier = Modifier.weight(3f))

            Image(
                painter = painterResource(id = state.selectedSkin.image),
                contentDescription = null,
                modifier = Modifier.size(110.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            GlossyButton(
                text = "Shop",
                modifier = Modifier.fillMaxWidth(0.55f),
                onClick = { navController.navigate(AppDestination.Skins) }
            )

            Spacer(modifier = Modifier.weight(3f))
        }
    }
}
