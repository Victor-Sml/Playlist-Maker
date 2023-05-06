package com.victor_sml.playlistmaker.sharing.domain.api

import com.victor_sml.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(link: String)
    fun openEmail(emailData: EmailData)
}