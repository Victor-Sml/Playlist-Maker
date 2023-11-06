package com.victor_sml.playlistmaker.sharing.domain.api

import android.net.Uri
import com.victor_sml.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareText(text: String, previewTitle: String? = null, previewImageUri: Uri? = null)

    fun openLink(link: String)

    fun openEmail(emailData: EmailData)
}