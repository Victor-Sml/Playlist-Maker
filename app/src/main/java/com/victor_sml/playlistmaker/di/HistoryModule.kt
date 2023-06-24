package com.victor_sml.playlistmaker.di

import com.victor_sml.playlistmaker.search.data.HistoryRepositoryImpl
import com.victor_sml.playlistmaker.search.data.api.StorageClient
import com.victor_sml.playlistmaker.search.data.storage.SharedPrefHistoryStorage
import com.victor_sml.playlistmaker.search.domain.HistoryInteractorImpl
import com.victor_sml.playlistmaker.search.domain.api.HistoryInteractor
import com.victor_sml.playlistmaker.search.domain.api.HistoryRepository
import org.koin.dsl.module

val historyModule = module {
    single<HistoryInteractor> {
        HistoryInteractorImpl(get(), get())
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(get())
    }

    single<StorageClient> {
        SharedPrefHistoryStorage(get(), get())
    }
}