package com.chicken.retrodoodle.ui.screens.menu

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.chicken.retrodoodle.R
import com.chicken.retrodoodle.audio.AudioController
import com.chicken.retrodoodle.ui.components.PixelButton
import com.chicken.retrodoodle.ui.navigation.AppDestination
import com.chicken.retrodoodle.ui.theme.components.GradientText

@Composable
fun MenuScreen(navController: NavHostController, audio: AudioController) {
    LaunchedEffect(Unit) { audio.playMenuMusic() }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0E1C2A)) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg_game),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.35f
            )

            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GradientText(text = "Chicken Retro Doodle", size = 34.sp, stroke = 7f)
                Spacer(modifier = Modifier.height(6.dp))

                val transition = rememberInfiniteTransition(label = "chick")
                val bob by transition.animateFloat(
                    initialValue = -6f,
                    targetValue = 6f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(900),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "bob"
                )

                Image(
                    painter = painterResource(id = R.drawable.chicken_1),
                    contentDescription = null,
                    modifier = Modifier
                        .size(72.dp)
                        .padding(vertical = 6.dp)
                        .graphicsLayer { translationY = bob }
                )

                PixelButton(text = "Play") {
                    navController.navigate(AppDestination.Game)
                }
                PixelButton(text = "Skins") {
                    navController.navigate(AppDestination.Skins)
                }
            }
        }
    }
}
