package com.victor_sml.playlistmaker.common.models

import android.os.Parcelable
import com.victor_sml.playlistmaker.search.ui.view.recycler.api.RecyclerItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackUi(
    val artistName: String?,
    val trackName: String?,
    val artworkUrl100: String?,
    val trackTime: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) : Parcelable, RecyclerItem {

    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")}



