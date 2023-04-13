package com.victor_sml.playlistmaker.presentation.player

interface PlayerPresenter {
    fun playbackControl()
    fun onViewPaused()
    fun onViewDestroyed()
}