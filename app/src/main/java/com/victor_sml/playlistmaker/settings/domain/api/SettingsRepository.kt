package com.victor_sml.playlistmaker.settings.domain.api

import com.victor_sml.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(themeSettings: ThemeSettings)
}