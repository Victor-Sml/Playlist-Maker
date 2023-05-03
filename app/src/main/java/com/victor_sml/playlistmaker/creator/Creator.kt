package com.victor_sml.playlistmaker.creator

import android.content.Context
import com.victor_sml.playlistmaker.App
import com.victor_sml.playlistmaker.player.data.PlayerRepositoryImpl
import com.victor_sml.playlistmaker.player.data.player.PlayerImpl
import com.victor_sml.playlistmaker.player.domain.PlayerInteractorImpl
import com.victor_sml.playlistmaker.player.domain.api.PlayerInteractor
import com.victor_sml.playlistmaker.search.data.HistoryRepositoryImpl
import com.victor_sml.playlistmaker.search.data.SearchRepositoryImpl
import com.victor_sml.playlistmaker.search.data.network.RetrofitNetworkClient
import com.victor_sml.playlistmaker.search.data.storage.SharedPreferencesStorage
import com.victor_sml.playlistmaker.search.domain.api.HistoryInteractor
import com.victor_sml.playlistmaker.search.domain.api.HistoryRepository
import com.victor_sml.playlistmaker.search.domain.api.SearchInteractor
import com.victor_sml.playlistmaker.search.domain.api.SearchRepository
import com.victor_sml.playlistmaker.search.domain.HistoryInteractorImpl
import com.victor_sml.playlistmaker.search.domain.SearchInteractorImpl
import com.victor_sml.playlistmaker.settings.data.SettingsRepositoryImpl
import com.victor_sml.playlistmaker.settings.data.storage.SharedPrefSettingStorage
import com.victor_sml.playlistmaker.settings.domain.SettingsInteractorImpl
import com.victor_sml.playlistmaker.settings.domain.api.SettingsInteractor
import com.victor_sml.playlistmaker.settings.domain.api.SettingsRepository
import com.victor_sml.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.victor_sml.playlistmaker.sharing.data.SharingRepositoryImpl
import com.victor_sml.playlistmaker.sharing.data.source.StringSourceImpl
import com.victor_sml.playlistmaker.sharing.domain.SharingInteractorImpl
import com.victor_sml.playlistmaker.sharing.domain.api.SharingInteractor

object Creator {
    private fun getSearchRepository(context: Context): SearchRepository =
        SearchRepositoryImpl(RetrofitNetworkClient(context))

    fun provideSearchInteractor(context: Context): SearchInteractor =
        SearchInteractorImpl(getSearchRepository(context))

    private fun getHistoryRepository(app: App): HistoryRepository =
        HistoryRepositoryImpl(SharedPreferencesStorage(app.getSharedPreferences()))

    fun provideHistoryInteractor(app: App): HistoryInteractor =
        HistoryInteractorImpl(getHistoryRepository(app))

    fun providePlayerInteractor(): PlayerInteractor =
        PlayerInteractorImpl(PlayerRepositoryImpl(PlayerImpl()))

    private fun getSettingRepository(app: App): SettingsRepository {
        val settingsStorage = SharedPrefSettingStorage(app)
        return SettingsRepositoryImpl(settingsStorage)
    }

    fun provideSettingInteractor(app: App): SettingsInteractor =
        SettingsInteractorImpl(getSettingRepository(app))

    fun provideSharingInteractor(context: Context): SharingInteractor =
        SharingInteractorImpl(
            ExternalNavigatorImpl(context),
            SharingRepositoryImpl(StringSourceImpl(context))
        )
}