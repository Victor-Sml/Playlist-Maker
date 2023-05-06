package com.victor_sml.playlistmaker.player.domain

import com.victor_sml.playlistmaker.player.domain.api.PlayerInteractor
import com.victor_sml.playlistmaker.player.domain.api.PlayerInteractor.StateObserver
import com.victor_sml.playlistmaker.player.domain.api.PlayerRepository

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {

    override fun preparePlayer(source: String, stateObserver: StateObserver) {
        repository.preparePlayer(source, stateObserver)
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