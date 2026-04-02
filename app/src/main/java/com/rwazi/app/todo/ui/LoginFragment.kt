package com.rwazi.app.todo.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.rwazi.app.todo.R
import com.rwazi.app.todo.base.BaseFragment
import com.rwazi.app.todo.databinding.FragmentLoginBinding
import com.rwazi.app.todo.util.ColorUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : com.rwazi.app.todo.base.BaseFragment<FragmentLoginBinding, AuthViewModel>(
    FragmentLoginBinding::inflate
) {
    override val viewModel: AuthViewModel by viewModels()

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            viewModel.signInWithGoogle(credential)
        } catch (e: ApiException) {
            Toast.makeText(requireContext(), "Google Sign In Failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun initControl(view: View, savedInstanceState: Bundle?) {
        if (viewModel.isLoggedIn()) {
            findNavController().navigate(R.id.action_LoginFragment_to_FirstFragment)
            return
        }
        binding.loginRoot.setBackgroundColor(com.rwazi.app.todo.util.ColorUtils.getRandomSoftColor())
        
        binding.btnGoogleSignIn.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val intent = GoogleSignIn.getClient(requireActivity(), gso).signInIntent
            googleSignInLauncher.launch(intent)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { result ->
                    when (result) {
                        is AuthViewModel.AuthResult.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.btnGoogleSignIn.isEnabled = false
                        }
                        is AuthViewModel.AuthResult.Success -> {
                            findNavController().navigate(R.id.action_LoginFragment_to_FirstFragment)
                        }
                        is AuthViewModel.AuthResult.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnGoogleSignIn.isEnabled = true
                            Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}
