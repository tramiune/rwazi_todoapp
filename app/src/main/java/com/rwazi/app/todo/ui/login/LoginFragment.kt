package com.rwazi.app.todo.ui.login

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rwazi.app.todo.base.BaseFragment
import com.rwazi.app.todo.base.extension.click
import com.rwazi.app.todo.base.extension.collectInStarted
import com.rwazi.app.todo.base.extension.goneView
import com.rwazi.app.todo.base.extension.visibleView
import com.rwazi.app.todo.base.type.ProgressType
import com.rwazi.app.todo.base.type.ViewState
import com.rwazi.app.todo.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LogInViewModel>(
    FragmentLoginBinding::inflate
) {
    override val classTypeOfViewModel: Class<LogInViewModel>
        get() = LogInViewModel::class.java

    @Inject
    lateinit var googleAuthManager: GoogleAuthManager

    override fun initControl(view: View, savedInstanceState: Bundle?) {
        if (viewModel.isLoggedIn()) {
            val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
            findNavController().navigate(action)
            return
        }
    }

    override fun setupClick() {
        binding.btnGoogleSignIn.click {
            viewModel.onGoogleSignInClicked()
        }
    }

    private fun startGoogleSignIn() {
        lifecycleScope.launch {
            googleAuthManager.getIdToken(requireActivity())
                .onSuccess { idToken ->
                    if (idToken != null) {
                        viewModel.signInWithGoogle(idToken)
                    } else {
                        viewModel.onGoogleSignInCancelled()
                    }
                }
                .onFailure { error ->
                    viewModel.handleError(error)
                    viewModel.onGoogleSignInCancelled()
                }
        }
    }

    override fun observer() {
        observeLogin()
    }

    private fun observeLogin() {
        viewModel.effect.collectInStarted(this) { effect ->
            when (effect) {
                is LogInViewModel.LoginEffect.LaunchGoogleSignIn -> {
                    startGoogleSignIn()
                }

                is LogInViewModel.LoginEffect.Success -> {
                    val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                    findNavController().navigate(action)
                }
            }
        }
    }

    override fun showProgressView(progress: ProgressType) {
        binding.progressBar.visibleView()
        binding.btnGoogleSignIn.isEnabled = false
    }

    override fun hideProgress(idle: ViewState.Idle) {
        binding.progressBar.goneView()
        binding.btnGoogleSignIn.isEnabled = true
    }
}