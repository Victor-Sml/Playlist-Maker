package com.victor_sml.playlistmaker.settings.data

import com.victor_sml.playlistmaker.settings.data.api.SettingsStorage
import com.victor_sml.playlistmaker.settings.domain.api.SettingsRepository
import com.victor_sml.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(
    private val settingsStorage: SettingsStorage,
) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings = settingsStorage.getThemeSettings()

    override fun updateThemeSetting(themeSettings: ThemeSettings) {
        settingsStorage.putThemeSetting(themeSettings)
    }
}