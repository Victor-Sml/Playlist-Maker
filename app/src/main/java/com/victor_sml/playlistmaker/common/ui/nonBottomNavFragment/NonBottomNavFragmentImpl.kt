package com.victor_sml.playlistmaker.common.ui.nonBottomNavFragment

import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.viewbinding.ViewBinding
import com.victor_sml.playlistmaker.common.ui.BindingFragment
import com.victor_sml.playlistmaker.common.ui.nonBottomNavFragment.api.NonBottomNavFragment

abstract class NonBottomNavFragmentImpl<T : ViewBinding> : BindingFragment<T>(),
    NonBottomNavFragment {

    private var animationListener: AnimationListener? = null

    override fun setAnimationListener(animationListener: AnimationListener) {
        this.animationListener = animationListener
    }

    private fun removeAnimationListener() {
        this.animationListener = null
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val animation = AnimationUtils.loadAnimation(context, nextAnim)

        if (enter) return animation
        animation.setAnimationListener(animationListener)

        return animation
    }

    override fun onDestroy() {
        super.onDestroy()

        removeAnimationListener()
    }
}