package com.victor_sml.playlistmaker.common.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Int,
    val title: String,
    val coverPath: String?,
    val description: String?,
    val numberOfTracks: Int
): Parcelable