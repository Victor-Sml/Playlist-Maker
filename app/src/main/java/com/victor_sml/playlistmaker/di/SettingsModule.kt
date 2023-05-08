package com.victor_sml.playlistmaker.di

import android.content.Context
import com.victor_sml.playlistmaker.settings.data.SettingsRepositoryImpl
import com.victor_sml.playlistmaker.settings.data.api.SettingsStorage
import com.victor_sml.playlistmaker.settings.data.storage.SharedPrefSettingStorage
import com.victor_sml.playlistmaker.settings.domain.SettingsInteractorImpl
import com.victor_sml.playlistmaker.settings.domain.api.SettingsInteractor
import com.victor_sml.playlistmaker.settings.domain.api.SettingsRepository
import com.victor_sml.playlistmaker.settings.ui.stateholders.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val settingsModule = module {
    viewModel { (context: Context) ->
        SettingsViewModel(get(), get(){ parametersOf(context)})
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