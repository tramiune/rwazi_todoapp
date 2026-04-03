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

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding, SettingsViewModel>(
    FragmentSettingsBinding::inflate
) {
    override val classTypeOfViewModel: Class<SettingsViewModel>
        get() = SettingsViewModel::class.java

    override fun initControl(view: View, savedInstanceState: Bundle?) {}

    override fun setupClick() {
        binding.btnBack.click {
            findNavController().popBackStack()
        }

        binding.btnLogout.click {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.signOut()
                findNavController().navigate(R.id.LoginFragment)
            }
        }
    }
}