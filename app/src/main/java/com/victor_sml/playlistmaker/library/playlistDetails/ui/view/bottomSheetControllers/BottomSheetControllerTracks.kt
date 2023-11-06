package com.victor_sml.playlistmaker.library.playlistDetails.ui.view.bottomSheetControllers

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.victor_sml.playlistmaker.R.color.steel_grey
import com.victor_sml.playlistmaker.common.ui.BottomSheetController

class BottomSheetControllerTracks(
    bottomSheet: ViewGroup,
    private val overlay: View,
    toolbar: MaterialToolbar,
    titleTop: TextView,
) : BottomSheetController(bottomSheet, overlay, toolbar) {

    init {
        bottomSheetBehavior.state = STATE_COLLAPSED

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                renderOverlay(slideOffset)
                setRebelViewsColor(slideOffset)

                titleTop.isVisible = slideOffset > DARK_EDGE_SLIDE_OFFSET
            }
        })
    }

    override fun renderOverlay(bottomSheetSlideOffset: Float, alphaStep: Float) {
        if (bottomSheetSlideOffset > DEFAULT_SLIDE_OFFSET) revealOverlay(bottomSheetSlideOffset)
        else leaveOverlay()
    }

    private fun revealOverlay(alpha: Float) {
        overlay.setBackgroundColor(overlay.context.resources.getColor(steel_grey, null))
        overlay.alpha = alpha
        overlay.isVisible = true
    }

    private fun leaveOverlay() {
        overlay.isVisible = false
        overlay.alpha = DEFAULT_OVERLAY_ALPHA
    }

    companion object {
        const val DEFAULT_OVERLAY_ALPHA = 0f
        const val DEFAULT_SLIDE_OFFSET = 0f
    }
}