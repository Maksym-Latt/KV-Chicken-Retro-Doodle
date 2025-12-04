package com.chicken.retrodoodle.ui.screens.skins

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
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
import com.chicken.retrodoodle.ui.components.PixelButton
import com.chicken.retrodoodle.ui.components.SkinCard
import com.chicken.retrodoodle.ui.theme.components.GradientText

@Composable
fun SkinsScreen(navController: NavHostController, viewModel: SkinsViewModel = hiltViewModel()) {
    val selected by viewModel.selected.collectAsState()

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0E1C2A)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.bg_shop),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.45f
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientText(text = "Skins", size = 30.sp, stroke = 7f)
                PlayerSkin.values().forEach { skin ->
                    SkinCard(
                        skin = skin,
                        selected = skin == selected,
                        onClick = { viewModel.selectSkin(it) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                PixelButton(text = "Back") { navController.navigateUp() }
            }
        }
    }
}
