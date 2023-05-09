package com.victor_sml.playlistmaker.settings.ui.stateholders

import androidx.lifecycle.ViewModel
import com.victor_sml.playlistmaker.settings.domain.api.SettingsInteractor
import com.victor_sml.playlistmaker.settings.domain.model.ThemeSettings
import com.victor_sml.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {
    fun getThemeSettings(): ThemeSettings = settingsInteractor.getThemeSettings()

    fun updateThemeSetting(themeSettings: ThemeSettings) {
        settingsInteractor.updateThemeSetting(themeSettings)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }
}