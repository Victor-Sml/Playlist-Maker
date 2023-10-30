package com.victor_sml.playlistmaker.common.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.victor_sml.playlistmaker.common.Constants.BIG_ARTWORK_RADIUS_DP
import com.victor_sml.playlistmaker.common.Constants.Y_COORDINATE
import com.victor_sml.playlistmaker.common.utils.Utils.dpToPx

object UtilsUi {

    fun View.doOnApplyWindowInsets(
        left: Boolean = false,
        top: Boolean = false,
        right: Boolean = false,
        bottom: Boolean = false,
        action: ((view: View, insets: Insets) -> Unit)? = null,
    ) {
        ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                if (top) topMargin = insets.top
                if (left) leftMargin = insets.left
                if (right) rightMargin = insets.right
                if (bottom) bottomMargin = insets.bottom
            }

            if (action != null) {
                action(view, insets)
            }

            WindowInsetsCompat.CONSUMED
        }
    }

    //Возвращает число пикселей от нижней границы экрана до View.bottom.
    fun View.getInvertedBottom(): Int {
        val windowManager = this.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayHeight = windowManager.currentWindowMetrics.bounds.height()
        val viewLocation = IntArray(2)

        this.getLocationInWindow(viewLocation)

        return displayHeight - (viewLocation[Y_COORDINATE] + (this.height + this.marginBottom))
    }

    fun ImageView.setImageFrom(path: String?, placeholderId: Int) {
        Glide.with(this)
            .load(path)
            .placeholder(placeholderId)
            .transform(CenterCrop(), RoundedCorners(BIG_ARTWORK_RADIUS_DP.dpToPx()))
            .into(this)
    }
}