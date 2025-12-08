package com.chicken.retrodoodle.ui.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row

@Composable
fun ScoreCounter(
    value: Int,
    modifier: Modifier = Modifier,
    textSize: TextUnit = 24.sp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AccentGlowTitle(
            text = "Score: $value",
            size = textSize,
            stretchExpand = false,
            borderSize = 6f,
            brush = Brush.verticalGradient(
                listOf(Color(0xFFAEB0FD), Color(0xFFAEB0FD))
            )
        )
    }
}
