package com.victor_sml.playlistmaker.sharing.data

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.ACTION_VIEW
import android.content.Intent.ACTION_SENDTO
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import com.victor_sml.playlistmaker.sharing.domain.api.ExternalNavigator
import com.victor_sml.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun shareLink(link: String) {
        try {
            val shareIntent = Intent(ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, link)
            }
            shareIntent.flags = FLAG_ACTIVITY_NEW_TASK
            context.startActivity(shareIntent)
        } catch (e: ActivityNotFoundException) {
        }
    }

    override fun openLink(link: String) {
        try {
            val agreementIntent = Intent(ACTION_VIEW, Uri.parse(link))
            agreementIntent.flags = FLAG_ACTIVITY_NEW_TASK
            context.startActivity(agreementIntent)
        } catch (e: ActivityNotFoundException) {
        }
    }

    override fun openEmail(emailData: EmailData) {
        try {
            val supportIntent = Intent(ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.emailUri))
                putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
                putExtra(Intent.EXTRA_TEXT, emailData.body)
            }
            supportIntent.flags = FLAG_ACTIVITY_NEW_TASK
            context.startActivity(supportIntent)
        } catch (e: ActivityNotFoundException) {
        }
    }
}