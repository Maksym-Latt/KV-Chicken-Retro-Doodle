package com.chicken.retrodoodle.ui.screens.game

sealed interface GameEvent {
    object Jump : GameEvent
    object Hit : GameEvent
    object Collect : GameEvent
    object Win : GameEvent
}
