package com.victor_sml.playlistmaker.di

import com.victor_sml.playlistmaker.common.data.LibraryRepositoryImpl
import com.victor_sml.playlistmaker.common.domain.api.LibraryRepository
import com.victor_sml.playlistmaker.library.ui.stateholder.FavoritesViewModel
import com.victor_sml.playlistmaker.library.ui.stateholder.PlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val libraryModule = module {
    viewModelOf(::FavoritesViewModel)

    viewModelOf(::PlaylistViewModel)

    singleOf(::LibraryRepositoryImpl) bind LibraryRepository::class
}