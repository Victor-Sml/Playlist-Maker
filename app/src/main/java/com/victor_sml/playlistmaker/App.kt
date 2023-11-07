package com.victor_sml.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.victor_sml.playlistmaker.di.common.commonModule
import com.victor_sml.playlistmaker.di.common.dataModule
import com.victor_sml.playlistmaker.di.common.domainModule
import com.victor_sml.playlistmaker.di.common.uiModule
import com.victor_sml.playlistmaker.di.historyModule
import com.victor_sml.playlistmaker.di.libraryModule
import com.victor_sml.playlistmaker.di.playerModule
import com.victor_sml.playlistmaker.di.searchModule
import com.victor_sml.playlistmaker.di.settingsModule
import com.victor_sml.playlistmaker.di.sharingModule
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
                commonModule,
                dataModule,
                domainModule,
                uiModule,
                historyModule,
                libraryModule,
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
            if (darkThemeEnabled) MODE_NIGHT_YES
            else MODE_NIGHT_NO
        )
    }
}