package com.victor_sml.playlistmaker.main.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var searchMenu: MenuItem
    private lateinit var libraryMenu: MenuItem
    private lateinit var settingsMenu: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fc_main) as NavHostFragment

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.destination_search,
                R.id.destination_library,
                R.id.destination_settings
            )
        )

        navController = navHostFragment.navController

        binding.bnvMain.setupWithNavController(navController)
        binding.tbMain.setupWithNavController(navController)
        binding.tbMain.setupWithNavController(navController, appBarConfiguration)

        initMenuItems()
        setListener()

        binding.bnvMain.setOnItemReselectedListener {
            return@setOnItemReselectedListener
        }
    }

    private fun initMenuItems() {
        binding.bnvMain.menu.apply {
            searchMenu = findItem(R.id.destination_search)
            libraryMenu = findItem(R.id.destination_library)
            settingsMenu = findItem(R.id.destination_settings)
        }
    }

    private fun setListener() {
        binding.bnvMain.setOnItemSelectedListener { item ->
            when (item) {
                searchMenu -> {
                    navController.navigate(R.id.action_global_search, null)
                    true
                }
                libraryMenu -> {
                    navController.navigate(R.id.action_global_library, null)
                    true
                }
                settingsMenu -> {
                    navController.navigate(R.id.action_global_settings, null)
                    true
                }
                else -> {
                    true
                }
            }
        }
    }
}