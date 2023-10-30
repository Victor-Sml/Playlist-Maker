package com.victor_sml.playlistmaker.settings.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets.Type.systemBars
import androidx.core.graphics.Insets
import androidx.core.view.updateLayoutParams
import com.victor_sml.playlistmaker.App
import com.victor_sml.playlistmaker.common.ui.BindingFragment
import com.victor_sml.playlistmaker.common.utils.UtilsUi.doOnApplyWindowInsets
import com.victor_sml.playlistmaker.databinding.FragmentSettingsBinding
import com.victor_sml.playlistmaker.settings.domain.model.ThemeSettings
import com.victor_sml.playlistmaker.settings.ui.stateholders.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingFragment : BindingFragment<FragmentSettingsBinding>() {
    private val viewModel by viewModel<SettingsViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.themeSwitcher.isChecked = viewModel.getThemeSettings().isDarkTheme
        applyWindowInsets()
        setListeners()
    }

    private fun applyWindowInsets() {
        with(binding) {
            llRoot.doOnApplyWindowInsets(left = true, right = true) { view, insets ->
                view.setMarginTop(insets)
            }
        }
    }

    private fun View.setMarginTop(insets: Insets) {
        this.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            if (insets.top != 0) topMargin =
                requireActivity().window.decorView.rootWindowInsets.getInsets(systemBars()).top
        }
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