package com.victor_sml.playlistmaker.sharing.data

import com.victor_sml.playlistmaker.common.data.api.StringSource
import com.victor_sml.playlistmaker.sharing.domain.api.SharingRepository
import com.victor_sml.playlistmaker.sharing.domain.model.EmailData

class SharingRepositoryImpl(private val stringSource: StringSource) : SharingRepository {
    override fun getShareAppLink(): String = stringSource.getString(SHARE_APP_URI)

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            stringSource.getString(SUPPORT_EMAIL_URI),
            stringSource.getString(SUPPORT_EMAIL_SUBJECT),
            stringSource.getString(SUPPORT_EMAIL_BODY)
        )
    }

    override fun getTermsLink(): String = stringSource.getString(USER_AGREEMENT_URI)

    companion object {
        const val SHARE_APP_URI = "share_app_uri"
        const val SUPPORT_EMAIL_URI = "support_email_uri"
        const val SUPPORT_EMAIL_SUBJECT = "support_email_subject"
        const val SUPPORT_EMAIL_BODY = "support_email_body"
        const val USER_AGREEMENT_URI = "user_agreement_uri"
    }
}