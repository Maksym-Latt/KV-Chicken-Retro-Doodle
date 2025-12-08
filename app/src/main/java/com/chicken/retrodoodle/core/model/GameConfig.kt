package com.chicken.retrodoodle.core.model

/**
 * Tunable gameplay constants to quickly iterate on feel and difficulty.
 * Adjust values here to change movement responsiveness without touching UI.
 */
object GameConfig {
    const val gravity = 2100f
    const val jumpForce = 1100f
    const val tiltAcceleration = 1250f
    const val maxHorizontalSpeed = 780f
    const val movingPlatformSpeed = 150f
    const val bugSpeed = 220f
    const val debugCollisionOverlay = true
}
