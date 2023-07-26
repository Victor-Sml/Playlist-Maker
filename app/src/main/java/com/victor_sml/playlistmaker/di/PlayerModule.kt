package com.victor_sml.playlistmaker.di

import android.media.MediaPlayer
import com.victor_sml.playlistmaker.player.data.PlayerRepositoryImpl
import com.victor_sml.playlistmaker.player.data.api.Player
import com.victor_sml.playlistmaker.player.data.player.PlayerImpl
import com.victor_sml.playlistmaker.player.domain.PlayerInteractorImpl
import com.victor_sml.playlistmaker.player.domain.api.PlayerInteractor
import com.victor_sml.playlistmaker.player.domain.api.PlayerRepository
import com.victor_sml.playlistmaker.player.ui.stateholders.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val playerModule = module {
    viewModelOf(::PlayerViewModel)

    factoryOf(::PlayerInteractorImpl) bind PlayerInteractor::class

    factoryOf(::PlayerRepositoryImpl) bind PlayerRepository::class

    factoryOf(::PlayerImpl) bind Player::class

    factoryOf(::MediaPlayer)
}