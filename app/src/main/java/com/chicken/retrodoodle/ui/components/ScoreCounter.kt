package com.chicken.retrodoodle.ui.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import com.chicken.retrodoodle.R

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
        GradientText(
            text = "Score: $value",
            size = textSize,
            expand = false,
            stroke = 6f,
            brush = Brush.verticalGradient(
                listOf(Color(0xFFAEB0FD), Color(0xFFAEB0FD))
            )
        )
    }
}
