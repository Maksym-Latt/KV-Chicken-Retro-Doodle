package com.chicken.retrodoodle.audio

interface AudioPlaybackGateway {
    fun launchMenuTrack()
    fun launchSessionTrack()
    fun haltAllStreams()
    fun suspendStream()
    fun resumeStream()

    fun adjustMusicLevel(percent: Int)
    fun adjustEffectsLevel(percent: Int)

    fun triggerVictoryCue()
    fun triggerFailureCue()
    fun triggerPickupCue()
    fun triggerJumpCue()
}
