package com.victor_sml.playlistmaker

import android.content.Context
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
    private lateinit var inputEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.searchToolbar)
        inputEditText = findViewById(R.id.inputEditText)

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

        toolbar.setNavigationOnClickListener {
            this.finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.run {
            putString(SEARCH_INPUT, searchQuery)
            putInt(INPUT_START_SELECTION, inputEditText.selectionStart)
            putInt(INPUT_END_SELECTION, inputEditText.selectionEnd)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.run {
            inputEditText.setText(getString(SEARCH_INPUT, ""))
            inputEditText.setSelection(getInt(INPUT_START_SELECTION), getInt(INPUT_END_SELECTION))
        }
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
        const val INPUT_START_SELECTION = "START_SELECTION"
        const val INPUT_END_SELECTION = "END_SELECTION"
    }
}
