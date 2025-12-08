package com.chicken.retrodoodle.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.retrodoodle.R
import com.chicken.retrodoodle.ui.components.GlossyButton
import com.chicken.retrodoodle.ui.components.GradientText

@Composable
fun GameIntroOverlay(
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000)),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = modifier
                .fillMaxWidth(0.82f),
            contentAlignment = Alignment.TopEnd
        ) {
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GradientText(
                    text = "How to Play",
                    size = 40.sp,
                    stroke = 10f,
                )

                Spacer(modifier = Modifier.height(18.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    IntroTip("Tilt your phone to move")
                    IntroTip("Jump automatically on landing")
                    IntroTip("Stomp bugs, grab eggs and climb!")
                }

                Spacer(modifier = Modifier.height(28.dp))

                GlossyButton(
                    text = "Start",
                    textSize = 28.sp,
                    cornerRadius = 22.dp,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(64.dp),
                    onClick = onStart
                )
            }
        }
    }
}

@Composable
fun IntroTip(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.item_gold_egg), // Можешь заменить на другую
            contentDescription = null,
            tint =  Color(0xffaeb0fd),
            modifier = Modifier.size(22.dp)
        )

        GradientText(
            text = text,
            size = 16.sp,
            stroke = 10f,
            alignment  =  TextAlign.Start,
        )
    }
}
