package com.victor_sml.playlistmaker.domain.api

import com.victor_sml.playlistmaker.domain.api.PlayerInteractor.PlayerStateConsumer

interface PlayerRepository {
    fun preparePlayer(source: String, playerStateConsumer: PlayerStateConsumer)
    fun startPlayer()
    fun pausePlayer()
    fun getPlaybackProgress(): Int
    fun releasePlayer()
}