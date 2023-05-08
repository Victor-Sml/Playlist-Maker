package com.victor_sml.playlistmaker.di

import android.content.Context
import com.victor_sml.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.victor_sml.playlistmaker.sharing.data.SharingRepositoryImpl
import com.victor_sml.playlistmaker.sharing.data.api.StringSource
import com.victor_sml.playlistmaker.sharing.data.source.StringSourceImpl
import com.victor_sml.playlistmaker.sharing.domain.SharingInteractorImpl
import com.victor_sml.playlistmaker.sharing.domain.api.ExternalNavigator
import com.victor_sml.playlistmaker.sharing.domain.api.SharingInteractor
import com.victor_sml.playlistmaker.sharing.domain.api.SharingRepository
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val sharingModule = module {
    single<SharingInteractor> { (context: Context) ->
        SharingInteractorImpl(get() { parametersOf(context) }, get())
    }

    single<ExternalNavigator> { (context: Context) ->
        ExternalNavigatorImpl(context)
    }

    single<SharingRepository> {
        SharingRepositoryImpl(get())
    }

    single<StringSource> {
        StringSourceImpl(get())
    }
}