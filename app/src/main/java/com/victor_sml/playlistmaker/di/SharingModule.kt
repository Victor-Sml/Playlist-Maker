package com.victor_sml.playlistmaker.di

import com.victor_sml.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.victor_sml.playlistmaker.sharing.data.SharingRepositoryImpl
import com.victor_sml.playlistmaker.sharing.domain.SharingInteractorImpl
import com.victor_sml.playlistmaker.sharing.domain.api.ExternalNavigator
import com.victor_sml.playlistmaker.sharing.domain.api.SharingInteractor
import com.victor_sml.playlistmaker.sharing.domain.api.SharingRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharingModule = module {
    singleOf(::SharingInteractorImpl) bind SharingInteractor::class

    singleOf(::ExternalNavigatorImpl) bind ExternalNavigator::class

    singleOf(::SharingRepositoryImpl) bind SharingRepository::class
}