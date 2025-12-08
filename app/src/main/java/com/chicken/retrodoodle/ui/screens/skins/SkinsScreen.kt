package com.chicken.retrodoodle.ui.screens.skins

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.chicken.retrodoodle.R
import com.chicken.retrodoodle.audio.AudioPlaybackGateway
import com.chicken.retrodoodle.core.model.PlayerSkin
import com.chicken.retrodoodle.ui.components.CoinsCounter
import com.chicken.retrodoodle.ui.components.GlossyButton
import com.chicken.retrodoodle.ui.components.AccentGlowTitle

@Composable
fun SkinsScreen(
    navController: NavHostController,
    audio: AudioPlaybackGateway,
    viewModel: SkinsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { audio.launchMenuTrack() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(
                listOf(Color(0xFFB4FF63), Color(0xFF61C932))
            ))
    ) {

        Image(
            painter = painterResource(id = R.drawable.bg_shop_retro),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.safeDrawing.asPaddingValues()),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start
            ) {
                GlossyButton(
                    iconRes = R.drawable.ic_home,
                    modifier = Modifier.size(60.dp),
                    cornerRadius = 16.dp,
                    onClick = { navController.navigateUp() }
                )

                Spacer(modifier = Modifier.weight(1f))

                CoinsCounter(
                    value = state.eggs,
                    modifier = Modifier
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            SkinsGrid(
                skins = state.skins,
                selected = state.selectedSkin,
                onSelect = { viewModel.selectSkin(it) },
                onBuy = { viewModel.buySkin(it) }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
@Composable
fun SkinsGrid(
    skins: List<SkinUiModel>,
    selected: PlayerSkin,
    onSelect: (PlayerSkin) -> Unit,
    onBuy: (PlayerSkin) -> Unit
) {

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val cardWidth = maxWidth / 2 - 10.dp
        val cardHeight = cardWidth * 1.8f
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            skins.chunked(2).forEach { rowSkins ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowSkins.forEach { skin ->
                        SkinCard(
                            skin = skin,
                            isSelected = skin.skin == selected,
                            onSelect = { onSelect(skin.skin) },
                            onBuy = { onBuy(skin.skin) },
                            width = cardWidth,
                            height = cardHeight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SkinCard(
    skin: SkinUiModel,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onBuy: () -> Unit,
    width: Dp,
    height: Dp
) {
    val price = skin.price
    val owned = skin.owned

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xffd4f75b),
            Color(0xff76970c)
        )
    )

    val titleParts = skin.title.split(" ")

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .shadow(10.dp, RoundedCornerShape(20.dp))
            .background(brush = gradient, shape = RoundedCornerShape(20.dp))
            .border(3.dp, Color(0xFF1C2B31), RoundedCornerShape(20.dp))
            .padding(8.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()

        ) {
            Spacer(modifier = Modifier.weight(0.2f))

            Box(
                contentAlignment = Alignment.Center
            ) {
                if (titleParts.size >= 2) {

                    AccentGlowTitle(
                        text = titleParts[0],
                        size = 36.sp,
                        borderSize = 4f,
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFFAEB0FD), Color(0xFFAEB0FD))
                        )
                    )

                    AccentGlowTitle(
                        text = titleParts[1],
                        size = 36.sp,
                        borderSize = 4f,
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFFAEB0FD), Color(0xFFAEB0FD))
                        ),
                        modifier = Modifier.absoluteOffset(y = 36.dp)
                    )

                } else {
                    AccentGlowTitle(
                        text = skin.title,
                        size = 36.sp,
                        borderSize = 4f,
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFFAEB0FD), Color(0xFFAEB0FD))
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(3f))

            Box(
                modifier = Modifier
                    .weight(8f)
                    .wrapContentSize(Alignment.Center)
            ) {
                Image(
                    painter = painterResource(id = skin.image),
                    contentDescription = null,
                    modifier = Modifier.size(width * 0.85f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            when {
                owned && isSelected ->
                    GlossyButton(
                        text = "Selected",
                        modifier = Modifier
                            .fillMaxWidth(0.85f),
                        textSize = 24.sp,
                        enabled = false,
                        onClick = {}
                    )

                owned && !isSelected ->
                    GlossyButton(
                        text = "Select",
                        modifier = Modifier
                            .fillMaxWidth(0.85f),
                        textSize = 24.sp,
                        onClick = onSelect
                    )

                else ->
                    GlossyButton(
                        text = price.toString(),
                        iconRes = R.drawable.item_gold_egg,
                        modifier = Modifier
                            .fillMaxWidth(0.85f),
                        textSize = 24.sp,
                        onClick = onBuy
                    )
            }
            Spacer(modifier = Modifier.weight(0.2f))
        }
    }
}