package com.victor_sml.playlistmaker.common.data.db.convertors

import com.victor_sml.playlistmaker.common.data.db.convertors.TrackConvertor.toTrack
import com.victor_sml.playlistmaker.common.data.db.entity.PlaylistEntity
import com.victor_sml.playlistmaker.common.data.db.entity.PlaylistWithTracks
import com.victor_sml.playlistmaker.common.models.Playlist

object PlaylistConvertor {

    fun Playlist.toPlaylistEntity() = PlaylistEntity(
        id,
        title,
        coverPath,
        description
    )

    fun PlaylistWithTracks.toPlaylist(): Playlist {
        val tracks = tracks.map { it.toTrack() }
        return Playlist(
            playlist.id,
            playlist.title,
            playlist.coverPath,
            playlist.description,
            tracks
        )
    }
}