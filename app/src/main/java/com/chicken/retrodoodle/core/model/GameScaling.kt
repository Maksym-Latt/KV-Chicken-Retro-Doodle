package com.chicken.retrodoodle.core.model

import androidx.compose.ui.unit.dp

/**
 * Centralized scaling values for gameplay visuals and collisions.
 * Adjust [itemScale] to resize every sprite proportionally.
 * Adjust [collisionScale] (0f..1f) to tighten or loosen hit boxes.
 */
object GameScaling {
    const val itemScale: Float = 1f
    const val collisionScale: Float = 0.9f

    private const val BASE_PLAYER_SIZE = 32f
    private const val BASE_PLATFORM_WIDTH = 104f
    private const val BASE_PLATFORM_HEIGHT = 20f
    private const val BASE_COLLECTIBLE_SIZE = 24f
    private const val BASE_ENEMY_WIDTH = 32f
    private const val BASE_ENEMY_HEIGHT = 32f
    private const val BASE_PLATFORM_BUFFER = 4f
    private const val BASE_ENEMY_COLLISION_HALF_WIDTH = 16f
    private const val BASE_ENEMY_COLLISION_HALF_HEIGHT = 18f
    private const val BASE_COLLECTIBLE_COLLISION_HALF_WIDTH = 14f
    private const val BASE_COLLECTIBLE_COLLISION_HALF_HEIGHT = 16f

    val playerSize: Float = BASE_PLAYER_SIZE * itemScale
    val playerSizeDp = playerSize.dp

    val platformWidth: Float = BASE_PLATFORM_WIDTH * itemScale
    val platformHeight: Float = BASE_PLATFORM_HEIGHT * itemScale

    val collectibleSize: Float = BASE_COLLECTIBLE_SIZE * itemScale
    val collectibleSizeDp = collectibleSize.dp

    val enemyWidth: Float = BASE_ENEMY_WIDTH * itemScale
    val enemyHeight: Float = BASE_ENEMY_HEIGHT * itemScale
    val enemySizeDp = enemyWidth.dp

    val platformCollisionBuffer: Float = BASE_PLATFORM_BUFFER * itemScale

    val playerCollisionRadius: Float = playerSize * 0.5f * collisionScale
    val enemyCollisionHalfWidth: Float = BASE_ENEMY_COLLISION_HALF_WIDTH * itemScale * collisionScale
    val enemyCollisionHalfHeight: Float = BASE_ENEMY_COLLISION_HALF_HEIGHT * itemScale * collisionScale
    val collectibleCollisionHalfWidth: Float = BASE_COLLECTIBLE_COLLISION_HALF_WIDTH * itemScale * collisionScale
    val collectibleCollisionHalfHeight: Float = BASE_COLLECTIBLE_COLLISION_HALF_HEIGHT * itemScale * collisionScale
}
