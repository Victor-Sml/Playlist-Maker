package com.victor_sml.playlistmaker.player.data

import com.victor_sml.playlistmaker.player.data.api.Player
import com.victor_sml.playlistmaker.player.domain.api.PlayerInteractor.StateObserver
import com.victor_sml.playlistmaker.player.domain.api.PlayerRepository

class PlayerRepositoryImpl(
    private val player: Player
) : PlayerRepository {

    override fun preparePlayer(source: String, stateObserver: StateObserver) {
        player.prepare(source, stateObserver)
    }

    override fun startPlayer() {
        player.startPlayer()
    }

    override fun pausePlayer() {
        player.pausePlayer()
    }

    override fun getPlaybackProgress(): Int = player.getPlaybackProgress()

    override fun releasePlayer() {
        player.releasePlayer()
    }
}