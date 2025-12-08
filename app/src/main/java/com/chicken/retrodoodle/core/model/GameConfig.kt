package com.chicken.retrodoodle.core.model

/**
 * Tunable gameplay constants to quickly iterate on feel and difficulty.
 * Adjust values here to change movement responsiveness without touching UI.
 */
object GameConfig {
    const val gravity = 2600f
    const val jumpForce = 2200f
    const val tiltAcceleration = 2250f
    const val maxHorizontalSpeed = 1080f
    const val movingPlatformSpeed = 150f
    const val bugSpeed = 120f
    const val debugCollisionOverlay = true
}
