package com.victor_sml.playlistmaker.player.ui.stateholders

import com.victor_sml.playlistmaker.R

enum class PlayerState(
    val controllerIconId: Int,
    val controllerAvailability: Boolean = true
) {
    DEFAULT(R.drawable.ic_play, false),
    PREPARED(R.drawable.ic_play),
    STARTED(R.drawable.ic_pause),
    PAUSED(R.drawable.ic_play),
    PLAYBACK_COMPLETION(R.drawable.ic_play)
}