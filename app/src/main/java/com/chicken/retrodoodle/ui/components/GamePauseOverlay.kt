package com.chicken.retrodoodle.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GamePauseOverlay(
    onContinue: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OverlayPanel(modifier = modifier) {
        GradientText(text = "Paused", size = 26.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PixelButton(text = "Continue") { onContinue() }
            PixelButton(text = "Restart") { onRestart() }
            PixelButton(text = "Main Menu") { onMenu() }
        }
    }
}
