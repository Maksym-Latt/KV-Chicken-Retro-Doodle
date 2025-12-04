package com.chicken.retrodoodle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PixelButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    expand: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(vertical = 12.dp, horizontal = 18.dp),
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(6.dp)
    Box(
        modifier = modifier
            .then(if (expand) Modifier.fillMaxWidth() else Modifier)
            .shadow(6.dp, shape)
            .background(if (enabled) Color(0xFF1B2B3B) else Color(0xFF555555), shape)
            .border(3.dp, Color(0xFF4BE7FF), shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        GradientText(
            text = text,
            expand = true,
            size = MaterialTheme.typography.titleLarge.fontSize,
            stroke = 6f
        )
    }
}
