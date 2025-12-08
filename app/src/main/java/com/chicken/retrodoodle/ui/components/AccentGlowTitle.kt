package com.chicken.retrodoodle.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.chicken.retrodoodle.R

@Composable
fun AccentGlowTitle(
    text: String,
    modifier: Modifier = Modifier,

    stretchExpand: Boolean = true,
    textAlign: TextAlign = TextAlign.Center,

    size: TextUnit = 48.sp,
    borderSize: Float = 8f,
    borderColor: Color = Color(0xff040300),

    brush: Brush  = Brush.verticalGradient(
        listOf(Color(0xFFAEB0FD), Color(0xFFAEB0FD))
    )
) {
    val typefacePack = remember {
        FontFamily(
            Font(
                R.font.fraunces,
                weight = FontWeight.Normal
            )
        )
    }

    val textPreset = MaterialTheme.typography.displayLarge.copy(
        fontSize = size,
        fontWeight = FontWeight.Normal,
        fontFamily = typefacePack,
        textAlign = textAlign
    )

    val widthScope = if (stretchExpand) Modifier.fillMaxWidth() else Modifier

    Box(modifier = modifier) {

        val gradientText = buildAnnotatedString {
            withStyle(style = SpanStyle(brush = brush)) {
                append(text)
            }
        }

        Text(
            text = text,
            style = textPreset.copy(
                color = borderColor,
                drawStyle = Stroke(
                    width = borderSize,
                    join = StrokeJoin.Round
                )
            ),
            lineHeight = size * 1.8f,
            modifier = widthScope
        )

        Text(
            text = gradientText,
            style = textPreset,
            color = Color.White,
            lineHeight = size * 1.8f,
            modifier = widthScope
        )
    }
}
