package com.victor_sml.playlistmaker.library.playlistEditor.ui.stateholders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victor_sml.playlistmaker.common.domain.GetStringUseCase
import com.victor_sml.playlistmaker.common.domain.api.playlists.PlaylistInteractor
import com.victor_sml.playlistmaker.common.domain.models.Playlist
import com.victor_sml.playlistmaker.common.utils.DBQueryState
import com.victor_sml.playlistmaker.common.utils.DBQueryState.Error
import com.victor_sml.playlistmaker.common.utils.DBQueryState.Ok
import com.victor_sml.playlistmaker.library.playlistEditor.ui.stateholders.PlaylistCreationState.Fail
import com.victor_sml.playlistmaker.library.playlistEditor.ui.stateholders.PlaylistCreationState.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistEditorViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val getStringUseCase: GetStringUseCase,
) : ViewModel() {
    private val creationState = MutableLiveData<PlaylistCreationState>()

    fun getCreationState(): LiveData<PlaylistCreationState> = creationState

    fun addPlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            processQueryResult(playlistInteractor.addPlaylist(playlist), playlist)
        }
    }

    private fun processQueryResult(queryState: DBQueryState, playlist: Playlist) {
        when (queryState) {
            is Ok ->
                creationState.postValue(Success(getSuccessMessage(playlist.title)))

            is Error ->
                creationState.postValue(Fail(getFailMessage()))
        }
    }

    private fun getSuccessMessage(playlistTitle: String) =
        String.format(getStringUseCase.execute(PLAYLIST_CREATION_STR_ID), playlistTitle)

    private fun getFailMessage() =
        getStringUseCase.execute(PLAYLIST_CREATION_FAIL_STR_ID)

    companion object {
        const val PLAYLIST_CREATION_STR_ID = "playlist_creation"
        const val PLAYLIST_CREATION_FAIL_STR_ID = "playlist_creation_fail"
    }
}