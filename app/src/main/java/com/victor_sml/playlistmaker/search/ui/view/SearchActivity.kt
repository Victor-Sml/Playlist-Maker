package com.victor_sml.playlistmaker.search.ui.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.databinding.ActivitySearchBinding
import com.victor_sml.playlistmaker.player.ui.view.PlayerActivity
import com.victor_sml.playlistmaker.search.ui.view.recycler.TrackDelegate.TrackClickListener
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.Loading
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.SearchResult
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.History
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.NothingFound
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.ConnectionFailure
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.Empty
import com.victor_sml.playlistmaker.common.models.TrackUi
import com.victor_sml.playlistmaker.search.ui.view.SearchActivity.StateVisibility.LOADING
import com.victor_sml.playlistmaker.search.ui.view.SearchActivity.StateVisibility.SEARCH_RESULT
import com.victor_sml.playlistmaker.search.ui.view.SearchActivity.StateVisibility.HISTORY
import com.victor_sml.playlistmaker.search.ui.view.SearchActivity.StateVisibility.NOTHING_FOUND
import com.victor_sml.playlistmaker.search.ui.view.SearchActivity.StateVisibility.CONNECTION_FAILURE
import com.victor_sml.playlistmaker.search.ui.view.SearchActivity.StateVisibility.EMPTY
import com.victor_sml.playlistmaker.search.ui.view.recycler.ClearButtonDelegate.ClickListener
import com.victor_sml.playlistmaker.search.ui.view.recycler.RecyclerController
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchViewModel

const val TRACK_FOR_PLAYER = "track for player"

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var recyclerController: RecyclerController
    private var thisRestored: Boolean = false
    private lateinit var screenState: SearchScreenState
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable =
        Runnable { viewModel.searchTracks(binding.inputEditText.text.toString()) }

    private val trackClickListener = object : TrackClickListener {
        override fun onTrackClick(trackUi: TrackUi, context: Context) {
            if (clickDebounce()) {
                viewModel.addToHistory(trackUi)
                Intent(context, PlayerActivity::class.java)
                    .putExtra(TRACK_FOR_PLAYER, trackUi).let { context.startActivity(it) }
            }
        }
    }

    private val clearHistoryClickListener = object : ClickListener {
        override fun onButtonClick() {
            if (clickDebounce()) viewModel.clearHistory()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        thisRestored = savedInstanceState?.getBoolean(RESTORE_INSTANCE_STATE) ?: false

        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory(this)
        )[SearchViewModel::class.java]

        recyclerController =
            RecyclerController(binding.rwTracks, trackClickListener, clearHistoryClickListener)

        setListeners()

        viewModel.getScreenState().observe(this) { screenState ->
            this.screenState = screenState
            when (screenState) {
                is Loading -> renderViews(LOADING)

                is SearchResult -> renderViews(SEARCH_RESULT, screenState.tracks)

                is History -> renderViews(HISTORY, screenState.tracks)

                is NothingFound -> renderViews(NOTHING_FOUND)

                is ConnectionFailure -> renderViews(CONNECTION_FAILURE)

                is Empty -> renderViews(EMPTY)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showSoftInput()
    }

    override fun onStop() {
        super.onStop()
        if (screenState is History) viewModel.getHistory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(RESTORE_INSTANCE_STATE, true)
    }

    private fun renderViews(
        state: StateVisibility,
        tracks: List<TrackUi>? = null,
    ) {
        when (state) {
            SEARCH_RESULT -> tracks?.let { recyclerController.addTracks(tracks, false) }
            HISTORY -> tracks?.let { recyclerController.addTracks(tracks, true) }
            else -> Unit
        }
        setVisibility(state)
    }

    private fun setVisibility(state: StateVisibility) {
        binding.apply {
            progressBar.visibility = state.progressBar
            tvHistoryTitle.visibility = state.historyTitle
            rwTracks.visibility = state.recycler
        }
        findViewById<View>(R.id.nothing_found).visibility = state.nothingFound
        findViewById<View>(R.id.connection_failure).visibility = state.connectionFailure
    }

    private fun setListeners() {
        binding.inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearInput.visibility = clearButtonVisibility(s)

                if (thisRestored) {
                    thisRestored = false
                    return
                }

                if (s.isNullOrEmpty()) {
                    handler.removeCallbacks(searchRunnable)
                    viewModel.getHistory()
                } else {
                    searchDebounce(searchRunnable)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.clearInput.setOnClickListener {
            binding.inputEditText.setText("")
            hideSoftInput()
        }

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handler.removeCallbacks(searchRunnable)
                viewModel.searchTracks(binding.inputEditText.text.toString())
                hideSoftInput()
                true
            } else {
                false
            }
        }

        binding.inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && (view as EditText).text.isEmpty()) {
                viewModel.getHistory()
            }
        }

        binding.connectionFailure.btnRefresh.setOnClickListener {
            if (clickDebounce())
                viewModel.searchTracks(binding.inputEditText.text.toString())
        }

        binding.searchToolbar.setNavigationOnClickListener { finish() }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            GONE
        } else {
            VISIBLE
        }
    }

    private fun showSoftInput() {
        binding.inputEditText.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun hideSoftInput() {
        val view = this.currentFocus
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
        currentFocus?.clearFocus()
    }

    private fun searchDebounce(searchRunnable: Runnable) {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    enum class StateVisibility(
        val progressBar: Int,
        val historyTitle: Int,
        val recycler: Int,
        val nothingFound: Int,
        val connectionFailure: Int
    ) {
        LOADING(VISIBLE, GONE, GONE, GONE, GONE),
        SEARCH_RESULT(GONE, GONE, VISIBLE, GONE, GONE),
        HISTORY(GONE, VISIBLE, VISIBLE, GONE, GONE),
        NOTHING_FOUND(GONE, GONE, GONE, VISIBLE, GONE),
        CONNECTION_FAILURE(GONE, GONE, GONE, GONE, VISIBLE),
        EMPTY(GONE, GONE, GONE, GONE, GONE)
    }

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        private const val RESTORE_INSTANCE_STATE = "restore state"
    }
}