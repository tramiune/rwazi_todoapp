package com.rwazi.app.todo.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rwazi.app.todo.R
import com.rwazi.app.todo.base.BaseFragment
import com.rwazi.app.todo.data.repository.AuthRepository
import com.rwazi.app.todo.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding, NoteViewModel>(
    FragmentSettingsBinding::inflate
) {
    override val viewModel: NoteViewModel by viewModels()

    @Inject
    lateinit var authRepository: AuthRepository

    override fun initControl(view: View, savedInstanceState: Bundle?) {
        val sharedPrefs = requireContext().getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
        
        val isDarkModeObj = sharedPrefs.getBoolean("dark_mode", false)
        val systemDarkMode = (requireContext().resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
        val currentMode = if (sharedPrefs.contains("dark_mode")) isDarkModeObj else systemDarkMode
        
        binding.switchDarkMode.isChecked = currentMode

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("dark_mode", isChecked).apply()
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
                authRepository.signOut()
                findNavController().navigate(R.id.LoginFragment)
            }
        }
    }
}
