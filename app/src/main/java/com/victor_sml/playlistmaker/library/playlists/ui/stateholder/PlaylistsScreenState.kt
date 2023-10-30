package com.victor_sml.playlistmaker.library.playlists.ui.stateholder

import com.victor_sml.playlistmaker.common.ui.recycler.api.RecyclerItem

sealed class PlaylistsScreenState(val playlists: ArrayList<RecyclerItem>? = null){
    object Empty: PlaylistsScreenState()
    class Content(playlists: ArrayList<RecyclerItem>): PlaylistsScreenState(playlists)
}
