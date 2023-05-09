package com.victor_sml.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.victor_sml.playlistmaker.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

const val PM_PREFERENCES = "playlist_preferences"
const val APP_THEME = "dark_theme_enabled"

class App : Application() {
    var isDarkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                appModule,
                historyModule,
                playerModule,
                searchModule,
                settingsModule,
                sharingModule
            )
        }

        switchTheme(getSharedPreferences(PM_PREFERENCES, MODE_PRIVATE).getBoolean(APP_THEME, false))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        isDarkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}