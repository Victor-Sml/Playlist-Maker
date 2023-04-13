package com.victor_sml.playlistmaker.data.player

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.victor_sml.playlistmaker.data.Player
import com.victor_sml.playlistmaker.data.PlayerRepository

class PlayerImpl : Player {
    private val player = MediaPlayer()
    override var playerState: Player.PlayerState = Player.PlayerState.DEFAULT
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var repository: PlayerRepository.PlayerCallback

    private val playbackProgress = object : Runnable {
        override fun run() {
            repository.onPlaybackProgressChanged(player.currentPosition)
            handler.postDelayed(this, PLAYBACK_PROGRESS_DELAY_MILLIS)
        }
    }

    override fun prepare(source: String, playerCallback: PlayerRepository.PlayerCallback) {
        repository = playerCallback
        player.setDataSource(source)
        player.prepareAsync()

        player.setOnPreparedListener {
            playerCallback.onPlayerPrepared()
            playerState = Player.PlayerState.PREPARED
        }

        player.setOnCompletionListener {
            handler.removeCallbacks(playbackProgress)
            playerState = Player.PlayerState.PREPARED
            playerCallback.onPlaybackCompleted()
        }
    }

    override fun startPlayer() {
        player.start()
        playbackProgress.run()
        playerState = Player.PlayerState.STARTED
    }

    override fun pausePlayer() {
        player.pause()
        handler.removeCallbacks(playbackProgress)
        playerState = Player.PlayerState.PAUSED
    }

    override fun releasePlayer() {
        player.release()
    }

    companion object {
        private const val PLAYBACK_PROGRESS_DELAY_MILLIS = 300L
    }
}