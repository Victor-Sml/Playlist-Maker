package com.victor_sml.playlistmaker.sharing.data

import com.victor_sml.playlistmaker.sharing.data.SharingRepositoryImpl.StringId.SHARE_APP_URI
import com.victor_sml.playlistmaker.sharing.data.SharingRepositoryImpl.StringId.SUPPORT_EMAIL_URI
import com.victor_sml.playlistmaker.sharing.data.SharingRepositoryImpl.StringId.SUPPORT_EMAIL_SUBJECT
import com.victor_sml.playlistmaker.sharing.data.SharingRepositoryImpl.StringId.SUPPORT_EMAIL_BODY
import com.victor_sml.playlistmaker.sharing.data.SharingRepositoryImpl.StringId.USER_AGREEMENT_URI
import com.victor_sml.playlistmaker.sharing.data.api.StringSource
import com.victor_sml.playlistmaker.sharing.domain.api.SharingRepository
import com.victor_sml.playlistmaker.sharing.domain.model.EmailData

class SharingRepositoryImpl(private val sharingSource: StringSource) : SharingRepository {
    override fun getShareAppLink(): String = sharingSource.getString(SHARE_APP_URI)

    override fun getSupportEmailData(): EmailData {
        val emailDetails = sharingSource.getStrings(
            arrayOf(
                SUPPORT_EMAIL_URI,
                SUPPORT_EMAIL_SUBJECT,
                SUPPORT_EMAIL_BODY
            )
        )
        return EmailData(
            emailUri = emailDetails[0],
            subject = emailDetails[1],
            body = emailDetails[2]
        )
    }

    override fun getTermsLink(): String = sharingSource.getString(USER_AGREEMENT_URI)

    enum class StringId(val value: String) {
        SHARE_APP_URI("share_app_uri"),
        SUPPORT_EMAIL_URI("support_email_uri"),
        SUPPORT_EMAIL_SUBJECT("support_email_subject"),
        SUPPORT_EMAIL_BODY("support_email_body"),
        USER_AGREEMENT_URI("user_agreement_uri")
    }
}