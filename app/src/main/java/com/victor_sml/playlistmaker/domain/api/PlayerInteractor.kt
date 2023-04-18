package com.victor_sml.playlistmaker.domain.api

import com.victor_sml.playlistmaker.domain.PlayerState

interface PlayerInteractor {
    fun preparePlayer(source: String, playerStateConsumer: PlayerStateConsumer)
    fun startPlayer()
    fun pausePlayer()
    fun getPlaybackProgress(): Int
    fun releasePlayer()

    interface PlayerStateConsumer {
        fun consume(state: PlayerState)
    }
}