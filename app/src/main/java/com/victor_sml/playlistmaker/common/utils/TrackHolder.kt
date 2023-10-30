package com.victor_sml.playlistmaker.common.utils

import android.os.Bundle
import com.victor_sml.playlistmaker.common.domain.models.Track

class TrackHolder() {
    private var track = Bundle(1)

    fun passTrack(key: String, track: Track) {
        this.track.putParcelable(key, track)
    }

    fun getTrack(key: String): Track? {
        return track.getParcelable(key)
    }
}