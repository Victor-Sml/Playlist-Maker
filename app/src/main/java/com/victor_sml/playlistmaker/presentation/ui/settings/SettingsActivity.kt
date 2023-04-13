package com.victor_sml.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.victor_sml.playlistmaker.App
import com.victor_sml.playlistmaker.DARK_THEME_ENABLED
import com.victor_sml.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    private lateinit var app: App
    private lateinit var toolbar: Toolbar
    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var shareListItem: FrameLayout
    private lateinit var supportListItem: FrameLayout
    private lateinit var agreementListItem: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        app = applicationContext as App

        initView()
        setListeners()
    }

    private fun initView() {
        toolbar = findViewById(R.id.settingsToolbar)
        themeSwitcher = findViewById(R.id.theme_switcher)
        shareListItem = findViewById(R.id.share_list_item)
        supportListItem = findViewById(R.id.support_list_item)
        agreementListItem = findViewById(R.id.agreement_list_item)

        themeSwitcher.isChecked = app.isDarkThemeEnabled()
    }

    private fun setListeners() {
        toolbar.setNavigationOnClickListener {
            this.finish()
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            app.switchTheme(checked)
            app.getSharedPreferences()
                .edit()
                .putBoolean(DARK_THEME_ENABLED, themeSwitcher.isChecked)
                .apply()
        }

        shareListItem.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_uri))
            }
            startActivity(shareIntent)
        }

        supportListItem.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email_uri)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_body))
            }
            startActivity(supportIntent)
        }

        agreementListItem.setOnClickListener {
            val agreementIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.user_agreement_uri))
            )
            startActivity(agreementIntent)
        }
    }

}