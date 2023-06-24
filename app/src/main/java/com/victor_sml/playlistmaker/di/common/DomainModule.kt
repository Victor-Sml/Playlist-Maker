package com.victor_sml.playlistmaker.di.common

import com.victor_sml.playlistmaker.common.domain.GetStringUseCase
import org.koin.dsl.module

val domainModule = module {
    single<GetStringUseCase> {
        GetStringUseCase(get())
    }
}