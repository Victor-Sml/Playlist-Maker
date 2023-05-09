package com.victor_sml.playlistmaker.settings.data.storage

import com.victor_sml.playlistmaker.APP_THEME
import com.victor_sml.playlistmaker.App
import com.victor_sml.playlistmaker.settings.data.api.SettingsStorage
import com.victor_sml.playlistmaker.settings.domain.model.ThemeSettings

class SharedPrefSettingStorage(private val app: App) : SettingsStorage {
    private val sharedPreferences = app.getSharedPreferences()

    override fun getThemeSettings(): ThemeSettings =
        ThemeSettings(app.isDarkThemeEnabled())

    override fun putThemeSetting(settings: ThemeSettings) {
        sharedPreferences
            .edit()
            .putBoolean(APP_THEME, settings.isDarkTheme)
            .apply()
    }
}