package com.victor_sml.playlistmaker.library.playlistEditor.ui.stateholders

import android.util.Log
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

    fun savePlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            val x = playlistInteractor.savePlaylist(playlist)
            Log.d("MyLogs", x.toString() + " state")
            processQueryResult(x, playlist)
        }
    }

    private fun processQueryResult(queryState: DBQueryState, playlist: Playlist) {
        when (queryState) {
            is Ok ->
                creationState.postValue(Success(getSuccessMessage(playlist.title, queryState)))

            is Error ->
                creationState.postValue(Fail(getFailMessage()))
        }
    }

    private fun getSuccessMessage(playlistTitle: String, queryState: Ok): String {
        return when (queryState) {
            is DBQueryState.Added ->
                String.format(getStringUseCase.execute(PLAYLIST_CREATED_STR_ID), playlistTitle)

            is DBQueryState.Updated ->
                String.format(getStringUseCase.execute(PLAYLIST_EDITED_STR_ID), playlistTitle)

            else -> ""
        }
    }

    private fun getFailMessage() =
        getStringUseCase.execute(PLAYLIST_CREATION_FAIL_STR_ID)

    companion object {
        const val PLAYLIST_CREATED_STR_ID = "playlist_created_message"
        const val PLAYLIST_EDITED_STR_ID = "playlist_edited_message"
        const val PLAYLIST_CREATION_FAIL_STR_ID = "playlist_creation_fail"
    }
}