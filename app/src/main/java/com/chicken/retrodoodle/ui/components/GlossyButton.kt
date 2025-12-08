package com.chicken.retrodoodle.ui.components

import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind

import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*

@Composable
fun GlossyButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    iconRes: Int? = null,
    enabled: Boolean = true,
    cornerRadius: Dp = 18.dp,
    brush: Brush = Brush.verticalGradient(
        listOf(Color(0xFF82E1F6), Color(0xFF306F7D))
    ),
    disabledBrush: Brush = Brush.verticalGradient(
        listOf(Color(0x9982E1F6), Color(0x99306f7d))
    ),
    borderColor: Color = Color(0xFF1C2B31),
    textSize: TextUnit = 24.sp,
    iconScale: Float = 1f,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val alpha = if (enabled) 1f else 0.8f
    val iconSize = 32.dp * iconScale

    Box(
        modifier = modifier
            .background(if (enabled) brush else disabledBrush, shape)
            .border(3.dp, borderColor, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .drawBehind {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.55f * alpha),
                            Color.Transparent
                        )
                    ),
                    cornerRadius = CornerRadius(cornerRadius.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            if (iconRes != null && text == null) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize)
                        .alpha(alpha),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Color(0xff000000))
                )
            }

            if (iconRes != null && text != null) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(iconSize)
                            .alpha(alpha),
                        contentScale = ContentScale.Fit
                    )

                    AccentGlowTitle(
                        text = text,
                        size = textSize,
                        borderSize = 10f,
                        modifier = Modifier.alpha(alpha),
                        stretchExpand = false,
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFFAEB0FD), Color(0xFFAEB0FD))
                        )
                    )
                }
            }

            if (iconRes == null && text != null) {
                AccentGlowTitle(
                    text = text,
                    size = textSize,
                    borderSize = 10f,
                    modifier = Modifier.alpha(alpha),
                    stretchExpand = false,
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFAEB0FD), Color(0xFFAEB0FD))
                    )
                )
            }
        }
    }
}