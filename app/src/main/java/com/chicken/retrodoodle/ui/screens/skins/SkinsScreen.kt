package com.chicken.retrodoodle.ui.screens.skins

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.chicken.retrodoodle.R
import com.chicken.retrodoodle.audio.AudioController
import com.chicken.retrodoodle.core.model.PlayerSkin
import com.chicken.retrodoodle.ui.components.GlossyButton
import com.chicken.retrodoodle.ui.components.PixelButton
import com.chicken.retrodoodle.ui.components.SkinCard
import com.chicken.retrodoodle.ui.components.GradientText

@Composable
fun SkinsScreen(
    navController: NavHostController,
    audio: AudioController,
    viewModel: SkinsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { audio.playMenuMusic() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E1C2A))
    ) {

        // -------------------------
        // ФОН
        // -------------------------
        Image(
            painter = painterResource(id = R.drawable.bg_shop),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // -------------------------
        // КНОПКА НАЗАД
        // -------------------------
        GlossyButton(
            iconRes = R.drawable.ic_launcher_foreground,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
                .size(60.dp)
                .align(Alignment.TopStart),
            cornerRadius = 16.dp,
            onClick = { navController.navigateUp() }
        )

        // -------------------------
        // СОДЕРЖИМОЕ
        // -------------------------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // -------------------------
            // ТЕКУЩИЕ ОЧКИ
            // -------------------------
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                GradientText(text = "Collected: ${state.eggs}", size = 26.sp, stroke = 6f)
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_coin),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // -------------------------
            // СКИНЫ
            // -------------------------
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
    skins: List<PlayerSkin>,
    selected: PlayerSkin,
    onSelect: (PlayerSkin) -> Unit,
    onBuy: (PlayerSkin) -> Unit
) {

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        skins.chunked(2).forEach { rowSkins ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowSkins.forEach { skin ->
                    SkinCard(
                        skin = skin,
                        isSelected = skin == selected,
                        onSelect = { onSelect(skin) },
                        onBuy = { onBuy(skin) }
                    )
                }
            }
        }
    }
}
@Composable
fun SkinCard(
    skin: PlayerSkin,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onBuy: () -> Unit
) {
    val price = skin.price
    val owned = skin.owned

    Box(
        modifier = Modifier
            .width(160.dp)
            .height(250.dp)
            .shadow(12.dp, RoundedCornerShape(22.dp))
            .background(Color(0xFFACFF63), RoundedCornerShape(22.dp))
            .border(4.dp, Color(0xFF1C2B31), RoundedCornerShape(22.dp))
            .padding(10.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {

            GradientText(text = skin.title, size = 20.sp, stroke = 6f)

            Image(
                painter = painterResource(id = skin.image),
                contentDescription = null,
                modifier = Modifier.size(110.dp)
            )

            when {
                owned && isSelected ->
                    GlossyButton(text = "Selected", enabled = false)

                owned && !isSelected ->
                    GlossyButton(text = "Select", onClick = onSelect)

                else ->
                    GlossyButton(
                        text = "${price}",
                        iconRes = R.drawable.ic_coin,
                        onClick = onBuy
                    )
            }
        }
    }
}



