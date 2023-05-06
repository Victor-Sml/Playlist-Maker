package com.victor_sml.playlistmaker.player.domain.api

interface PlayerInteractor {
    fun preparePlayer(source: String, stateObserver: StateObserver)
    fun startPlayer()
    fun pausePlayer()
    fun getPlaybackProgress(): Int
    fun releasePlayer()

    interface StateObserver {
        fun onPrepared()
        fun onStarted()
        fun onPause()
        fun onCompletion()
    }
}