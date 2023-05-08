package com.victor_sml.playlistmaker.settings.data.storage

import android.content.SharedPreferences
import com.victor_sml.playlistmaker.APP_THEME
import com.victor_sml.playlistmaker.App
import com.victor_sml.playlistmaker.settings.data.api.SettingsStorage
import com.victor_sml.playlistmaker.settings.domain.model.ThemeSettings

class SharedPrefSettingStorage(
    private val app: App,
    private val sharedPreferences: SharedPreferences
) : SettingsStorage {

    override fun getThemeSettings(): ThemeSettings = ThemeSettings(app.isDarkTheme)

    override fun putThemeSetting(settings: ThemeSettings) {
        sharedPreferences
            .edit()
            .putBoolean(APP_THEME, settings.isDarkTheme)
            .apply()
    }
}