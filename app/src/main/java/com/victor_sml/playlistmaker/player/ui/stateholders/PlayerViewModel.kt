package com.victor_sml.playlistmaker.player.ui.stateholders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victor_sml.playlistmaker.common.domain.GetStringUseCase
import com.victor_sml.playlistmaker.common.domain.api.TracksInteractor
import com.victor_sml.playlistmaker.common.models.Playlist
import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.common.utils.DBQueryState
import com.victor_sml.playlistmaker.common.utils.SingleLiveEvent
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState.DEFAULT
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState.PREPARED
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState.STARTED
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState.PAUSED
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerState.PLAYBACK_COMPLETION
import com.victor_sml.playlistmaker.player.domain.api.PlayerInteractor
import com.victor_sml.playlistmaker.common.utils.Utils.toTimeMMSS
import com.victor_sml.playlistmaker.library.domain.api.PlaylistInteractor
import com.victor_sml.playlistmaker.player.ui.stateholders.FavoriteState.Dislike
import com.victor_sml.playlistmaker.player.ui.stateholders.FavoriteState.Like
import com.victor_sml.playlistmaker.player.ui.stateholders.TrackInsertionState.Fail
import com.victor_sml.playlistmaker.player.ui.stateholders.TrackInsertionState.Success
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: PlayerInteractor,
    private val trackInteractor: TracksInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val getStringUseCase: GetStringUseCase,
) :
    ViewModel(), PlayerInteractor.StateObserver {

    private var timerJob: Job? = null

    private var playerState = MutableLiveData(DEFAULT)
    private var playbackProgress = MutableLiveData<String>()
    private var favoriteState = MutableLiveData<FavoriteState>()
    private var playlists = MutableLiveData<ArrayList<Playlist>>()
    private var trackInsertionState = SingleLiveEvent<TrackInsertionState>()

    fun getPlayerState(): LiveData<PlayerState> = playerState
    fun getPlaybackProgress(): LiveData<String> = playbackProgress
    fun getFavoriteState(): LiveData<FavoriteState> = favoriteState
    fun getPlaylists(): LiveData<ArrayList<Playlist>> = playlists
    fun getTrackInsertionState(): LiveData<TrackInsertionState> = trackInsertionState

    init {
        preparePlayer()
        postFavoriteState()
        observePlaylists()
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

    fun onAddToPlaylistClick(playlist: Playlist, track: Track) {
        viewModelScope.launch {
            trackInteractor.addTrack(track)
            processQueryResult(
                playlistInteractor.insertToPlaylist(playlist.id, track.trackId),
                playlist.title
            )
        }
    }

    private fun preparePlayer() {
        try {
            track.previewUrl?.let { playerInteractor.preparePlayer(it, this) }
        } catch (_: IOException) {
        }
    }

    private fun postFavoriteState() {
        if (track.isFavorite) favoriteState.postValue(Like)
        else favoriteState.postValue(Dislike)
    }

    private fun observePlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.loadPlaylist().collect { playlists.postValue(it as ArrayList) }
        }
    }

    private suspend fun changeFavoriteState() {
        if (track.isFavorite) trackInteractor.addTrack(track)
        else trackInteractor.deleteTrackFromFavorites(track.trackId)
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

    private fun processQueryResult(queryState: DBQueryState, playlistTitle: String) {
        when (queryState) {
            is DBQueryState.Ok ->
                trackInsertionState.postValue(Success(getSuccessMessage(playlistTitle)))

            is DBQueryState.ErrorUnique ->
                trackInsertionState.postValue(Fail(getFailMessage(playlistTitle)))

            else -> {
            }
        }
    }

    private fun getSuccessMessage(playlistTitle: String) =
        String.format(getStringUseCase.execute(ADDED_TO_PLAYLIST_STR_ID), playlistTitle)

    private fun getFailMessage(playlistTitle: String) =
        String.format(getStringUseCase.execute(EXIST_WITHIN_PLAYLIST_STR_ID), playlistTitle)

    companion object {
        private const val DEFAULT_PLAYBACK_PROGRESS = "00:00"
        private const val PLAYBACK_PROGRESS_DELAY_MILLIS = 300L

        private const val ADDED_TO_PLAYLIST_STR_ID = "has_been_added_to_playlist"
        private const val EXIST_WITHIN_PLAYLIST_STR_ID = "already_in_playlist"
    }
}