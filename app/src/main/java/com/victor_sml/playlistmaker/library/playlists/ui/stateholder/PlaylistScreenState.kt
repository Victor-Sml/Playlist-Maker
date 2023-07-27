package com.victor_sml.playlistmaker.library.playlists.ui.stateholder

import com.victor_sml.playlistmaker.common.models.Playlist

sealed class PlaylistScreenState(val playlists: ArrayList<Playlist>? = null){
    object Empty: PlaylistScreenState()
    class Content(playlists: ArrayList<Playlist>): PlaylistScreenState(playlists)
}
