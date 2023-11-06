package com.victor_sml.playlistmaker.di

import com.victor_sml.playlistmaker.common.data.TrackRepositoryImpl
import com.victor_sml.playlistmaker.common.domain.api.tracks.TrackRepository
import com.victor_sml.playlistmaker.common.domain.PlaylistInteractorImpl
import com.victor_sml.playlistmaker.common.data.PlaylistRepositoryImpl
import com.victor_sml.playlistmaker.common.domain.api.playlists.PlaylistInteractor
import com.victor_sml.playlistmaker.common.domain.api.playlists.PlaylistRepository
import com.victor_sml.playlistmaker.library.favorites.ui.stateholder.FavoritesViewModel
import com.victor_sml.playlistmaker.library.playlistEditor.ui.stateholders.PlaylistEditorViewModel
import com.victor_sml.playlistmaker.library.playlists.ui.stateholder.PlaylistsViewModel
import com.victor_sml.playlistmaker.library.playlistDetails.ui.stateholder.PlaylistDetailsViewModel
import com.victor_sml.playlistmaker.library.playlistDetails.domain.SharePlaylistUseCase
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val libraryModule = module {
    viewModelOf(::FavoritesViewModel)

    viewModelOf(::PlaylistsViewModel)

    viewModelOf(::PlaylistDetailsViewModel)

    viewModelOf(::PlaylistEditorViewModel)

    singleOf(::TrackRepositoryImpl) bind TrackRepository::class

    singleOf(::PlaylistInteractorImpl) bind PlaylistInteractor::class

    singleOf(::PlaylistRepositoryImpl) bind PlaylistRepository::class

    singleOf(::SharePlaylistUseCase)
}