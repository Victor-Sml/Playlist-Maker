package com.victor_sml.playlistmaker.di

import com.victor_sml.playlistmaker.library.ui.stateholder.FavoritesViewModel
import com.victor_sml.playlistmaker.library.ui.stateholder.PlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val libraryModule = module {
    viewModel {
       FavoritesViewModel()
    }

    viewModel {
        PlaylistViewModel()
    }
}