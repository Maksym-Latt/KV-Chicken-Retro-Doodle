package com.chicken.retrodoodle.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.retrodoodle.core.model.PlayerSkin
import com.chicken.retrodoodle.ui.theme.components.GradientText

@Composable
fun SkinCard(
    skin: PlayerSkin,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (PlayerSkin) -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, shape)
            .background(Color(if (selected) 0xFF0F2435 else 0xAA0F2435), shape)
            .border(3.dp, if (selected) Color(0xFF4BE7FF) else Color(0xFF3A4A55), shape)
            .clickable { onClick(skin) }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        GradientText(text = skin.title, size = 18.sp, stroke = 5f)
        Image(
            painter = painterResource(id = skin.sprite),
            contentDescription = skin.title,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        GradientText(text = if (selected) "Selected" else "Tap to equip", size = 14.sp, stroke = 4f)
    }
}
