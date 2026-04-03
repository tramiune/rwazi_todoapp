package com.rwazi.app.todo.ui.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rwazi.app.todo.R
import com.rwazi.app.todo.base.BaseFragment
import com.rwazi.app.todo.base.type.ProgressType
import com.rwazi.app.todo.base.type.ViewState
import com.rwazi.app.todo.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding, SettingsViewModel>(
    FragmentSettingsBinding::inflate
) {
    override val classTypeOfViewModel: Class<SettingsViewModel>
        get() = SettingsViewModel::class.java


    override fun initControl(view: View, savedInstanceState: Bundle?) {
        observeTheme()
        setupListeners()
    }

    private fun observeTheme() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.themeFlow.collectLatest { isDarkMode ->
                val systemDarkMode = (requireContext().resources.configuration.uiMode and
                        Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

                val currentMode = isDarkMode ?: systemDarkMode
                binding.switchDarkMode.isChecked = currentMode
            }
        }
    }

    private fun setupListeners() {
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setThemeMode(isChecked)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnLogout.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.signOut()
                findNavController().navigate(R.id.LoginFragment)
            }
        }
    }

    override fun showProgressView(progress: ProgressType) {}
    override fun hideProgress(idle: ViewState.Idle) {}
    override fun displayError(error: ViewState.Error) {}
}