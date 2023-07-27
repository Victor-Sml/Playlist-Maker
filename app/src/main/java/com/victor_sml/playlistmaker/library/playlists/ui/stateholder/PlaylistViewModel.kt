package com.victor_sml.playlistmaker.library.playlists.ui.stateholder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victor_sml.playlistmaker.common.models.Playlist
import com.victor_sml.playlistmaker.library.domain.api.PlaylistInteractor
import com.victor_sml.playlistmaker.library.playlists.ui.stateholder.PlaylistScreenState.Content
import com.victor_sml.playlistmaker.library.playlists.ui.stateholder.PlaylistScreenState.Empty
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    private val playlistState = MutableLiveData<PlaylistScreenState>()

    fun getPlaylistState(): LiveData<PlaylistScreenState> = playlistState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.loadPlaylist().collect { playlists ->
                if (playlists.isNullOrEmpty()) playlistState.postValue(Empty)
                else
                    playlistState.postValue(Content(playlists as ArrayList<Playlist>))
            }
        }
    }
}