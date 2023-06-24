package com.victor_sml.playlistmaker.di.common

import com.victor_sml.playlistmaker.common.data.StringRepositoryImpl
import com.victor_sml.playlistmaker.common.data.StringSourceImpl
import com.victor_sml.playlistmaker.common.data.api.StringSource
import com.victor_sml.playlistmaker.common.domain.api.StringRepository
import org.koin.dsl.module

val dataModule = module {
    single<StringRepository> {
        StringRepositoryImpl(get())
    }

    single<StringSource> {
        StringSourceImpl(get())
    }
}