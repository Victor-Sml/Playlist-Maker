package com.victor_sml.playlistmaker.player.ui.view

import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.victor_sml.playlistmaker.common.ui.BottomSheetController

class PlayerBottomSheetController(
    bottomSheet: ViewGroup,
    toolbar: MaterialToolbar,
    private val overlay: View,
    private val titleTop: TextView,
) : BottomSheetController(bottomSheet, overlay, toolbar) {
    private val resources = bottomSheet.context.resources

    init {
        bottomSheetBehavior.state = STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                changeOverlayVisibility(newState)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                renderViews(slideOffset)
            }
        })
    }

    private fun changeOverlayVisibility(bottomSheetState: Int) {
        when (bottomSheetState) {
            STATE_HIDDEN -> overlay.isVisible = false
            else -> overlay.isVisible = true
        }
    }

    private fun renderViews(slideOffset: Float) {
        renderOverlay(slideOffset, OVERLAY_ALPHA_STEP)

        setRebelViewsColor(slideOffset) {
            ((resources.configuration.uiMode and UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_NO)
        }

        titleTop.isVisible = slideOffset > DARK_EDGE_SLIDE_OFFSET
    }

    companion object {
        const val OVERLAY_ALPHA_STEP = 0.1f
    }
}