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
import androidx.compose.foundation.layout.aspectRatio
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
            .background(Color(0x99000000)),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = modifier.fillMaxWidth(0.7f),
            contentAlignment = Alignment.TopEnd
        ) {
            Column(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xff84e4fa),
                                Color(0xff2d6b78)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .border(6.dp, Color.Black, RoundedCornerShape(28.dp))
                    .padding(horizontal = 26.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.Start
            ) {

                GradientText(
                    text = "Paused",
                    size = 46.sp,
                    stroke = 15f,
                )

                AudioSettingsSection(
                    musicOn = musicOn,
                    soundsOn = soundsOn,
                    onToggleMusic = onToggleMusic,
                    onToggleSounds = onToggleSounds,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    GlossyButton(
                        iconRes = R.drawable.ic_restart,
                        iconScale = 1f,
                        cornerRadius = 22.dp,
                        modifier = Modifier.size(70.dp).aspectRatio(1.1f),
                        onClick = onRestart
                    )

                    GlossyButton(
                        iconRes = R.drawable.ic_home,
                        iconScale = 1f,
                        cornerRadius = 22.dp,
                        modifier = Modifier.size(70.dp).aspectRatio(1.1f),
                        onClick = onMenu
                    )
                }
            }

            GlossyButton(
                iconRes = R.drawable.ic_close,
                iconScale = 1.3f,
                cornerRadius = 20.dp,
                modifier = Modifier
                    .offset(x = 20.dp, y = (-20).dp)
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
    Column() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GradientText(
                text = "Music",
                size = 32.sp,
                stroke = 15f,
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
                size = 32.sp,
                stroke = 15f,
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
    val bgColor = if (enabled) Color(0xFF8FFF57) else Color(0xFFDCF59C)
    val knobOffset by animateDpAsState(if (enabled) 42.dp else 2.dp)

    Box(
        modifier = modifier
            .width(66.dp)
            .height(36.dp)
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(3.dp, Color.Black, RoundedCornerShape(12.dp))
            .clickable { onToggle() }
    ) {
        Box(
            modifier = Modifier
                .offset(x = knobOffset, y = 8.dp)
                .size(20.dp)
                .background(Color(0xff193f0d), RoundedCornerShape(10.dp))
                .border(1.dp, Color.Black, RoundedCornerShape(10.dp))
        )
    }
}

