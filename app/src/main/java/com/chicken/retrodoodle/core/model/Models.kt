package com.chicken.retrodoodle.core.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.chicken.retrodoodle.R
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

enum class PlatformType { Static, Moving, Cracked }

data class Platform(
    val id: Int,
    val position: Offset,
    val width: Float = 96f,
    val height: Float = 18f,
    val type: PlatformType = PlatformType.Static,
    val direction: Float = 1f
)

data class Enemy(
    val id: Int,
    val position: Offset,
    val speed: Float = 40f,
    val direction: Float = 1f
)

data class Collectible(
    val id: Int,
    val position: Offset
)

data class Player(
    val position: Offset = Offset.Zero,
    val velocity: Offset = Offset.Zero,
    val skin: PlayerSkin = PlayerSkin.Classic
)

@Parcelize
enum class PlayerSkin(@DrawableRes val sprite: Int) : Parcelable {
    Classic(R.drawable.chicken_1),
    Blue(R.drawable.chicken_2),
    Red(R.drawable.chicken_3),
    Knight(R.drawable.chicken_4);

    val title: String
        get() = when (this) {
            Classic -> "Classic Chick"
            Blue -> "Blue Sky Hen"
            Red -> "Red Pixel Hen"
            Knight -> "Knight Chick"
        }
}

enum class GameStatus { Idle, Playing, Paused, GameOver }

val PlayerSize = 32.dp
