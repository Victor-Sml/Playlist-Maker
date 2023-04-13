package com.victor_sml.playlistmaker

import com.victor_sml.playlistmaker.data.PlayerRepository
import com.victor_sml.playlistmaker.data.PlayerRepositoryImpl
import com.victor_sml.playlistmaker.data.player.PlayerImpl
import com.victor_sml.playlistmaker.presentation.player.PlayerPresenter
import com.victor_sml.playlistmaker.presentation.player.PlayerPresenterImpl
import com.victor_sml.playlistmaker.presentation.player.PlayerView

object Creator {
    private fun getPlayerRepository(source: String?): PlayerRepository =
        PlayerRepositoryImpl(PlayerImpl(), source)

    fun providePlayerPresenter(view: PlayerView, previewUrl: String?): PlayerPresenter {
        val repository = getPlayerRepository(previewUrl)
        return PlayerPresenterImpl(
            view = view,
            repository = repository
        )
    }
}