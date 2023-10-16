package com.victor_sml.playlistmaker.player.ui.stateholders


sealed class TrackInsertionState(val message: String){
    class Success(message: String): TrackInsertionState(message)
    class Fail(message: String): TrackInsertionState(message)
}
