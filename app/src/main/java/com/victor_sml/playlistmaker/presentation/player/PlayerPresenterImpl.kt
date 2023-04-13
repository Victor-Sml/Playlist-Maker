package com.victor_sml.playlistmaker.presentation.player

import androidx.lifecycle.LifecycleOwner
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.Utils.millisToMMSS
import com.victor_sml.playlistmaker.data.PlayerRepository

class PlayerPresenterImpl(
    private var view: PlayerView?,
    private val repository: PlayerRepository
) : PlayerPresenter {
    private var progressObserver =
        { progress: Int -> view?.updatePlaybackProgress(millisToMMSS(progress)) ?: Unit }

    private var availabilityObserver =
        { isEnable: Boolean -> view?.updateControllerAvailability(isEnable) ?: Unit }

    private var actionObserver =
        { action: PlayerRepository.ControllerAction ->
            when (action) {
                PlayerRepository.ControllerAction.PLAY -> view?.updateControllerImage(R.drawable.ic_play)
                    ?: Unit
                PlayerRepository.ControllerAction.PAUSE -> view?.updateControllerImage(R.drawable.ic_pause)
                    ?: Unit
            }
        }

    init {
        (view as LifecycleOwner).let { playerView ->
            repository.progress.observe(playerView, progressObserver)
            repository.controllerAvailability.observe(playerView, availabilityObserver)
            repository.controllerAction.observe(playerView, actionObserver)
        }
    }

    override fun playbackControl() {
        repository.playbackControl()
    }

    override fun onViewPaused() {
        repository.pausePlayer()
    }

    override fun onViewDestroyed() {
        view = null
        repository.run {
            controllerAction.removeObserver(actionObserver)
            controllerAvailability.removeObserver(availabilityObserver)
            progress.removeObserver(progressObserver)
            releasePlayer()
        }
    }
}