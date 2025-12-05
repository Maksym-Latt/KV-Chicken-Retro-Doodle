package com.chicken.retrodoodle.core.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Offset
import com.chicken.retrodoodle.R
import com.chicken.retrodoodle.core.model.GameScaling.playerSizeDp
import kotlinx.parcelize.Parcelize

enum class PlatformType { Static, Moving, Cracked }

data class Platform(
    val id: Int,
    val position: Offset,
    val width: Float = 96f,
    val height: Float = 18f,
    val type: PlatformType = PlatformType.Static,
    val direction: Float = 1f,
    val isBroken: Boolean = false,
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
enum class PlayerSkin(
    @DrawableRes val sprite: Int,
    val price: Int,
) : Parcelable {
    Classic(R.drawable.chicken_1, price = 0),
    Blue(R.drawable.chicken_2, price = 40),
    Red(R.drawable.chicken_3, price = 60),
    Knight(R.drawable.chicken_4, price = 90);

    val title: String
        get() = when (this) {
            Classic -> "Classic Chick"
            Blue -> "Blue Sky"
            Red -> "Red Pixel"
            Knight -> "Knight Chick"
        }

    val image: Int
        get() = sprite
}

enum class GameStatus { Idle, Playing, Paused, GameOver }

val PlayerSize = playerSizeDp
