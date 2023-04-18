package com.victor_sml.playlistmaker.data

import com.victor_sml.playlistmaker.domain.api.PlayerInteractor.PlayerStateConsumer
import com.victor_sml.playlistmaker.domain.api.PlayerRepository

class PlayerRepositoryImpl(
    private val player: Player
) : PlayerRepository {

    override fun preparePlayer(source: String, playerStateConsumer: PlayerStateConsumer) {
        player.prepare(source, playerStateConsumer)
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