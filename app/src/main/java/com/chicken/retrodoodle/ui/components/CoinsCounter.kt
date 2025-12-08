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
fun CoinsCounter(
    value: Int,
    modifier: Modifier = Modifier,
    iconSize: Dp = 32.dp,
    textSize: TextUnit = 24.sp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AccentGlowTitle(
            text = "Collected: $value",
            size = textSize,
            stretchExpand = false,
            borderSize = 6f,
            brush = Brush.verticalGradient(
                listOf(Color(0xFFAEB0FD), Color(0xFFAEB0FD))
            )
        )

        Spacer(modifier = Modifier.width(6.dp))

        Image(
            painter = painterResource(id = R.drawable.item_gold_egg),
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            contentScale = ContentScale.Fit
        )
    }
}
