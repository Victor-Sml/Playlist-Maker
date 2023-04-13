package com.victor_sml.playlistmaker.presentation.ui.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.victor_sml.playlistmaker.ItunesAPI
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.TracksHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.victor_sml.playlistmaker.TracksHandler.RequestState.REQUESTING
import com.victor_sml.playlistmaker.TracksHandler.RequestState.CONNECTION_FAILURE
import com.victor_sml.playlistmaker.TracksHandler.RequestState.NOTHING_FOUND
import com.victor_sml.playlistmaker.TracksResponse

class SearchActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { sendRequest(searchQuery) }

    /**
     * Хранит текст из поля поискового запроса [inputEditText].
     * */
    private var searchQuery: String = ""

    /**
     * Поле для ввода поискового запроса.
     */
    private lateinit var inputEditText: EditText

    /**
     * Кнопка обновить.
     */
    private lateinit var refreshButton: Button

    /**
     * Кнопка для отчистки поискового запроса.
     */
    private lateinit var clearButton: ImageView //
    private lateinit var toolbar: Toolbar
    private lateinit var iTunesService: ItunesAPI
    private lateinit var tracksHandler: TracksHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setListeners()
        iTunesService = initItunesService()
        tracksHandler = TracksHandler(
            recyclerView = findViewById(R.id.rw_tracklist),
            nothingFound = findViewById(R.id.nothing_found_layout),
            connectionFailure = findViewById(R.id.connection_failure_layout),
            progressBar = findViewById(R.id.progressBar),
            historyTitle = findViewById(R.id.tv_historyTitle)
        )
        inputEditText.requestFocus()
    }

    override fun onStop() {
        super.onStop()
        tracksHandler.saveHistory()
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

    private fun initViews() {
        refreshButton = findViewById(R.id.btn_refresh)
        clearButton = findViewById(R.id.clearIcon)
        toolbar = findViewById(R.id.searchToolbar)
        inputEditText = findViewById(R.id.inputEditText)
    }

    private fun initItunesService(): ItunesAPI {
        return Retrofit.Builder().baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesAPI::class.java)
    }

    private fun searchDebounce(searchRunnable: Runnable) {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun setListeners() {
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString()
                clearButton.visibility = clearButtonVisibility(s)

                if (s.isNullOrEmpty()) {
                    handler.removeCallbacks(searchRunnable)
                    tracksHandler.showHistory()
                } else {
                    searchDebounce(searchRunnable)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        inputEditText.addTextChangedListener(searchTextWatcher)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            tracksHandler.clearSearchResult()
            hideSoftInput()
            currentFocus?.clearFocus()
        }

        refreshButton.setOnClickListener {
            sendRequest(searchQuery)
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handler.removeCallbacks(searchRunnable)
                sendRequest(searchQuery)
                hideSoftInput()
                true
            } else {
                false
            }
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && (view as EditText).text.isEmpty()) {
                tracksHandler.showHistory()
            }
        }

        toolbar.setNavigationOnClickListener {
            this.finish()
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

    private fun sendRequest(searchQuery: String) {
        if (searchQuery.trim().isEmpty()) return // Не ищем пробелы
        tracksHandler.showPlaceholder(REQUESTING) // Запускаем прогрессбар

        iTunesService.getTracks(searchQuery)
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    if (response.isSuccessful && response.body()?.results?.isNotEmpty() == true) {
                        response.body()?.results?.let { tracksHandler.showSearchResult(it) }
                    }
                    if (response.isSuccessful && response.body()?.results?.isNotEmpty() == false) {
                        tracksHandler.showPlaceholder(NOTHING_FOUND)
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    tracksHandler.showPlaceholder(CONNECTION_FAILURE)
                }
            })
    }

    companion object {
        const val ITUNES_BASE_URL = "https://itunes.apple.com"
        const val SEARCH_DEBOUNCE_DELAY = 2000L

        /**
         * Константы для сохранения состояния UI.
         */
        const val SEARCH_INPUT = "SEARCH_INPUT"
        const val INPUT_START_SELECTION = "START_SELECTION"
        const val INPUT_END_SELECTION = "END_SELECTION"
    }
}