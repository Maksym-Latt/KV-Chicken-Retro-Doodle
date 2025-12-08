package com.chicken.retrodoodle.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameTitle(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AccentGlowTitle(
            text = "CHICKEN",
            size = 62.sp,
            borderSize = 7f,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xff79d6e9),
                    Color(0xff326d78)
                )
            )
        )

        AccentGlowTitle(
            text = "RETRO",
            size = 62.sp,
            borderSize = 7f,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xffc3e54c),
                    Color(0xff83a417)
                )
            ),
            modifier = Modifier.offset(y = (-75).dp)
        )

        AccentGlowTitle(
            text = "DOODLE",
            size = 62.sp,
            borderSize = 7f,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xff7d9432),
                    Color(0xff4c600e)
                )
            ),
            modifier = Modifier.offset(y = (-145).dp)
        )
    }
}
