package com.victor_sml.playlistmaker.presentation.player

import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.domain.PlayerState
import com.victor_sml.playlistmaker.domain.PlayerState.PREPARED
import com.victor_sml.playlistmaker.domain.PlayerState.STARTED
import com.victor_sml.playlistmaker.domain.PlayerState.PAUSED
import com.victor_sml.playlistmaker.domain.PlayerState.PLAYBACK_COMPLETION
import com.victor_sml.playlistmaker.presentation.player.api.PlaybackController
import com.victor_sml.playlistmaker.presentation.player.api.PlayerView

class PlaybackControllerImpl(private val view: PlayerView) : PlaybackController {

    override fun setControllerState(playerState: PlayerState) {
        when (playerState) {
            STARTED -> setPauseAction()
            PAUSED -> setPlayAction()
            PLAYBACK_COMPLETION -> resetController()
            PREPARED -> {
                setControllerAvailability(isEnable = true)
                resetController()
            }
            else -> {}
        }
    }

    private fun setPlayAction() {
        view.updateControllerImage(R.drawable.ic_play)
    }

    private fun setPauseAction() {
        view.updateControllerImage(R.drawable.ic_pause)
    }

    private fun setControllerAvailability(isEnable: Boolean) =
        view.updateControllerAvailability(isEnable)

    private fun resetController() {
        view.updateControllerImage(R.drawable.ic_play)
        view.updatePlaybackProgress(DEFAULT_PLAYBACK_PROGRESS)
    }

    companion object {
        private const val DEFAULT_PLAYBACK_PROGRESS = "00:00"
    }
}