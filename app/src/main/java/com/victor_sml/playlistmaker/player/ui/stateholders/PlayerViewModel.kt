package com.victor_sml.playlistmaker.player.ui.stateholders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState.DEFAULT
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState.PREPARED
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState.STARTED
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState.PAUSED
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState.PLAYBACK_COMPLETION
import com.victor_sml.playlistmaker.player.domain.api.PlayerInteractor
import com.victor_sml.playlistmaker.common.utils.Utils.toTimeMMSS
import java.io.IOException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    trackSource: String?,
    private val interactor: PlayerInteractor,
) :
    ViewModel(), PlayerInteractor.StateObserver {

    var timerJob: Job? = null

    init {
        try {
            trackSource?.let { interactor.preparePlayer(it, this) }
        } catch (e: IOException) {
        }
    }

    private var playerState = MutableLiveData(DEFAULT)
    private var playbackProgress = MutableLiveData<String>()

    fun getPlayerState(): LiveData<PlayerState> = playerState
    fun getPlaybackProgress(): LiveData<String> = playbackProgress

    fun playbackControl() {
        when (playerState.value) {
            PREPARED, PAUSED, PLAYBACK_COMPLETION -> startPlayer()
            STARTED -> pausePlayer()
            else -> {}
        }
    }

    private fun startPlayer() {
        interactor.startPlayer()
    }

    private fun pausePlayer() {
        interactor.pausePlayer()
    }

    override fun onPrepared() {
        playerState.postValue(PREPARED)
        playbackProgress.postValue(DEFAULT_PLAYBACK_PROGRESS)
    }

    override fun onStarted() {
        playerState.postValue(STARTED)
        startTimer()
    }

    override fun onPause() {
        playerState.postValue(PAUSED)
        stopTimer()
    }

    override fun onCompletion() {
        stopTimer()
        playerState.postValue(PLAYBACK_COMPLETION)
        playbackProgress.postValue(DEFAULT_PLAYBACK_PROGRESS)
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(PLAYBACK_PROGRESS_DELAY_MILLIS)
                interactor.getPlaybackProgress().toTimeMMSS().let { progress ->
                    playbackProgress.postValue(progress)
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        interactor.releasePlayer()
    }

    companion object {
        private const val DEFAULT_PLAYBACK_PROGRESS = "00:00"
        private const val PLAYBACK_PROGRESS_DELAY_MILLIS = 300L
    }
}