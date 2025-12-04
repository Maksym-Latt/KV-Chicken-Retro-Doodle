package com.chicken.retrodoodle.audio

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AudioControllerEntryPoint {
    fun audioController(): AudioController
}