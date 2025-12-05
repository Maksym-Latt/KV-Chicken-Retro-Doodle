package com.chicken.retrodoodle.ui.screens.game

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.retrodoodle.R
import com.chicken.retrodoodle.ui.components.GlossyButton
import com.chicken.retrodoodle.ui.components.GradientText

@Composable
fun GamePauseOverlay(
    musicOn: Boolean,
    soundsOn: Boolean,
    onToggleMusic: () -> Unit,
    onToggleSounds: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000)), // затемнение
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = modifier.fillMaxWidth(0.82f),
            contentAlignment = Alignment.TopEnd
        ) {

            // ---------- САМА ПАНЕЛЬ ----------
            Column(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFFB8F1FF),
                                Color(0xFF5AB6D4)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .border(6.dp, Color.Black, RoundedCornerShape(28.dp))
                    .padding(horizontal = 26.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.Start
            ) {

                // ---------- PAUSED ----------
                GradientText(
                    text = "Paused",
                    size = 38.sp,
                    stroke = 10f,
                )

                Spacer(Modifier.height(22.dp))

                AudioSettingsSection(
                    musicOn = musicOn,
                    soundsOn = soundsOn,
                    onToggleMusic = onToggleMusic,
                    onToggleSounds = onToggleSounds,
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    GlossyButton(
                        iconRes = R.drawable.ic_launcher_foreground,
                        iconScale = 1.4f,
                        cornerRadius = 22.dp,
                        modifier = Modifier.size(80.dp),
                        onClick = onRestart
                    )

                    GlossyButton(
                        iconRes = R.drawable.ic_launcher_foreground,
                        iconScale = 1.4f,
                        cornerRadius = 22.dp,
                        modifier = Modifier.size(80.dp),
                        onClick = onMenu
                    )
                }
            }

            GlossyButton(
                iconRes = R.drawable.ic_launcher_foreground,
                iconScale = 1.3f,
                cornerRadius = 40.dp,
                modifier = Modifier
                    .offset(x = 22.dp, y = (-26).dp)
                    .size(64.dp),
                onClick = onResume
            )
        }
    }
}

@Composable
fun AudioSettingsSection(
    musicOn: Boolean,
    soundsOn: Boolean,
    onToggleMusic: () -> Unit,
    onToggleSounds: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GradientText(
                text = "Music",
                size = 22.sp,
                stroke = 6f,
                expand = false
            )

            PixelSwitch(
                enabled = musicOn,
                onToggle = onToggleMusic
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GradientText(
                text = "Sounds",
                size = 22.sp,
                stroke = 6f,
                expand = false
            )

            PixelSwitch(
                enabled = soundsOn,
                onToggle = onToggleSounds
            )
        }
    }
}

@Composable
fun PixelSwitch(
    enabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (enabled) Color(0xFF8FFF57) else Color(0xFF5A5A5A)
    val knobOffset by animateDpAsState(if (enabled) 22.dp else 2.dp)

    Box(
        modifier = modifier
            .width(46.dp)
            .height(26.dp)
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
            .clickable { onToggle() }
    ) {
        Box(
            modifier = Modifier
                .offset(x = knobOffset, y = 2.dp)
                .size(20.dp)
                .background(Color(0xFFEFFFEF), RoundedCornerShape(10.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(10.dp))
        )
    }
}

