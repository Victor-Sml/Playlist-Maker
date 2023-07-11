package com.victor_sml.playlistmaker.di.common

import androidx.room.Room
import com.victor_sml.playlistmaker.common.data.LibraryRepositoryImpl
import com.victor_sml.playlistmaker.common.data.StringRepositoryImpl
import com.victor_sml.playlistmaker.common.data.StringSourceImpl
import com.victor_sml.playlistmaker.common.data.api.StringSource
import com.victor_sml.playlistmaker.common.data.db.AppDatabase
import com.victor_sml.playlistmaker.common.domain.api.LibraryRepository
import com.victor_sml.playlistmaker.common.domain.api.StringRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::StringRepositoryImpl) bind StringRepository::class

    singleOf(::StringSourceImpl) bind StringSource::class

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db").build()
    }

    singleOf(::LibraryRepositoryImpl) bind LibraryRepository::class
}