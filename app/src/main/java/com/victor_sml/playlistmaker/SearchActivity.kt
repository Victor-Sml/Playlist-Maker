package com.victor_sml.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {
    //Переменя для хранения поискового запроса
    private var searchQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val inputEditText = findViewById<EditText>(R.id.inputEditText)

        inputEditText.setText(searchQuery)

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString()
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        inputEditText.addTextChangedListener(searchTextWatcher)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideSoftInput()
            currentFocus?.clearFocus()
        }

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.searchToolbar)
        toolbar.setNavigationOnClickListener {
            val backToMainIntent = Intent(this@SearchActivity, MainActivity::class.java)
            startActivity(backToMainIntent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SEARCH_INPUT, searchQuery)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString(SEARCH_INPUT, "")
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun hideSoftInput() {
        val view = this.currentFocus
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    companion object {
        const val SEARCH_INPUT = "SEARCH_INPUT"
    }
}