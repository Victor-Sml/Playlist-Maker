package com.victor_sml.playlistmaker.library.playlistEditor.ui.stateholders

sealed class PlaylistCreationState(val message: String) {
    class Success(message: String): PlaylistCreationState(message)
    class Fail(message: String): PlaylistCreationState(message)
}
