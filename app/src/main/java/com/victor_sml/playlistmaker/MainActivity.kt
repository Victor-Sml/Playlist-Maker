package com.victor_sml.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button)
        val searchOnClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchIntent)
            }
        }
        searchButton.setOnClickListener(searchOnClickListener)

        val libraryButton = findViewById<Button>(R.id.library_button)
        libraryButton.setOnClickListener {
            val libraryIntent = Intent(this@MainActivity, LibraryActivity::class.java)
            startActivity(libraryIntent)
        }

        val settingsButton = findViewById<Button>(R.id.settings_button)
        val settingsOnClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val settingsIntent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(settingsIntent)
            }
        }
        settingsButton.setOnClickListener(settingsOnClickListener)
    }
}