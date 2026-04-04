package com.rwazi.app.todo.ui.settings

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rwazi.app.todo.R
import com.rwazi.app.todo.base.BaseFragment
import com.rwazi.app.todo.base.extension.click
import com.rwazi.app.todo.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

import androidx.recyclerview.widget.GridLayoutManager
import com.rwazi.app.todo.base.extension.collectInStarted
import com.rwazi.app.todo.ui.settings.adapter.ThemePaletteAdapter

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding, SettingsViewModel>(
    FragmentSettingsBinding::inflate
) {
    override val classTypeOfViewModel: Class<SettingsViewModel>
        get() = SettingsViewModel::class.java

    private val paletteAdapter by lazy {
        ThemePaletteAdapter { palette ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.updateSelectedTheme(palette.themeResId)
                // Wait for the change to persist before recreating
                activity?.recreate()
            }
        }
    }

    override fun initControl(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
    }

    override fun observer() {
        viewModel.isAutoTheme.collectInStarted(this) { isAuto ->
            binding.switchAutoTheme.isChecked = isAuto
            binding.rvPalettes.visibility = if (isAuto) View.GONE else View.VISIBLE
        }

        viewModel.selectedThemeResId.collectInStarted(this) { resId ->
            paletteAdapter.setSelectedTheme(resId)
        }

        paletteAdapter.submitList(viewModel.themePalettes)
    }

    override fun setupClick() {
        binding.btnBack.click {
            findNavController().popBackStack()
        }

        binding.switchAutoTheme.setOnCheckedChangeListener { view, isChecked ->
            if (view.isPressed) { // Only handle manual user touch
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.updateAutoTheme(isChecked)
                    if (isChecked) {
                        // Apply a random theme immediately when switching to Auto
                        activity?.recreate()
                    }
                }
            }
        }

        binding.btnLogout.click {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.signOut()
                findNavController().navigate(R.id.LoginFragment)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvPalettes.apply {
            adapter = paletteAdapter
            layoutManager = GridLayoutManager(requireContext(), 5)
        }
    }
}