package com.victor_sml.playlistmaker.data.player

import android.media.MediaPlayer
import com.victor_sml.playlistmaker.data.Player
import com.victor_sml.playlistmaker.domain.api.PlayerInteractor.PlayerStateConsumer
import com.victor_sml.playlistmaker.domain.PlayerState
import com.victor_sml.playlistmaker.domain.PlayerState.DEFAULT
import com.victor_sml.playlistmaker.domain.PlayerState.PREPARED
import com.victor_sml.playlistmaker.domain.PlayerState.STARTED
import com.victor_sml.playlistmaker.domain.PlayerState.PAUSED
import com.victor_sml.playlistmaker.domain.PlayerState.PLAYBACK_COMPLETION

class PlayerImpl : Player {
    private val player = MediaPlayer()
    private var playerState: PlayerState = DEFAULT
        private set(value) {
            field = value
            playerStateConsumer.consume(value)
        }

    private lateinit var playerStateConsumer: PlayerStateConsumer

    override fun prepare(source: String, playerStateConsumer: PlayerStateConsumer) {
        this.playerStateConsumer = playerStateConsumer

        player.setDataSource(source)
        player.prepareAsync()

        player.setOnPreparedListener {
            playerState = PREPARED
        }

        player.setOnCompletionListener {
            playerState = PLAYBACK_COMPLETION
        }
    }

    override fun startPlayer() {
        player.start()
        playerState = STARTED
    }

    override fun pausePlayer() {
        player.pause()
        playerState = PAUSED
    }

    override fun releasePlayer() {
        player.release()
    }

    override fun getPlaybackProgress(): Int = player.currentPosition
}