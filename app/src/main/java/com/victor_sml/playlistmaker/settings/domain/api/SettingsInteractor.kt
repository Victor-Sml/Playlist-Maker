package com.victor_sml.playlistmaker.settings.domain.api

import com.victor_sml.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}