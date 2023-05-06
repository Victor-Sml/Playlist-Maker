package com.victor_sml.playlistmaker.player.data.api

import com.victor_sml.playlistmaker.player.domain.api.PlayerInteractor

interface Player {
    fun prepare(source: String, stateObserver: PlayerInteractor.StateObserver)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getPlaybackProgress(): Int
}