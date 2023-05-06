package com.victor_sml.playlistmaker.settings.data.api

import com.victor_sml.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsStorage {
    fun getThemeSettings(): ThemeSettings
    fun putThemeSetting(settings: ThemeSettings)
}