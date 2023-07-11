package com.victor_sml.playlistmaker.player.ui.stateholders

import com.victor_sml.playlistmaker.R

sealed class FavoriteState(val iconId: Int, val colorId: Int) {
    operator fun component1() = iconId
    operator fun component2() = colorId

    object Like : FavoriteState(LIKE_ICON_ID, LIKE_COLOR_ID)
    object Dislike : FavoriteState(DISLIKE_ICON_ID, DISLIKE_COLOR_ID)

    companion object {
        private const val LIKE_ICON_ID = R.drawable.ic_outline_heart
        private const val LIKE_COLOR_ID = R.color.yp_red

        private const val DISLIKE_ICON_ID = R.drawable.ic_heart
        private const val DISLIKE_COLOR_ID = R.color.yp_white
    }
}