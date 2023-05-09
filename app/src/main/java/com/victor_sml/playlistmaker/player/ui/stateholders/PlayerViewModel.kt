package com.victor_sml.playlistmaker.player.ui.stateholders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.victor_sml.playlistmaker.common.utils.IterativeLambda
import com.victor_sml.playlistmaker.common.utils.IterativeLambdaIml
import com.victor_sml.playlistmaker.creator.Creator
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState.*
import com.victor_sml.playlistmaker.player.domain.api.PlayerInteractor
import com.victor_sml.playlistmaker.common.utils.Utils.toTimeMMSS
import java.io.IOException

class PlayerViewModel(trackSource: String, private val interactor: PlayerInteractor) :
    ViewModel(), PlayerInteractor.StateObserver {

    init {
        try {
            trackSource.let { interactor.preparePlayer(it, this) }
        } catch (e: IOException) {
        }
    }

    private val progressCounter: IterativeLambda = IterativeLambdaIml(
        PLAYBACK_PROGRESS_DELAY_MILLIS
    ) {
        interactor.getPlaybackProgress().toTimeMMSS().let { progress ->
            playbackProgress.postValue(progress)
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
        progressCounter.start()
    }

    private fun pausePlayer() {
        progressCounter.stop()
        interactor.pausePlayer()
    }

    override fun onPrepared() {
        playerState.postValue(PREPARED)
        playbackProgress.postValue(DEFAULT_PLAYBACK_PROGRESS)
    }

    override fun onStarted() {
        playerState.postValue(STARTED)
    }

    override fun onPause() {
        playerState.postValue(PAUSED)
    }

    override fun onCompletion() {
        progressCounter.stop()
        playerState.postValue(PLAYBACK_COMPLETION)
        playbackProgress.postValue(DEFAULT_PLAYBACK_PROGRESS)
    }

    override fun onCleared() {
        super.onCleared()
        progressCounter.stop()
        interactor.releasePlayer()
    }

    companion object {
        fun getViewModelFactory(trackSource: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(
                    trackSource,
                    Creator.providePlayerInteractor()
                )
            }
        }

        private const val DEFAULT_PLAYBACK_PROGRESS = "00:00"
        private const val PLAYBACK_PROGRESS_DELAY_MILLIS = 300L
    }
}