package com.victor_sml.playlistmaker.di

import com.victor_sml.playlistmaker.settings.data.SettingsRepositoryImpl
import com.victor_sml.playlistmaker.settings.data.api.SettingsStorage
import com.victor_sml.playlistmaker.settings.data.storage.SharedPrefSettingStorage
import com.victor_sml.playlistmaker.settings.domain.SettingsInteractorImpl
import com.victor_sml.playlistmaker.settings.domain.api.SettingsInteractor
import com.victor_sml.playlistmaker.settings.domain.api.SettingsRepository
import com.victor_sml.playlistmaker.settings.ui.stateholders.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    viewModel {
        SettingsViewModel(get(), get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<SettingsStorage> {
        SharedPrefSettingStorage(get(), get())
    }
}