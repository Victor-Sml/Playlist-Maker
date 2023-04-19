package com.victor_sml.playlistmaker.domain

import com.victor_sml.playlistmaker.domain.api.PlayerInteractor
import com.victor_sml.playlistmaker.domain.api.PlayerRepository

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {

    override fun preparePlayer(
        source: String,
        playerStateConsumer: PlayerInteractor.PlayerStateConsumer
    ) {
        repository.preparePlayer(source, playerStateConsumer)
    }

    override fun startPlayer() {
        repository.startPlayer()
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun getPlaybackProgress(): Int = repository.getPlaybackProgress()

    override fun releasePlayer() {
        repository.releasePlayer()
    }
}