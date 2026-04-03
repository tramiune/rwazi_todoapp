package com.rwazi.app.todo.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.rwazi.app.todo.R
import com.rwazi.app.todo.base.BaseFragment
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
            findNavController().navigate(R.id.action_LoginFragment_to_FirstFragment)
            return
        }

        binding.btnGoogleSignIn.setOnClickListener {
            val credentialManager = CredentialManager.create(requireContext())

            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            lifecycleScope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = requireActivity(),
                    )

                    val credential = result.credential
                    if (credential is CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    ) {

                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val authCredential =
                            GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                        viewModel.signInWithGoogle(authCredential)
                    }
                } catch (e: GetCredentialException) {
                    viewModel.handleError(e)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect { effect ->
                    when (effect) {
                        is LogInViewModel.LoginEffect.Success -> {
                            findNavController().navigate(R.id.action_LoginFragment_to_FirstFragment)
                        }
                    }
                }
            }
        }
    }

    override fun showProgressView(progress: ProgressType) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnGoogleSignIn.isEnabled = false
    }
    
    override fun hideProgress(idle: ViewState.Idle) {
        binding.progressBar.visibility = View.GONE
        binding.btnGoogleSignIn.isEnabled = true
    }
    
    override fun displayError(error: ViewState.Error) {
        if (error.showError) {
            Toast.makeText(requireContext(), error.error, Toast.LENGTH_SHORT).show()
        }
    }
}