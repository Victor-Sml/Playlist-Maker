package com.victor_sml.playlistmaker.search.ui.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.databinding.FragmentSearchBinding
import com.victor_sml.playlistmaker.player.ui.view.PlayerActivity
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.ConnectionFailure
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.Empty
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.History
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.Loading
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.NothingFound
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.SearchResult
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchViewModel
import com.victor_sml.playlistmaker.search.ui.view.recycler.RecyclerController
import com.victor_sml.playlistmaker.search.ui.view.recycler.delegates.ButtonDelegate.ClickListener
import com.victor_sml.playlistmaker.search.ui.view.recycler.delegates.TrackDelegate.TrackClickListener
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

const val TRACK_FOR_PLAYER = "track for player"

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var backPressedCallback: OnBackPressedCallback
    private lateinit var screenState: SearchScreenState
    private val viewModel by viewModel<SearchViewModel>()
    private val handler: Handler by inject()
    private var thisRestored: Boolean = false
    private var isClickAllowed = true

    private val recyclerController: RecyclerController by inject {
        parametersOf(
            binding.rwTracks,
            trackClickListener,
            recyclerButtonClickListener
        )
    }

    private val searchRunnable =
        Runnable { viewModel.searchTracks(binding.inputEditText.text.toString()) }

    private val trackClickListener = object : TrackClickListener {
        override fun onTrackClick(track: Track, context: Context) {
            if (clickDebounce()) {
                viewModel.addToHistory(track)
                Intent(context, PlayerActivity::class.java)
                    .putExtra(TRACK_FOR_PLAYER, track).let { context.startActivity(it) }
            }
        }
    }

    private val recyclerButtonClickListener = object : ClickListener {
        override fun onButtonClick(callback: () -> Unit) {
            if (clickDebounce()) callback()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backPressedCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                viewModel.getHistory()
                isEnabled = false
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this, backPressedCallback
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        thisRestored = savedInstanceState?.getBoolean(RESTORE_INSTANCE_STATE) ?: false

        setListeners()

        viewModel.getScreenState().observe(viewLifecycleOwner) { screenState ->
            this.screenState = screenState
            renderViews(screenState)

            backPressedCallback.isEnabled = screenState is SearchResult
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(RESTORE_INSTANCE_STATE, true)
    }

    private fun renderViews(state: SearchScreenState) {
        updateRecycler(state)
        setViewVisibility(state)
    }

    private fun updateRecycler(state: SearchScreenState) {
        with(recyclerController) {
            when (state) {
                is SearchResult,
                is History,
                is NothingFound,
                is ConnectionFailure -> updateContent(state.items)
                is Empty, Loading -> clearContent()
            }
        }
    }

    private fun setViewVisibility(state: SearchScreenState) {
        when (state) {
            is Loading -> showProgressBar()
            is SearchResult, is History, is NothingFound, is ConnectionFailure -> showRecycler()
            is Empty -> clearScreen()
        }
    }

    private fun showRecycler() {
        binding.progressBar.isVisible = false
        binding.rwTracks.isVisible = true
    }

    private fun showProgressBar() {
        binding.rwTracks.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun clearScreen() {
        binding.rwTracks.isVisible = false
        binding.progressBar.isVisible = false
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
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun showSoftInput() {
        binding.inputEditText.requestFocus()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun hideSoftInput() {
        val view = activity?.currentFocus
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
        activity?.currentFocus?.clearFocus()
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

    companion object {
        private const val RESTORE_INSTANCE_STATE = "restore state"

        const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}