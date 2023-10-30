package com.victor_sml.playlistmaker.main.ui.view

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.ui.nonBottomNavFragment.api.NonBottomNavFragment
import com.victor_sml.playlistmaker.common.utils.UtilsUi.doOnApplyWindowInsets
import com.victor_sml.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    var isCurrentDestinationWithBottomNav = true

    private val fragmentAnimationListener = object : AnimationListener {
        override fun onAnimationStart(p0: Animation?) {
        }

        override fun onAnimationEnd(p0: Animation?) {
            binding.bnvMain.isVisible = isCurrentDestinationWithBottomNav
        }

        override fun onAnimationRepeat(p0: Animation?) {
        }
    }

    private val onBackStackChangedListener = OnBackStackChangedListener {
        val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment

        if (currentFragment is NonBottomNavFragment) {
            binding.bnvMain.isVisible = false
            currentFragment.setAnimationListener(fragmentAnimationListener)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeUI()
    }

    private fun initializeUI() {
        window.setDecorFitsSystemWindows(false)

        applyWindowInsets()
        initNavigation()
        setListener()
    }

    private fun applyWindowInsets() {
        binding.bnvMain.doOnApplyWindowInsets(left = true, right = true, bottom = true)
    }

    private fun setListener() {
        navHostFragment.childFragmentManager
            .addOnBackStackChangedListener(onBackStackChangedListener)

        binding.bnvMain.setOnItemReselectedListener {
            return@setOnItemReselectedListener
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.destination_library,
                R.id.destination_search,
                R.id.destination_settings -> isCurrentDestinationWithBottomNav = true

                else -> isCurrentDestinationWithBottomNav = false
            }
        }
    }

    private fun initNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fc_main) as NavHostFragment

        navController = navHostFragment.navController

        binding.bnvMain.setupWithNavController(navController)
    }
}