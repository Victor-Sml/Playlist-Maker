package com.victor_sml.playlistmaker.common.data.db.convertors

import com.victor_sml.playlistmaker.common.data.db.dto.PlaylistDto
import com.victor_sml.playlistmaker.common.data.db.entity.PlaylistEntity
import com.victor_sml.playlistmaker.common.domain.models.Playlist

object PlaylistConvertor {

    fun PlaylistDto.toPlaylist() =
        Playlist(
            playlist.id,
            playlist.title,
            playlist.coverPath,
            playlist.description,
            numberOfTracks
        )

    fun Playlist.toPlaylistEntity() = PlaylistEntity(
        id,
        title,
        coverPath,
        description
    )
}