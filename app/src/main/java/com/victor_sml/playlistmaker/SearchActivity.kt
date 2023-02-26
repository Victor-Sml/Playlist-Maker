package com.victor_sml.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import com.victor_sml.playlistmaker.TracksHandler.RequestState

class SearchActivity : AppCompatActivity() {

    private lateinit var tracksHandler: TracksHandler

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
    private lateinit var clearButton: ImageView

    private lateinit var historyTitle: TextView

    private lateinit var toolbar: Toolbar

    private val iTunesService = initItunesService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setListeners()

        tracksHandler = TracksHandler(
            recyclerView = findViewById(R.id.rw_tracklist),
            nothingFound = findViewById(R.id.nothing_found_layout),
            connectionFailure = findViewById(R.id.connection_failure_layout),
            historyTitle
        )
        inputEditText.requestFocus()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.run {
            putString(SEARCH_INPUT, searchQuery)
            putInt(INPUT_START_SELECTION, inputEditText.selectionStart)
            putInt(INPUT_END_SELECTION, inputEditText.selectionEnd)
            putInt(RECYCLER_VIEW_VISIBILITY, tracksHandler.recyclerView.visibility)
            putInt(NOTHING_FOUND_VIEW_VISIBILITY, tracksHandler.nothingFound.visibility)
            putInt(
                CONNECTION_FAILURE_VIEW_VISIBILITY,
                tracksHandler.connectionFailure.visibility
            )
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.run {
            inputEditText.setText(getString(SEARCH_INPUT, ""))
            inputEditText.setSelection(getInt(INPUT_START_SELECTION), getInt(INPUT_END_SELECTION))
            tracksHandler.recyclerView.visibility = getInt(RECYCLER_VIEW_VISIBILITY)
            tracksHandler.nothingFound.visibility = getInt(NOTHING_FOUND_VIEW_VISIBILITY)
            tracksHandler.connectionFailure.visibility =
                getInt(CONNECTION_FAILURE_VIEW_VISIBILITY)
        }
    }

    private fun initViews() {
        historyTitle = findViewById(R.id.tw_historyTitle)
        refreshButton = findViewById(R.id.btn_refresh)
        clearButton = findViewById(R.id.clearIcon)
        toolbar = findViewById(R.id.searchToolbar)
        inputEditText = findViewById(R.id.inputEditText)

    }

    private fun initItunesService(): ItunesAPI =
        Retrofit.Builder().baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesAPI::class.java)

    private fun setListeners() {
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString()
                clearButton.visibility = clearButtonVisibility(s)

                if (s.isNullOrEmpty()) tracksHandler.showHistory()
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
                        tracksHandler.showSearchResult(RequestState.NOTHING_FOUND)
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    tracksHandler.showSearchResult(RequestState.CONNECTION_FAILURE)
                }
            })
    }

    companion object {
        const val ITUNES_BASE_URL = "https://itunes.apple.com"

        /**
         * Константы для сохранения состояния UI.
         */
        const val SEARCH_INPUT = "SEARCH_INPUT"
        const val INPUT_START_SELECTION = "START_SELECTION"
        const val INPUT_END_SELECTION = "END_SELECTION"
        const val RECYCLER_VIEW_VISIBILITY = "RECYCLER_VISIBILITY"
        const val NOTHING_FOUND_VIEW_VISIBILITY = "NOTHING_FOUND_VISIBILITY"
        const val CONNECTION_FAILURE_VIEW_VISIBILITY = "CONNECTION_FAILURE_VISIBILITY"
    }
}