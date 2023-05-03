package com.victor_sml.playlistmaker.sharing.domain.api

import com.victor_sml.playlistmaker.sharing.domain.model.EmailData

interface SharingRepository {
    fun getShareAppLink(): String
    fun getSupportEmailData(): EmailData
    fun getTermsLink(): String
}