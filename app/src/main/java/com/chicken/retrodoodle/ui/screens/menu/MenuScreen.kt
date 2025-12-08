package com.chicken.retrodoodle.ui.screens.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.chicken.retrodoodle.R
import com.chicken.retrodoodle.audio.AudioController
import com.chicken.retrodoodle.ui.components.GameTitle
import com.chicken.retrodoodle.ui.components.GlossyButton
import com.chicken.retrodoodle.ui.components.GradientText
import com.chicken.retrodoodle.ui.navigation.AppDestination

@Composable
fun MenuScreen(
    navController: NavHostController,
    audio: AudioController
) {
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
            iconRes = R.drawable.ic_settings,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
                .size(60.dp)
                .align(Alignment.TopStart),
            cornerRadius = 16.dp,
            onClick = { navController.navigate(AppDestination.Settings) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.safeDrawing.asPaddingValues()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            GameTitle()

            Spacer(modifier = Modifier.weight(1.2f))

            GlossyButton(
                iconRes = R.drawable.ic_play,
                cornerRadius = 20.dp,
                onClick = { navController.navigate(AppDestination.Game) },
                modifier = Modifier.fillMaxWidth(0.2f).aspectRatio(1.2f)
            )

            Spacer(modifier = Modifier.weight(3f))

            Image(
                painter = painterResource(id = R.drawable.chicken_1),
                contentDescription = null,
                modifier = Modifier.size(110.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            GlossyButton(
                text = "Shop",
                modifier = Modifier.fillMaxWidth(0.55f),
                onClick = { navController.navigate(AppDestination.Skins) }
            )

            Spacer(modifier = Modifier.weight(2f))
        }
    }
}
