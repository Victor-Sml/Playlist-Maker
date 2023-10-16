package com.victor_sml.playlistmaker.di

import com.victor_sml.playlistmaker.common.data.TrackRepositoryImpl
import com.victor_sml.playlistmaker.common.domain.api.TrackRepository
import com.victor_sml.playlistmaker.library.domain.PlaylistInteractorImpl
import com.victor_sml.playlistmaker.library.data.PlaylistRepositoryImpl
import com.victor_sml.playlistmaker.library.domain.api.PlaylistInteractor
import com.victor_sml.playlistmaker.library.domain.api.PlaylistRepository
import com.victor_sml.playlistmaker.library.favorites.ui.stateholder.FavoritesViewModel
import com.victor_sml.playlistmaker.library.ui.stateholders.NewPlaylistViewModel
import com.victor_sml.playlistmaker.library.playlists.ui.stateholder.PlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val libraryModule = module {
    viewModelOf(::FavoritesViewModel)

    viewModelOf(::PlaylistViewModel)

    singleOf(::TrackRepositoryImpl) bind TrackRepository::class

    viewModelOf(::NewPlaylistViewModel)

    singleOf(::PlaylistInteractorImpl) bind PlaylistInteractor::class

    singleOf(::PlaylistRepositoryImpl) bind PlaylistRepository::class
}