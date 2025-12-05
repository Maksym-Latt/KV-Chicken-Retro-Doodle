package com.chicken.retrodoodle.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PixelButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    iconRes: Int? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 14.dp),

    // ---- Цвета ----
    backgroundColor: Color = Color(0xFF1B2B3B),
    borderColor: Color = Color(0xFF6DF2FF),
    textSize: TextUnit = 26.sp,
    stroke: Float = 6f,

    // ---- Другие параметры ----
    cornerRadius: Dp = 10.dp,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .shadow(10.dp, shape)
            .background(backgroundColor, shape)
            .border(3.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ---------------- ИКОНКА ----------------
            if (iconRes != null) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // ---------------- ТЕКСТ ----------------
            if (text != null) {
                GradientText(
                    text = text,
                    size = textSize,
                    stroke = stroke,
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFAEB0FD), Color(0xFFAEB0FD))
                    )
                )
            }
        }
    }
}