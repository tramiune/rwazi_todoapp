package com.rwazi.app.todo.ui.login

import android.os.Bundle
import android.view.View
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
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
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LogInViewModel>(
    FragmentLoginBinding::inflate
) {
    override val classTypeOfViewModel: Class<LogInViewModel>
        get() = LogInViewModel::class.java

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
        val credentialManager = CredentialManager.create(requireContext())
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.default_web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = requireActivity(),
                )
                handleSignInResult(result.credential)
            } catch (e: GetCredentialException) {
                viewModel.handleError(e)
            }
        }
    }

    private fun handleSignInResult(credential: androidx.credentials.Credential) {
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val authCredential =
                GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            viewModel.signInWithGoogle(authCredential)
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