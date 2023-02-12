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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Константы для сохранения состояния UI.
 */
const val SEARCH_INPUT = "SEARCH_INPUT"
const val INPUT_START_SELECTION = "START_SELECTION"
const val INPUT_END_SELECTION = "END_SELECTION"
const val RECYCLER_VIEW_VISIBILITY = "RECYCLER_VISIBILITY"
const val NOTHING_FOUND_VIEW_VISIBILITY = "NOTHING_FOUND_VISIBILITY"
const val CONNECTION_FAILURE_VIEW_VISIBILITY = "CONNECTION_FAILURE_VISIBILITY"

class SearchActivity : AppCompatActivity() {
    /**
     * Хранит текст из поля поискового запроса [inputEditText].
     * */
    private var searchQuery: String = ""

    /**
     * RecyclerView для отображенитя списка треков.
     */
    private lateinit var adapter: TrackAdapter

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

    private lateinit var toolbar: Toolbar

    /**
     * [ViewContainer] содержит ссылки на View сообщений-заглушек и RecyclerView для треклиста.
     */
    private lateinit var viewContainer: ViewContainer

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val iTunesService = initItunesService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewContainer = ViewContainer(
            recyclerView = findViewById(R.id.rw_tracklist),
            nothingFoundView = findViewById(R.id.nothing_found_layout),
            connectionFailureView = findViewById(R.id.connection_failure_layout)
        )

        initViews()
        initRecyclerView()
        setListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.run {
            putString(SEARCH_INPUT, searchQuery)
            putInt(INPUT_START_SELECTION, inputEditText.selectionStart)
            putInt(INPUT_END_SELECTION, inputEditText.selectionEnd)
            putInt(RECYCLER_VIEW_VISIBILITY, viewContainer.recyclerView.visibility)
            putInt(NOTHING_FOUND_VIEW_VISIBILITY, viewContainer.nothingFoundView.visibility)
            putInt(
                CONNECTION_FAILURE_VIEW_VISIBILITY,
                viewContainer.connectionFailureView.visibility
            )
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.run {
            inputEditText.setText(getString(SEARCH_INPUT, ""))
            inputEditText.setSelection(getInt(INPUT_START_SELECTION), getInt(INPUT_END_SELECTION))
            viewContainer.recyclerView.visibility = getInt(RECYCLER_VIEW_VISIBILITY)
            viewContainer.nothingFoundView.visibility = getInt(NOTHING_FOUND_VIEW_VISIBILITY)
            viewContainer.connectionFailureView.visibility =
                getInt(CONNECTION_FAILURE_VIEW_VISIBILITY)
        }
    }

    private fun initViews() {
        refreshButton = findViewById(R.id.btn_refresh)
        clearButton = findViewById(R.id.clearIcon)
        toolbar = findViewById(R.id.searchToolbar)
        inputEditText = findViewById(R.id.inputEditText)
    }

    private fun initRecyclerView() {
        adapter = TrackAdapter(arrayListOf())
        viewContainer.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewContainer.recyclerView.adapter = adapter
    }

    private fun initItunesService(): ItunesAPI =
        Retrofit.Builder().baseUrl(iTunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesAPI::class.java)

    private fun setListeners() {
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
            viewContainer.clearVisibility()
        }

        refreshButton.setOnClickListener {
            sendRequest(searchQuery)
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendRequest(searchQuery)
                true
            } else {
                false
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

    private fun showSoftInput() {
        val inputMethodManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun handleResponse(
        requestState: RequestState,
        response: Response<TracksResponse>? = null
    ) {
        viewContainer.setVisibility(requestState)
        if (requestState == RequestState.FOUND) adapter.update(response?.body()?.results!!)
    }

    private fun sendRequest(searchQuery: String) {
        iTunesService.getTracks(searchQuery)
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    if (response.isSuccessful && response.body()?.results?.isNotEmpty() == true) {
                        handleResponse(RequestState.FOUND, response)
                    }
                    if (response.isSuccessful && response.body()?.results?.isNotEmpty() == false) {
                        handleResponse(RequestState.NOTHING_FOUND, response)
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    handleResponse(RequestState.CONNECTION_FAILURE)
                }
            })
    }

    /**
     * Содержит View сообщений-заглушек: [nothingFoundView] - "Ничего не нашлось",
     * [connectionFailureView] - "Проблемы со связью", [recyclerView] - RecyclerView с треклистом и
     * управляет отображением их на экране.
     */
    private inner class ViewContainer(
        val recyclerView: RecyclerView,
        val nothingFoundView: View,
        val connectionFailureView: View
    ) {
        /**
         * В соответствии с [RequestState] переданном в [requestState] устанавливает значение visibility
         * для [recyclerView], [nothingFoundView], [connectionFailureView].
         */
        fun setVisibility(requestState: RequestState) {
            recyclerView.visibility = requestState.recyclerVisibility
            nothingFoundView.visibility = requestState.nothingFoundViewVisibility
            connectionFailureView.visibility = requestState.connectionFailureViewVisibility
            updateInputMethod(requestState)
        }

        fun clearVisibility() {
            recyclerView.visibility = View.GONE
            nothingFoundView.visibility = View.GONE
            connectionFailureView.visibility = View.GONE
        }

        private fun updateInputMethod(requestState: RequestState) {
            if (requestState == RequestState.CONNECTION_FAILURE) hideSoftInput() else showSoftInput()
        }
    }

    /**
     * Статус поискового запроса.
     * Каждый экземпляр сожержит значение visibility для сообщений-заглушек и RecyclerView:
     *  [ViewContainer.recyclerView] = [recyclerVisibility],
     *  [ViewContainer.nothingFoundView] = [nothingFoundViewVisibility],
     *  [ViewContainer.connectionFailureView] = [connectionFailureViewVisibility].
     *  [inputMethodState] определяет будет ли показана экранная клавиатура.
     */
    private enum class RequestState(
        val recyclerVisibility: Int,
        val nothingFoundViewVisibility: Int,
        val connectionFailureViewVisibility: Int,
        val inputMethodState: InputMethodState
    ) {
        FOUND(View.VISIBLE, View.GONE, View.GONE, InputMethodState.UP),
        NOTHING_FOUND(View.GONE, View.VISIBLE, View.GONE, InputMethodState.UP),
        CONNECTION_FAILURE(View.GONE, View.GONE, View.VISIBLE, InputMethodState.DOWN)
    }

    /**
     * Состояние SoftInputMethod.
     * [UP] - отображается на экране.
     * [DOWN] - не отображается на экране.
     */
    private enum class InputMethodState {
        UP,
        DOWN
    }
}