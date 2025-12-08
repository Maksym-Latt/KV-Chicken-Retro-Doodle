package com.chicken.retrodoodle.core.model

import androidx.compose.ui.unit.dp

/**
 * Centralized scaling values for gameplay visuals and collisions.
 * Adjust [itemScale] to resize every sprite proportionally.
 * Adjust [collisionScale] (0f..1f) to tighten or loosen hit boxes.
 */
object GameScaling {
    const val itemScale: Float = 3f
    const val collisionScale: Float = 0.9f

    private const val BASE_PLAYER_WIDTH = 172f / itemScale
    private const val BASE_PLAYER_HEIGHT = 220f / itemScale
    private const val BASE_PLATFORM_WIDTH = 170f / itemScale
    private const val BASE_PLATFORM_HEIGHT = 44f / itemScale
    private const val BASE_COLLECTIBLE_WIDTH = 111f / itemScale
    private const val BASE_COLLECTIBLE_HEIGHT = 138f / itemScale
    private const val BASE_ENEMY_WIDTH = 150f / itemScale
    private const val BASE_ENEMY_HEIGHT = 112f / itemScale
    private const val BASE_PLATFORM_BUFFER = 4f

    val playerWidth: Float = BASE_PLAYER_WIDTH * itemScale
    val playerHeight: Float = BASE_PLAYER_HEIGHT * itemScale
    val playerHalfWidth: Float = playerWidth / 2f
    val playerHalfHeight: Float = playerHeight / 2f
    val playerSize: Float = playerHeight
    val playerSizeDp = playerHeight.dp

    val platformWidth: Float = BASE_PLATFORM_WIDTH * itemScale
    val platformHeight: Float = BASE_PLATFORM_HEIGHT * itemScale

    val collectibleWidth: Float = BASE_COLLECTIBLE_WIDTH * itemScale
    val collectibleHeight: Float = BASE_COLLECTIBLE_HEIGHT * itemScale
    val collectibleHalfWidth: Float = collectibleWidth / 2f
    val collectibleHalfHeight: Float = collectibleHeight / 2f
    val collectibleSize: Float = collectibleHeight
    val collectibleSizeDp = collectibleHeight.dp

    val enemyWidth: Float = BASE_ENEMY_WIDTH * itemScale
    val enemyHeight: Float = BASE_ENEMY_HEIGHT * itemScale
    val enemyHalfWidth: Float = enemyWidth / 2f
    val enemyHalfHeight: Float = enemyHeight / 2f
    val enemySizeDp = enemyWidth.dp

    val platformCollisionBuffer: Float = BASE_PLATFORM_BUFFER * itemScale

    val playerCollisionHalfWidth: Float = playerWidth * 0.35f
    val playerCollisionHalfHeight: Float = playerHeight * 0.35f
    val enemyCollisionHalfWidth: Float = enemyWidth * 0.35f
    val enemyCollisionHalfHeight: Float = enemyHeight * 0.35f
    val collectibleCollisionHalfWidth: Float = collectibleWidth * 0.4f
    val collectibleCollisionHalfHeight: Float = collectibleHeight * 0.4f
}
