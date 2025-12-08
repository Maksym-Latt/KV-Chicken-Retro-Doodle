package com.chicken.retrodoodle.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.retrodoodle.R

@Composable
fun GameHud(
    score: Int,
    levelEggs: Int,
    onPause: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(WindowInsets.safeDrawing.asPaddingValues()),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        GlossyButton(
            iconRes = R.drawable.ic_pause,
            modifier = Modifier.size(60.dp),
            cornerRadius = 16.dp,
            onClick = onPause
        )

        Spacer(modifier = Modifier.weight(1f))


        Box(
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.End) {

                CoinsCounter(
                    value = levelEggs,
                    modifier = Modifier
                )

                ScoreCounter(
                    value = score,
                    modifier = Modifier
                )
            }
        }
    }
}
