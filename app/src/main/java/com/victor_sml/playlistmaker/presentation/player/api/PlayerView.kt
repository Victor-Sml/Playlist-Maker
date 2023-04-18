package com.victor_sml.playlistmaker.presentation.player.api

interface PlayerView {
    fun updateControllerAvailability(isEnabled: Boolean)
    fun updateControllerImage(drawableId: Int)
    fun updatePlaybackProgress(progress: String)
}