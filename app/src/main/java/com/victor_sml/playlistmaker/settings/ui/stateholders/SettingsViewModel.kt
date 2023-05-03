package com.victor_sml.playlistmaker.settings.ui.stateholders

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.victor_sml.playlistmaker.App
import com.victor_sml.playlistmaker.creator.Creator
import com.victor_sml.playlistmaker.settings.domain.api.SettingsInteractor
import com.victor_sml.playlistmaker.settings.domain.model.ThemeSettings
import com.victor_sml.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {
    fun getThemeSettings(): ThemeSettings = settingsInteractor.getThemeSettings()

    fun updateThemeSetting(themeSettings: ThemeSettings) {
        settingsInteractor.updateThemeSetting(themeSettings)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)
                SettingsViewModel(
                    Creator.provideSettingInteractor(app),
                    Creator.provideSharingInteractor(context)
                )
            }
        }
    }
}