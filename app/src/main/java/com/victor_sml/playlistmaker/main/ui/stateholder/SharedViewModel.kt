package com.victor_sml.playlistmaker.main.ui.stateholder

import androidx.lifecycle.ViewModel
import com.victor_sml.playlistmaker.common.domain.models.Track
import com.victor_sml.playlistmaker.common.utils.TrackHolder
import org.koin.java.KoinJavaComponent.inject

class SharedViewModel : ViewModel() {
    private val trackHolder: TrackHolder by inject(TrackHolder::class.java)

    fun passTrack(key: String, track: Track) {
        trackHolder.passTrack(key, track)
    }

    fun getTrack(key: String) = trackHolder.getTrack(key)
}