package com.victor_sml.playlistmaker.settings.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.victor_sml.playlistmaker.App
import com.victor_sml.playlistmaker.databinding.ActivitySettingsBinding
import com.victor_sml.playlistmaker.settings.domain.model.ThemeSettings
import com.victor_sml.playlistmaker.settings.ui.stateholders.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel by viewModel<SettingsViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.themeSwitcher.isChecked = viewModel.getThemeSettings().isDarkTheme
        setListeners()
    }

    private fun setListeners() {
        binding.settingsToolbar.setNavigationOnClickListener {
            this.finish()
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
            viewModel.updateThemeSetting(ThemeSettings(checked))
        }

        binding.shareListItem.setOnClickListener {
            viewModel.shareApp()
        }

        binding.supportListItem.setOnClickListener {
            viewModel.openSupport()
        }

        binding.agreementListItem.setOnClickListener {
            viewModel.openTerms()
        }
    }
}