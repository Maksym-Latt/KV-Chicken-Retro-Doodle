package com.chicken.retrodoodle.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chicken.retrodoodle.audio.AudioController
import com.chicken.retrodoodle.ui.screens.splash.SplashScreen
import com.chicken.retrodoodle.ui.screens.menu.MenuScreen
import com.chicken.retrodoodle.ui.screens.game.GameScreen
import com.chicken.retrodoodle.ui.screens.skins.SkinsScreen
import com.chicken.retrodoodle.ui.screens.settings.SettingsScreen

@Composable
fun AppRoot(audio: AudioController, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppDestination.Splash,
        modifier = modifier.fillMaxSize()
    ) {
        composable(AppDestination.Splash) {
            SplashScreen(navController = navController, audio = audio)
        }
        composable(AppDestination.Menu) {
            MenuScreen(navController = navController, audio = audio)
        }
        composable(AppDestination.Game) {
            GameScreen(navController = navController, audio = audio)
        }
        composable(AppDestination.Skins) {
            SkinsScreen(navController = navController, audio = audio)
        }
        composable(AppDestination.Settings) {
            SettingsScreen(navController = navController, audio = audio)
        }
    }
}
