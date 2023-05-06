package com.victor_sml.playlistmaker.player.domain.api

import com.victor_sml.playlistmaker.player.domain.api.PlayerInteractor.StateObserver

interface PlayerRepository {
    fun preparePlayer(source: String, stateObserver: StateObserver)
    fun startPlayer()
    fun pausePlayer()
    fun getPlaybackProgress(): Int
    fun releasePlayer()
}