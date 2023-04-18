package com.victor_sml.playlistmaker.data

import com.victor_sml.playlistmaker.domain.api.PlayerInteractor.PlayerStateConsumer

interface Player {
    fun prepare(source: String, playerStateConsumer: PlayerStateConsumer)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getPlaybackProgress(): Int
}