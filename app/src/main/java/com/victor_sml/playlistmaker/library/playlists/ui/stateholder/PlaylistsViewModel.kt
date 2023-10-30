package com.victor_sml.playlistmaker.library.playlists.ui.stateholder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victor_sml.playlistmaker.common.ui.recycler.RecyclerItemsBuilder
import com.victor_sml.playlistmaker.library.playlists.ui.stateholder.PlaylistsScreenState.Content
import com.victor_sml.playlistmaker.library.playlists.ui.stateholder.PlaylistsScreenState.Empty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class PlaylistsViewModel(private val playlistInteractor: com.victor_sml.playlistmaker.common.domain.api.playlists.PlaylistInteractor) : ViewModel() {
    private val recyclerBuilder: RecyclerItemsBuilder by inject(
        RecyclerItemsBuilder::class.java
    )

    private val playlistState = MutableLiveData<PlaylistsScreenState>()

    fun getPlaylistState(): LiveData<PlaylistsScreenState> = playlistState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.loadPlaylists().collect { playlists ->
                if (playlists.isNullOrEmpty()) playlistState.postValue(Empty)
                else
                    playlistState.postValue(
                        Content(recyclerBuilder.addPlaylists(playlists).getItems())
                    )
            }
        }
    }
}