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
import androidx.lifecycle.lifecycleScope
import com.victor_sml.playlistmaker.common.models.Track
import com.victor_sml.playlistmaker.common.utils.debounce
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

const val TRACK_FOR_PLAYER = "track for player"

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SearchViewModel>()
    private lateinit var backPressedCallback: OnBackPressedCallback
    private var searchJob: Job? = null
    private var thisRestored: Boolean = false
    private lateinit var screenState: SearchScreenState
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private lateinit var onRecyclerButtonClickDebounce: (() -> Unit) -> Unit

    private val trackClickListener = object : TrackClickListener {
        override fun onTrackClick(track: Track, context: Context) {
            onTrackClickDebounce(track)
        }
    }

    private val recyclerButtonClickListener = object : ClickListener {
        override fun onButtonClick(callback: () -> Unit) {
            onRecyclerButtonClickDebounce(callback)
        }
    }

    private val recyclerController: RecyclerController by inject {
        parametersOf(
            binding.rwTracks,
            trackClickListener,
            recyclerButtonClickListener
        )
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
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        thisRestored = savedInstanceState?.getBoolean(RESTORE_INSTANCE_STATE) ?: false

        onTrackClickDebounce =
            debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope) { track ->
                viewModel.addToHistory(track)
                Intent(context, PlayerActivity::class.java)
                    .putExtra(TRACK_FOR_PLAYER, track).let { context?.startActivity(it) }
            }

        onRecyclerButtonClickDebounce =
            debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope) { callback -> callback() }

        viewModel.getScreenState().observe(viewLifecycleOwner) { screenState ->
            this.screenState = screenState
            renderViews(screenState)

            backPressedCallback.isEnabled = screenState is SearchResult
        }

        setListeners()
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
                is ConnectionFailure,
                -> updateContent(state.items)
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
                binding.clearInput.isVisible = clearButtonVisibility(s)

                if (thisRestored) {
                    thisRestored = false
                    return
                }

                processSearchRequestChange(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.clearInput.setOnClickListener {
            binding.inputEditText.setText("")
            hideSoftInput()
        }

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchDebounce(false)
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

    private fun processSearchRequestChange(searchRequest: CharSequence?) {
        if (searchRequest.isNullOrEmpty()) {
            searchDebounce(false)
            viewModel.getHistory()
        } else {
            searchDebounce()
        }
    }

    private fun clearButtonVisibility(s: CharSequence?) = !s.isNullOrEmpty()

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

    private fun searchDebounce(isActive: Boolean = true) {
        searchJob?.cancel()
        searchJob = viewLifecycleOwner.lifecycleScope.launch {
            if (isActive) {
                delay(SEARCH_DEBOUNCE_DELAY)
                viewModel.searchTracks(binding.inputEditText.text.toString())
            }
        }
    }

    companion object {
        private const val RESTORE_INSTANCE_STATE = "restore state"

        const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}