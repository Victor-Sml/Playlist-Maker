package com.victor_sml.playlistmaker.search.ui.view

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.Constants.CLICK_DEBOUNCE_DELAY
import com.victor_sml.playlistmaker.common.Constants.TRACK_FOR_PLAYER
import com.victor_sml.playlistmaker.common.domain.models.Track
import com.victor_sml.playlistmaker.common.ui.BindingFragment
import com.victor_sml.playlistmaker.common.ui.recycler.adapters.RecyclerAdapter
import com.victor_sml.playlistmaker.common.utils.debounce
import com.victor_sml.playlistmaker.databinding.FragmentSearchBinding
import com.victor_sml.playlistmaker.main.ui.stateholder.SharedViewModel
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.ConnectionFailure
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.Empty
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.History
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.Loading
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.NothingFound
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchScreenState.SearchResult
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchViewModel
import com.victor_sml.playlistmaker.common.ui.recycler.delegates.ButtonDelegate.ClickListener
import com.victor_sml.playlistmaker.common.ui.recycler.delegates.ButtonDelegate
import com.victor_sml.playlistmaker.common.ui.recycler.delegates.HeaderDelegate
import com.victor_sml.playlistmaker.common.ui.recycler.delegates.MessageDelegate
import com.victor_sml.playlistmaker.common.ui.recycler.delegates.SpaceDelegate
import com.victor_sml.playlistmaker.common.ui.recycler.delegates.TrackDelegate.TrackClickListener
import com.victor_sml.playlistmaker.common.ui.recycler.delegates.TrackDelegate
import com.victor_sml.playlistmaker.common.utils.UtilsUi.doOnApplyWindowInsets
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BindingFragment<FragmentSearchBinding>() {
    private val viewModel by viewModel<SearchViewModel>()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var backPressedCallback: OnBackPressedCallback
    private var searchJob: Job? = null
    private var thisRestored: Boolean = false
    private lateinit var screenState: SearchScreenState
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private lateinit var onRecyclerButtonClickDebounce: (() -> Unit) -> Unit

    private lateinit var recyclerAdapter: RecyclerAdapter

    private val trackClickListener = object : TrackClickListener {
        override fun onTrackClick(track: Track) {
            onTrackClickDebounce(track)
        }
    }

    private val recyclerButtonClickListener = object : ClickListener {
        override fun onButtonClick(callback: () -> Unit) {
            onRecyclerButtonClickDebounce(callback)
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

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        thisRestored = viewModel.isFragmentDestroyed

        observeScreenState()
        initializeUI()
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
        viewModel.isFragmentDestroyed = true
    }

    private fun observeScreenState() {
        viewModel.getScreenState().observe(viewLifecycleOwner) { screenState ->
            this.screenState = screenState
            renderViews(screenState)

            backPressedCallback.isEnabled = screenState is SearchResult
        }
    }

    private fun renderViews(state: SearchScreenState) {
        updateRecycler(state)
        setViewVisibility(state)
    }

    private fun setViewVisibility(state: SearchScreenState) {
        when (state) {
            is Loading -> showProgressBar()
            is SearchResult, is History, is NothingFound, is ConnectionFailure -> showRecycler()
            is Empty -> clearScreen()
        }
    }

    private fun showProgressBar() {
        binding.rwTracks.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun clearScreen() {
        binding.rwTracks.isVisible = false
        binding.progressBar.isVisible = false
    }

    private fun initializeUI() {
        applyWindowInsets()
        initRecycler()
        setDebounces()
        setListeners()
    }

    private fun applyWindowInsets() {
        binding.llRoot.doOnApplyWindowInsets(left = true, top = true, right = true)
    }

    private fun initRecycler() {
        recyclerAdapter = RecyclerAdapter(
            arrayListOf(
                TrackDelegate(trackClickListener),
                ButtonDelegate(recyclerButtonClickListener),
                MessageDelegate(),
                HeaderDelegate(),
                SpaceDelegate()
            )
        )
        binding.rwTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.rwTracks.adapter = recyclerAdapter
    }

    private fun updateRecycler(state: SearchScreenState) {
        when (state) {
            is SearchResult,
            is History,
            is NothingFound,
            is ConnectionFailure,
            -> recyclerAdapter.update(state.items)

            is Empty, Loading -> recyclerAdapter.update()
        }
    }

    private fun showRecycler() {
        binding.progressBar.isVisible = false
        binding.rwTracks.isVisible = true
    }

    private fun setListeners() {
        binding.inputEditText.doOnTextChanged { text, _, _, _ ->
            binding.clearInput.isVisible = clearButtonVisibility(text)

            if (thisRestored) {
                thisRestored = false
                return@doOnTextChanged
            }
            processSearchRequestChange(text)
        }

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

    private fun setDebounces() {
        onTrackClickDebounce =
            debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope) { track ->
                sharedViewModel.passTrack(TRACK_FOR_PLAYER, track)
                viewModel.onTrackClicked(track)
                findNavController().navigate(R.id.action_global_player)
            }

        onRecyclerButtonClickDebounce =
            debounce(
                CLICK_DEBOUNCE_DELAY,
                viewLifecycleOwner.lifecycleScope
            ) { callback -> callback() }
    }

    private fun processSearchRequestChange(searchRequest: CharSequence?) {
        if (searchRequest.isNullOrEmpty()) {
            searchDebounce(false)
            viewModel.getHistory()
        } else {
            searchDebounce()
        }
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

    private fun clearButtonVisibility(s: CharSequence?) = !s.isNullOrEmpty()

    private fun showSoftInput() {
        binding.inputEditText.requestFocus()
        activity?.window?.setSoftInputMode(SOFT_INPUT_STATE_VISIBLE)
    }

    private fun hideSoftInput() {
        val view = activity?.currentFocus

        val inputMethodManager =
            activity?.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
        activity?.currentFocus?.clearFocus()
    }

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}