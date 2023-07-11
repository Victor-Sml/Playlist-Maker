package com.victor_sml.playlistmaker.di

import com.victor_sml.playlistmaker.settings.data.SettingsRepositoryImpl
import com.victor_sml.playlistmaker.settings.data.api.SettingsStorage
import com.victor_sml.playlistmaker.settings.data.storage.SharedPrefSettingStorage
import com.victor_sml.playlistmaker.settings.domain.SettingsInteractorImpl
import com.victor_sml.playlistmaker.settings.domain.api.SettingsInteractor
import com.victor_sml.playlistmaker.settings.domain.api.SettingsRepository
import com.victor_sml.playlistmaker.settings.ui.stateholders.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val settingsModule = module {
    viewModelOf(::SettingsViewModel)

    singleOf(::SettingsInteractorImpl) bind SettingsInteractor::class

    singleOf(::SettingsRepositoryImpl) bind SettingsRepository::class

    singleOf(::SharedPrefSettingStorage) bind SettingsStorage::class
}