package com.chicken.retrodoodle.core.config

/**
 * Centralized tuning knobs for the in-game object sizes and collision bounds.
 *
 * [sizeScale] multiplies every sprite dimension while keeping proportions.
 * [collisionScale] shrinks or expands hit boxes relative to the visual size (0f..1f).
 */
object GameDimensions {
    const val sizeScale: Float = 1.0f
    const val collisionScale: Float = 0.9f

    val playerSize: Float = 32f * sizeScale
    val platformWidth: Float = 104f * sizeScale
    val platformHeight: Float = 20f * sizeScale
    val basePlatformWidth: Float = 110f * sizeScale
    val basePlatformHeight: Float = 20f * sizeScale
    val collectibleSize: Float = 24f * sizeScale
    val enemySize: Float = 32f * sizeScale
}
