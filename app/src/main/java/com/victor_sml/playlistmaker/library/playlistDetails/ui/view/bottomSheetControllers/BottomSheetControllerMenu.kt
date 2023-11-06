package com.victor_sml.playlistmaker.library.playlistDetails.ui.view.bottomSheetControllers

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.ui.BottomSheetController

class BottomSheetControllerMenu(
    bottomSheet: ViewGroup,
    overlay: View,
    toolbar: MaterialToolbar,
    bottomSheetControllerTack: BottomSheetController
) : BottomSheetController(bottomSheet, overlay, toolbar) {
    private val resources = bottomSheet.context.resources

    init {
        bottomSheetBehavior.state = STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    STATE_HIDDEN -> {
                        overlay.isVisible = false
                        bottomSheetControllerTack.bottomSheetBehavior.isDraggable = true
                    }

                    else -> {
                        overlay.setBackgroundColor(resources.getColor(R.color.yp_black, null))
                        overlay.isVisible = true
                        bottomSheetControllerTack.bottomSheetBehavior.isDraggable = false
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                renderOverlay(slideOffset, OVERLAY_ALPHA_STEP)
            }
        })
    }

    companion object {
        const val OVERLAY_ALPHA_STEP = 0.7f
    }
}