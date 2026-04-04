package com.rwazi.app.todo.ui.settings

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.rwazi.app.todo.R
import com.rwazi.app.todo.base.BaseFragment
import com.rwazi.app.todo.base.extension.click
import com.rwazi.app.todo.base.extension.collectInStarted
import com.rwazi.app.todo.databinding.FragmentSettingsBinding
import com.rwazi.app.todo.ui.settings.adapter.ThemePaletteAdapter
import dagger.hilt.android.AndroidEntryPoint

import com.rwazi.app.todo.base.extension.goneView
import com.rwazi.app.todo.base.extension.visibleView

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding, SettingsViewModel>(
    FragmentSettingsBinding::inflate
) {

    override val shouldObserveViewModelState: Boolean = false
    override val classTypeOfViewModel: Class<SettingsViewModel>
        get() = SettingsViewModel::class.java

    private val paletteAdapter by lazy {
        ThemePaletteAdapter { palette ->
            viewModel.updateSelectedTheme(palette.themeResId)
        }
    }

    override fun observer() {
        viewModel.isAutoTheme.collectInStarted(this) { isAuto ->
            binding.switchAutoTheme.isChecked = isAuto
            if (isAuto) {
                binding.rvPalettes.goneView()
            } else {
                binding.rvPalettes.visibleView()
            }
        }

        viewModel.themePalettes.collectInStarted(this) { palettes ->
            paletteAdapter.submitList(palettes)
        }

        viewModel.effect.collectInStarted(this) { effect ->
            when (effect) {
                is SettingsViewModel.SettingsEffect.SignedOut -> {
                    findNavController().navigate(R.id.LoginFragment)
                }
                is SettingsViewModel.SettingsEffect.ThemeChanged -> {
                    activity?.recreate()
                }
            }
        }
    }

    override fun initControl(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
    }

    override fun setupClick() {
        binding.topBar.setOnBackClickListener {
            findNavController().popBackStack()
        }

        binding.switchAutoTheme.setOnCheckedChangeListener { view, isChecked ->
            if (view.isPressed) { // Only handle manual user touch
                viewModel.updateAutoTheme(isChecked)
            }
        }

        binding.btnLogout.click {
            viewModel.signOut()
        }
    }

    private fun setupRecyclerView() {
        binding.rvPalettes.apply {
            adapter = paletteAdapter
            layoutManager = GridLayoutManager(requireContext(), 5)
        }
    }
}