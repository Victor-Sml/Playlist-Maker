package com.victor_sml.playlistmaker.presentation.player.api

import com.victor_sml.playlistmaker.domain.PlayerState

interface PlaybackController {
    fun setControllerState(playerState: PlayerState)
}