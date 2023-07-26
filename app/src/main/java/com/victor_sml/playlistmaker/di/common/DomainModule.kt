package com.victor_sml.playlistmaker.di.common

import com.victor_sml.playlistmaker.common.domain.GetStringUseCase
import com.victor_sml.playlistmaker.common.domain.TrackInteractorImpl
import com.victor_sml.playlistmaker.common.domain.api.TrackInteractor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    singleOf(::GetStringUseCase)

    singleOf(::TrackInteractorImpl) bind TrackInteractor::class
}