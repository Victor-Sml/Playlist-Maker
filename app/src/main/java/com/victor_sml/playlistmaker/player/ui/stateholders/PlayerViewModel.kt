package com.victor_sml.playlistmaker.player.ui.stateholders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victor_sml.playlistmaker.common.domain.api.LibraryInteractor
import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState.DEFAULT
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState.PREPARED
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState.STARTED
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState.PAUSED
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState.PLAYBACK_COMPLETION
import com.victor_sml.playlistmaker.player.domain.api.PlayerInteractor
import com.victor_sml.playlistmaker.common.utils.Utils.toTimeMMSS
import com.victor_sml.playlistmaker.player.ui.stateholders.FavoriteState.Dislike
import com.victor_sml.playlistmaker.player.ui.stateholders.FavoriteState.Like
import java.io.IOException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: PlayerInteractor,
    private val libraryInteractor: LibraryInteractor
) :
    ViewModel(), PlayerInteractor.StateObserver {

    private var timerJob: Job? = null

    private var playerState = MutableLiveData(DEFAULT)
    private var playbackProgress = MutableLiveData<String>()
    private var favoriteState = MutableLiveData<FavoriteState>()

    fun getPlayerState(): LiveData<PlayerState> = playerState
    fun getPlaybackProgress(): LiveData<String> = playbackProgress
    fun getFavoriteState(): LiveData<FavoriteState> = favoriteState

    init {
        try {
            track.previewUrl?.let { playerInteractor.preparePlayer(it, this) }
        } catch (e: IOException) {
        }

        postFavoriteState()
    }

    fun onPlaybackControlClick() {
        when (playerState.value) {
            PREPARED, PAUSED, PLAYBACK_COMPLETION -> startPlayer()
            STARTED -> pausePlayer()
            else -> {}
        }
    }

    fun onLikeClick() {
        viewModelScope.launch {
            track.isFavorite = !track.isFavorite
            postFavoriteState()
            changeFavoriteState()
        }
    }

    private fun postFavoriteState() {
        if (track.isFavorite) favoriteState.postValue(Like)
        else favoriteState.postValue(Dislike)
    }

    private suspend fun changeFavoriteState() {
        if (track.isFavorite) libraryInteractor.addTrackToFavorites(track)
        else libraryInteractor.deleteTrackFromFavorites(track.trackId)
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
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
                playerInteractor.getPlaybackProgress().toTimeMMSS().let { progress ->
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
        playerInteractor.releasePlayer()
    }

    companion object {
        private const val DEFAULT_PLAYBACK_PROGRESS = "00:00"
        private const val PLAYBACK_PROGRESS_DELAY_MILLIS = 300L
    }
}