package com.victor_sml.playlistmaker.presentation.player.api

interface PlayerPresenter {
    fun playbackControl()
    fun onViewPaused()
    fun onViewDestroyed()
}