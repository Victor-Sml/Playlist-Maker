package com.victor_sml.playlistmaker.di

import com.victor_sml.playlistmaker.search.data.HistoryRepositoryImpl
import com.victor_sml.playlistmaker.search.data.api.StorageClient
import com.victor_sml.playlistmaker.search.data.storage.SharedPrefHistoryStorage
import com.victor_sml.playlistmaker.search.domain.HistoryInteractorImpl
import com.victor_sml.playlistmaker.search.domain.api.HistoryInteractor
import com.victor_sml.playlistmaker.search.domain.api.HistoryRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val historyModule = module {
    singleOf(::HistoryInteractorImpl) bind HistoryInteractor::class

    singleOf(::HistoryRepositoryImpl) bind HistoryRepository::class

    singleOf(::SharedPrefHistoryStorage) bind StorageClient::class
}