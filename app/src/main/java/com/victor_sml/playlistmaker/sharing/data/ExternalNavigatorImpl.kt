package com.victor_sml.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.victor_sml.playlistmaker.sharing.domain.api.ExternalNavigator
import com.victor_sml.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun shareLink(link: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }
        context.startActivity(shareIntent)
    }

    override fun openLink(link: String) {
        val agreementIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(link)
        )
        context.startActivity(agreementIntent)
    }

    override fun openEmail(emailData: EmailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.emailUri))
            putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
            putExtra(Intent.EXTRA_TEXT, emailData.body)
        }
        context.startActivity(supportIntent)
    }
}