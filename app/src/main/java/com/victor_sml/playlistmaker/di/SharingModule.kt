package com.victor_sml.playlistmaker.di

import com.victor_sml.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.victor_sml.playlistmaker.sharing.data.SharingRepositoryImpl
import com.victor_sml.playlistmaker.sharing.data.api.StringSource
import com.victor_sml.playlistmaker.sharing.data.source.StringSourceImpl
import com.victor_sml.playlistmaker.sharing.domain.SharingInteractorImpl
import com.victor_sml.playlistmaker.sharing.domain.api.ExternalNavigator
import com.victor_sml.playlistmaker.sharing.domain.api.SharingInteractor
import com.victor_sml.playlistmaker.sharing.domain.api.SharingRepository
import org.koin.dsl.module

val sharingModule = module {
    single<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(get())
    }

    single<SharingRepository> {
        SharingRepositoryImpl(get())
    }

    single<StringSource> {
        StringSourceImpl(get())
    }
}