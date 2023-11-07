package com.victor_sml.playlistmaker.library.playlistDetails.domain

import android.net.Uri
import com.victor_sml.playlistmaker.sharing.domain.api.ExternalNavigator

class SharePlaylistUseCase(private val externalNavigator: ExternalNavigator) {
    fun execute(playlistMessage: String, previewTitle: String, contentUri: Uri?) {
        externalNavigator.shareText(playlistMessage, previewTitle, contentUri)
    }
}