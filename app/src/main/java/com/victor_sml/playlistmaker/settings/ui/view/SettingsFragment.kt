package com.victor_sml.playlistmaker.settings.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.victor_sml.playlistmaker.App
import com.victor_sml.playlistmaker.databinding.FragmentSettingsBinding
import com.victor_sml.playlistmaker.settings.domain.model.ThemeSettings
import com.victor_sml.playlistmaker.settings.ui.stateholders.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingFragment: Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.themeSwitcher.isChecked = viewModel.getThemeSettings().isDarkTheme
        setListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setListeners() {
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (activity?.applicationContext as App).switchTheme(checked)
            viewModel.updateThemeSetting(ThemeSettings(checked))
        }

        binding.shareListItem.setOnClickListener {
            viewModel.shareApp()
        }

        binding.supportListItem.setOnClickListener {
            viewModel.openSupport()
        }

        binding.agreementListItem.setOnClickListener {
            viewModel.openTerms()
        }
    }
}