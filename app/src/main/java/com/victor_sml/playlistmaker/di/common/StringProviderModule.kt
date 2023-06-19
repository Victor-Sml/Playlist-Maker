package com.victor_sml.playlistmaker.di.common

import com.victor_sml.playlistmaker.common.stringProvider.data.StringSourceImpl
import com.victor_sml.playlistmaker.common.stringProvider.data.StringRepositoryImpl
import com.victor_sml.playlistmaker.common.stringProvider.data.api.StringSource
import com.victor_sml.playlistmaker.common.stringProvider.domain.StringInteractorImpl
import com.victor_sml.playlistmaker.common.stringProvider.domain.api.StringInteractor
import com.victor_sml.playlistmaker.common.stringProvider.domain.api.StringRepository
import org.koin.dsl.module

val stringProviderModule = module {
    single<StringInteractor> {
        StringInteractorImpl(get())
    }

    single<StringRepository> {
        StringRepositoryImpl(get())
    }

    single<StringSource> {
        StringSourceImpl(get())
    }
}