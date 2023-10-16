package com.victor_sml.playlistmaker.di.common

import com.victor_sml.playlistmaker.common.domain.GetStringUseCase
import com.victor_sml.playlistmaker.common.domain.TracksInteractorImpl
import com.victor_sml.playlistmaker.common.domain.api.TracksInteractor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    singleOf(::GetStringUseCase)

    singleOf(::TracksInteractorImpl) bind TracksInteractor::class
}