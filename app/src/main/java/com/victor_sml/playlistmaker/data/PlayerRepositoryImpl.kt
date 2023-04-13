package com.victor_sml.playlistmaker.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.victor_sml.playlistmaker.data.PlayerRepository.ControllerAction.PLAY
import com.victor_sml.playlistmaker.data.PlayerRepository.ControllerAction.PAUSE
import com.victor_sml.playlistmaker.data.PlayerRepository.ControllerAction

class PlayerRepositoryImpl(
    val player: Player,
    source: String?,
) : PlayerRepository, PlayerRepository.PlayerCallback {

    init {
        source?.let { player.prepare(source, this) }
    }

    private val _progress = MutableLiveData(0)
    override val progress: LiveData<Int> = _progress

    private val _controllerAvailability = MutableLiveData(false)
    override val controllerAvailability: LiveData<Boolean> = _controllerAvailability

    private val _controllerAction = MutableLiveData(PLAY)
    override val controllerAction: LiveData<ControllerAction> = _controllerAction

    override fun playbackControl() {
        when (player.playerState) {
            Player.PlayerState.PREPARED,
            Player.PlayerState.PAUSED -> startPlayer()
            Player.PlayerState.STARTED -> pausePlayer()
            else -> {}
        }
    }

    private fun startPlayer() {
        player.startPlayer()
        _controllerAction.postValue(PAUSE)
    }

    override fun pausePlayer() {
        player.pausePlayer()
        _controllerAction.postValue(PLAY)
    }

    override fun releasePlayer() {
        player.releasePlayer()
    }

    override fun onPlayerPrepared() {
        _controllerAvailability.postValue(true)
    }

    override fun onPlaybackCompleted() {
        _controllerAction.postValue(PLAY)
        _progress.postValue(DEFAULT_PLAYBACK_PROGRESS_MILLIS)
    }

    override fun onPlaybackProgressChanged(progress: Int) {
        _progress.postValue(progress)
    }

    companion object {
        private const val DEFAULT_PLAYBACK_PROGRESS_MILLIS = 0
    }
}