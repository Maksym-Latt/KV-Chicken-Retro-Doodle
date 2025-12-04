package com.chicken.retrodoodle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HudBadge(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(6.dp)
    Row(
        modifier = modifier
            .shadow(4.dp, shape)
            .background(Color(0xAA0C1521), shape)
            .border(2.dp, Color(0xFF4BE7FF), shape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            GradientText(text = title, expand = false, size = 14.sp, stroke = 4f)
            GradientText(text = value, expand = false, size = 18.sp, stroke = 4f)
        }
    }
}
