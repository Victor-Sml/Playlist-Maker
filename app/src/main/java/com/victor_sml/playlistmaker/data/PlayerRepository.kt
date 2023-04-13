package com.victor_sml.playlistmaker.data

import androidx.lifecycle.LiveData

interface PlayerRepository {
    val progress: LiveData<Int>
    val controllerAvailability: LiveData<Boolean>
    val controllerAction: LiveData<ControllerAction>

    fun playbackControl()
    fun pausePlayer()
    fun releasePlayer()

    enum class ControllerAction {
        PLAY,
        PAUSE
    }

    interface PlayerCallback {
        fun onPlayerPrepared()
        fun onPlaybackCompleted()
        fun onPlaybackProgressChanged(progress: Int)
    }
}