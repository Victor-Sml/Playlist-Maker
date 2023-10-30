package com.victor_sml.playlistmaker.common.ui

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.victor_sml.playlistmaker.common.Constants.TOOLBAR_LIGHT_COLOR_ID
import com.victor_sml.playlistmaker.common.Constants.TOOLBAR_NIGHT_COLOR_ID
import com.victor_sml.playlistmaker.common.utils.Utils.dpToPx
import com.victor_sml.playlistmaker.common.utils.UtilsUi.getInvertedBottom

open class BottomSheetController(
    private val bottomSheet: ViewGroup,
    private val overlay: View,
    private val toolbar: MaterialToolbar,
) {
    val bottomSheetBehavior
        get() = BottomSheetBehavior.from(bottomSheet)

    private val context = bottomSheet.context

    private val windowInsetsController: WindowInsetsControllerCompat?
        get() =
            if (bottomSheet.isAttachedToWindow)
                (context as Activity).window.let { window ->
                    return WindowInsetsControllerCompat(window, window.decorView)
                }
            else null

    open fun renderOverlay(
        bottomSheetSlideOffset: Float,
        alphaStep: Float = DEFAULT_OVERLAY_ALPHA_STEP,
    ) {
        overlay.alpha = bottomSheetSlideOffset + alphaStep
    }

    fun setBottomSheetMaxHeight(view: View, gapDp: Int = DEFAULT_BOTTOM_SHEET_GAP_DP) {
        bottomSheetBehavior.maxHeight = view.getInvertedBottom() - gapDp.dpToPx()
    }

    fun setBottomSheetPeekHeight(view: View, gapDp: Int = DEFAULT_BOTTOM_SHEET_GAP_DP) {
        bottomSheetBehavior.peekHeight = view.getInvertedBottom() - gapDp.dpToPx()
    }

    fun setRebelViewsColor(bottomSheetSlideOffset: Float, isApplicable: () -> Boolean = { true }) {
        if (isApplicable()) {
            if (bottomSheetSlideOffset >= DARK_EDGE_SLIDE_OFFSET) changeRebelViewsColor(false)
            else changeRebelViewsColor(true)
        }
    }

    private fun changeRebelViewsColor(isLight: Boolean) {
        val color: (colorId: Int) -> Int = { context.getColor(it) }

        windowInsetsController?.isAppearanceLightStatusBars = isLight

        if (isLight)
            toolbar.setNavigationIconTint(color(TOOLBAR_NIGHT_COLOR_ID))
        else
            toolbar.setNavigationIconTint(color(TOOLBAR_LIGHT_COLOR_ID))
    }

    companion object {
        const val DEFAULT_OVERLAY_ALPHA_STEP = 0f
        const val DEFAULT_BOTTOM_SHEET_GAP_DP = 0

        const val DARK_EDGE_SLIDE_OFFSET = 0.8f
    }
}