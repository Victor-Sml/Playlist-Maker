package com.victor_sml.playlistmaker

import com.victor_sml.playlistmaker.data.PlayerRepositoryImpl
import com.victor_sml.playlistmaker.data.player.PlayerImpl
import com.victor_sml.playlistmaker.domain.api.PlayerInteractor
import com.victor_sml.playlistmaker.domain.PlayerInteractorImpl
import com.victor_sml.playlistmaker.domain.api.PlayerRepository
import com.victor_sml.playlistmaker.presentation.player.api.PlayerPresenter
import com.victor_sml.playlistmaker.presentation.player.PlayerPresenterImpl
import com.victor_sml.playlistmaker.presentation.player.api.PlayerView

object Creator {
    private fun getPlayerInteractor(): PlayerInteractor =
        PlayerInteractorImpl(getPlayerRepository())

    private fun getPlayerRepository(): PlayerRepository =
        PlayerRepositoryImpl(PlayerImpl())

    fun providePlayerPresenter(
        view: PlayerView,
        trackSource: String
    ): PlayerPresenter {
        val interactor = getPlayerInteractor()
        return PlayerPresenterImpl(view, interactor, trackSource)
    }
}