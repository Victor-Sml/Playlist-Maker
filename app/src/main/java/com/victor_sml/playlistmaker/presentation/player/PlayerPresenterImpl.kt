package com.victor_sml.playlistmaker.presentation.player

import com.victor_sml.playlistmaker.Utils.millisToMMSS
import com.victor_sml.playlistmaker.domain.api.PlayerInteractor
import com.victor_sml.playlistmaker.domain.PlayerState
import com.victor_sml.playlistmaker.domain.PlayerState.DEFAULT
import com.victor_sml.playlistmaker.domain.PlayerState.PREPARED
import com.victor_sml.playlistmaker.domain.PlayerState.STARTED
import com.victor_sml.playlistmaker.domain.PlayerState.PAUSED
import com.victor_sml.playlistmaker.domain.PlayerState.PLAYBACK_COMPLETION
import com.victor_sml.playlistmaker.domain.api.PlayerInteractor.PlayerStateConsumer
import com.victor_sml.playlistmaker.presentation.IterativeLambda
import com.victor_sml.playlistmaker.presentation.IterativeLambdaIml
import com.victor_sml.playlistmaker.presentation.player.api.PlayerPresenter
import com.victor_sml.playlistmaker.presentation.player.api.PlayerView
import java.io.IOException

class PlayerPresenterImpl(
    private var view: PlayerView?,
    private val interactor: PlayerInteractor,
    trackSource: String?
) : PlayerPresenter {
    private var playerState: PlayerState = DEFAULT
    private val playerController = view?.let { PlaybackControllerImpl(it) }

    private val playerStateConsumer: PlayerStateConsumer =
        object : PlayerStateConsumer {
            override fun consume(state: PlayerState) {
                if (state == PLAYBACK_COMPLETION) iterativeLambda.stop()
                playerController?.setControllerState(state) ?: Unit
                playerState = state
            }
        }

    init {
        try {
            trackSource?.let { preparePlayer(it, playerStateConsumer) }
        } catch (e: IOException) {
        }
    }

    private val setProgress =
        {
            val progress = interactor.getPlaybackProgress()
            view?.updatePlaybackProgress(millisToMMSS(progress)) ?: Unit
        }

    private val iterativeLambda: IterativeLambda = IterativeLambdaIml(
        PLAYBACK_PROGRESS_DELAY_MILLIS, setProgress
    )

    private fun preparePlayer(
        source: String,
        playerStateConsumer: PlayerStateConsumer
    ) {
        interactor.preparePlayer(source, playerStateConsumer)
    }

    override fun playbackControl() {
        when (playerState) {
            PREPARED, PAUSED, PLAYBACK_COMPLETION -> startPlayer()
            STARTED -> pausePlayer()
            else -> {}
        }
    }

    private fun startPlayer() {
        interactor.startPlayer()
        iterativeLambda.start()
    }

    private fun pausePlayer() {
        iterativeLambda.stop()
        interactor.pausePlayer()
    }

    override fun onViewPaused() {
        if (playerState == STARTED) {
            pausePlayer()
        }
    }

    override fun onViewDestroyed() {
        view = null
        interactor.releasePlayer()
    }

    companion object {
        private const val PLAYBACK_PROGRESS_DELAY_MILLIS = 300L
    }
}