package com.chicken.retrodoodle.ui.screens.game

import androidx.compose.ui.geometry.Offset
import com.chicken.retrodoodle.core.model.Collectible
import com.chicken.retrodoodle.core.model.Enemy
import com.chicken.retrodoodle.core.model.GameStatus
import com.chicken.retrodoodle.core.model.Platform
import com.chicken.retrodoodle.core.model.Player
import com.chicken.retrodoodle.core.model.PlayerSkin

data class GameUiState(
    val status: GameStatus = GameStatus.Idle,
    val player: Player = Player(),
    val platforms: List<Platform> = emptyList(),
    val enemies: List<Enemy> = emptyList(),
    val eggs: List<Collectible> = emptyList(),
    val score: Int = 0,
    val bestScore: Int = 0,
    val eggsCollected: Int = 0,
    val cameraHeight: Float = 0f,
    val selectedSkin: PlayerSkin = PlayerSkin.Classic
)

fun Player.withPosition(x: Float, y: Float) = copy(position = Offset(x, y))
