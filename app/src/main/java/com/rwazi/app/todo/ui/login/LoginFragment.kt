package com.rwazi.app.todo.ui.login

import android.os.Bundle
import android.view.View
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rwazi.app.todo.R
import com.rwazi.app.todo.base.BaseFragment
import com.rwazi.app.todo.base.extension.click
import com.rwazi.app.todo.base.extension.collectInStarted
import com.rwazi.app.todo.base.extension.goneView
import com.rwazi.app.todo.base.extension.visibleView
import com.rwazi.app.todo.base.type.ProgressType
import com.rwazi.app.todo.base.type.ViewState
import com.rwazi.app.todo.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

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
            startGoogleSignIn()
        }
    }

    private fun startGoogleSignIn() {
        lifecycleScope.launch {
            try {
                val idToken = googleAuthManager.signIn(requireActivity())
                if (idToken != null) {
                    viewModel.signInWithGoogle(idToken)
                }
            } catch (e: GetCredentialException) {
                viewModel.handleError(e)
            }
        }
    }

    override fun observer() {
        viewModel.effect.collectInStarted(this) { effect ->
            when (effect) {
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