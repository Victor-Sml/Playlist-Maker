package com.victor_sml.playlistmaker.presentation.player

interface PlayerView {
    fun updateControllerAvailability(isEnabled: Boolean)
    fun updateControllerImage(drawableId: Int)
    fun updatePlaybackProgress(progress: String)
}