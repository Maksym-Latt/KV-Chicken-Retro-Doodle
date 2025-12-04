package com.chicken.retrodoodle.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GameHud(
    score: Int,
    eggs: Int,
    onPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HudBadge(title = "Height", value = score.toString())
        HudBadge(title = "Eggs", value = eggs.toString())
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onPause) {
            Surface(color = Color(0xAA0C1521), shape = MaterialTheme.shapes.small) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause",
                    modifier = Modifier.padding(10.dp).size(18.dp),
                    tint = Color(0xFF4BE7FF)
                )
            }
        }
    }
}
