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
fun GameIntroOverlay(onStart: () -> Unit, modifier: Modifier = Modifier) {
    OverlayPanel(modifier = modifier) {
        GradientText(text = "How to Play", size = 26.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Tilt your phone to move left/right", textAlign = TextAlign.Center)
            Text(text = "Jump automatically when you land", textAlign = TextAlign.Center)
            Text(text = "Stomp bugs, grab eggs and climb!", textAlign = TextAlign.Center)
        }
        Spacer(modifier = Modifier.height(16.dp))
        PixelButton(text = "Start") { onStart() }
    }
}
