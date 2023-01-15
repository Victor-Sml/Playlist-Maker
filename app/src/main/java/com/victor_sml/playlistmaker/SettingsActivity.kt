package com.victor_sml.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.settingsToolbar)
        toolbar.setNavigationOnClickListener {
            this.finish()
        }

        val shareListItem = findViewById<FrameLayout>(R.id.share_list_item)
        shareListItem.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_uri))
            }
            startActivity(shareIntent)
        }

        val supportListItem = findViewById<FrameLayout>(R.id.support_list_item)
        supportListItem.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email_uri)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_body))
            }
            startActivity(supportIntent)
        }

        val agreementListItem = findViewById<FrameLayout>(R.id.agreement_list_item)
        agreementListItem.setOnClickListener {
            val agreementIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.user_agreement_uri))
            )
            startActivity(agreementIntent)
        }
    }
}