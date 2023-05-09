package com.victor_sml.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val PM_PREFERENCES = "playlist_preferences"
const val APP_THEME = "dark_theme_enabled"

class App : Application() {
    private var darkTheme = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(PM_PREFERENCES, MODE_PRIVATE)
        switchTheme(sharedPreferences.getBoolean(APP_THEME, false))
    }

    fun isDarkThemeEnabled() = darkTheme

    fun getSharedPreferences() = sharedPreferences

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}