package com.victor_sml.playlistmaker.library.playlistDetails.ui.model

import com.victor_sml.playlistmaker.common.domain.models.Playlist
import com.victor_sml.playlistmaker.common.ui.recycler.api.RecyclerItem.TrackItem

class PlaylistUi(
    val id: Int,
    val title: String,
    val coverPath: String?,
    val description: String?,
    val numberOfTracks: Int,
    val totalDuration: Int,
    val tracks: List<TrackItem>? = null,
) {
    fun toPlaylist() =
        Playlist(
            id,
            title,
            coverPath,
            description,
            numberOfTracks
        )
}
