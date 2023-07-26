package com.victor_sml.playlistmaker.common.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: Int,
    val artistName: String?,
    val trackName: String?,
    val artworkUrl100: String?,
    val trackTimeMillis: Long?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
    var isFavorite: Boolean
): Parcelable {

    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")}