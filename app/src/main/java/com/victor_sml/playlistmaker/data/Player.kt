package com.victor_sml.playlistmaker.data

interface Player {
    var playerState: PlayerState

    fun prepare(source: String, playerCallback: PlayerRepository.PlayerCallback)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()

    enum class PlayerState {
        DEFAULT,
        PREPARED,
        STARTED,
        PAUSED
    }
}