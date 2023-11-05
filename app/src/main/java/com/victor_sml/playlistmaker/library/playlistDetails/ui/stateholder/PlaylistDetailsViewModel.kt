package com.victor_sml.playlistmaker.library.playlistDetails.ui.stateholder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.victor_sml.playlistmaker.common.domain.models.PlaylistWithTracks
import com.victor_sml.playlistmaker.common.domain.models.Track
import com.victor_sml.playlistmaker.common.domain.api.playlists.PlaylistInteractor
import com.victor_sml.playlistmaker.common.ui.recycler.api.RecyclerItem
import com.victor_sml.playlistmaker.library.playlistDetails.ui.model.PlaylistUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var playlist = MutableLiveData<PlaylistUi>()

    fun getState(): LiveData<PlaylistUi> = playlist

    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.loadPlaylist(playlistId).collect { playlistWithTracks ->
                playlist.postValue(playlistWithTracks.toPlaylistUi())
            }
        }
    }

    fun deleteFromPlaylist(playlistId: Int, trackId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.deleteFromPlaylist(playlistId, trackId)
        }
    }

    companion object {
        fun PlaylistWithTracks.toPlaylistUi() =
            PlaylistUi(
                this.id,
                this.title,
                this.coverPath,
                this.description,
                this.tracks.getNumberOfTracks(),
                this.tracks.totalDuration(),
                this.tracks?.map { RecyclerItem.TrackItem(it) }
            )

        private fun List<Track>?.getNumberOfTracks(): Int {
            return if (this.isNullOrEmpty()) 0
            else this.size
        }

        private fun List<Track>?.totalDuration(): Int {
            if (this.isNullOrEmpty()) return 0
            var duration = 0L

            this.forEach { it.trackTimeMillis?.let { duration += it } }
            duration /= 60000

            return duration.toInt()
        }
    }
}