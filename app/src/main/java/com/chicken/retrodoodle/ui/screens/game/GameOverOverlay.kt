package com.chicken.retrodoodle.ui.screens.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.retrodoodle.ui.components.GradientText
import com.chicken.retrodoodle.ui.components.OverlayPanel
import com.chicken.retrodoodle.ui.components.PixelButton

@Composable
fun GameOverOverlay(
    score: Int,
    bestScore: Int,
    onRetry: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OverlayPanel(modifier = modifier) {
        GradientText(text = "FELL DOWN!", size = 28.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Score: $score", textAlign = TextAlign.Center)
            Text(text = "Best: $bestScore", textAlign = TextAlign.Center)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PixelButton(text = "Try Again") { onRetry() }
            PixelButton(text = "Main Menu") { onMenu() }
        }
    }
}
