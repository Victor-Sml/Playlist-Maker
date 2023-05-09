package com.victor_sml.playlistmaker.di

import android.media.MediaPlayer
import com.victor_sml.playlistmaker.player.data.PlayerRepositoryImpl
import com.victor_sml.playlistmaker.player.data.api.Player
import com.victor_sml.playlistmaker.player.data.player.PlayerImpl
import com.victor_sml.playlistmaker.player.domain.PlayerInteractorImpl
import com.victor_sml.playlistmaker.player.domain.api.PlayerInteractor
import com.victor_sml.playlistmaker.player.domain.api.PlayerRepository
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    viewModel { (trackSource: String?) ->
        PlayerViewModel(trackSource, get(), get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    factory<Player> {
        PlayerImpl(get())
    }

    factory {
        MediaPlayer()
    }
}