package com.chicken.retrodoodle.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.chicken.retrodoodle.R
import com.chicken.retrodoodle.audio.AudioController
import com.chicken.retrodoodle.ui.components.GameTitle
import com.chicken.retrodoodle.ui.navigation.AppDestination
import com.chicken.retrodoodle.ui.screens.menu.MenuViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController,
) {
    LaunchedEffect(Unit) {
        delay(3_000)
        navController.navigate(AppDestination.Menu) {
            popUpTo(AppDestination.Splash) { inclusive = true }
        }
    }

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .wrapContentHeight(Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameTitle()

            Spacer(modifier = Modifier.height(48.dp))

            Image(
                painter = painterResource(id = R.drawable.chicken_1),
                contentDescription = null,
                modifier = Modifier.size(140.dp)
            )
        }
    }
}
