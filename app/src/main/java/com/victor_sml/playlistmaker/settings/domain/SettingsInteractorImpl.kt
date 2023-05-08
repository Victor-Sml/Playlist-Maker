package com.victor_sml.playlistmaker.settings.domain

import com.victor_sml.playlistmaker.settings.domain.api.SettingsInteractor
import com.victor_sml.playlistmaker.settings.domain.api.SettingsRepository
import com.victor_sml.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl(private val repository: SettingsRepository): SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings = repository.getThemeSettings()

    override fun updateThemeSetting(settings: ThemeSettings) {
        repository.updateThemeSetting(settings)
    }
}