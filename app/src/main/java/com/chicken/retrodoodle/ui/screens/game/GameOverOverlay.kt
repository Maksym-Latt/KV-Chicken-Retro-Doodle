package com.chicken.retrodoodle.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.retrodoodle.ui.components.GlossyButton
import com.chicken.retrodoodle.ui.components.GradientText
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment

@Composable
fun GameOverOverlay(
    score: Int,
    bestScore: Int,
    onRetry: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xAA000000))
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(3f))
            GradientText(
                text = "FELL\nDOWN!",
                size = 56.sp,
                stroke = 20f,
                alignment = TextAlign.Center,
                expand = false,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            GradientText(
                text = "SCORE: $score",
                size = 42.sp,
                stroke = 15f,
                alignment = TextAlign.Center,
                expand = false,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            GradientText(
                text = "Best score: $bestScore",
                size = 28.sp,
                stroke = 15f,
                alignment = TextAlign.Center,
                expand = false,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            GlossyButton(
                text = "TRY AGAIN",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                textSize = 28.sp,
            ) { onRetry() }

            Spacer(modifier = Modifier.height(16.dp))

            GlossyButton(
                text = "MAIN MENU",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                textSize = 28.sp,
            ) { onMenu() }
            Spacer(modifier = Modifier.weight(3f))
        }
    }
}
