package com.victor_sml.playlistmaker.player.data.player

import android.media.MediaPlayer
import com.victor_sml.playlistmaker.player.data.api.Player
import com.victor_sml.playlistmaker.player.domain.api.PlayerInteractor.StateObserver

class PlayerImpl : Player {
    private val player = MediaPlayer()
    private lateinit var stateObserver: StateObserver

    override fun prepare(source: String, stateObserver: StateObserver) {
        this.stateObserver = stateObserver
        player.setDataSource(source)
        player.prepareAsync()

        player.setOnPreparedListener {
            stateObserver.onPrepared()
        }

        player.setOnCompletionListener {
            stateObserver.onCompletion()
        }
    }

    override fun startPlayer() {
        player.start()
        stateObserver.onStarted()
    }

    override fun pausePlayer() {
        player.pause()
        stateObserver.onPause()
    }

    override fun releasePlayer() {
        player.release()
    }

    override fun getPlaybackProgress(): Int = player.currentPosition
}