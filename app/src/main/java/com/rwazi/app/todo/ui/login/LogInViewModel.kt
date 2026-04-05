package com.rwazi.app.todo.ui.login

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.rwazi.app.todo.domain.model.User
import com.rwazi.app.todo.base.BaseViewModel
import com.rwazi.app.todo.domain.repository.AuthRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    sealed interface LoginEffect {
        data class Success(val user: User) : LoginEffect
        object LaunchGoogleSignIn : LoginEffect
    }

    private val _effect = MutableSharedFlow<LoginEffect>()
    val effect = _effect.asSharedFlow()

    fun isLoggedIn(): Boolean = authRepository.currentUser != null

    fun onGoogleSignInClicked() {
        showProgress(null)
        viewModelScope.launch {
            _effect.emit(LoginEffect.LaunchGoogleSignIn)
        }
    }

    fun onGoogleSignInCancelled() {
        hideProgress()
    }

    fun signInWithGoogle(idToken: String) {
        justExecute(
            action = {
                authRepository.signInWithGoogle(idToken).getOrThrow()
            },
            onSuccess = { user ->
                viewModelScope.launch { _effect.emit(LoginEffect.Success(user)) }
            },
            onError = {
                hideProgress()
            }
        )
    }
}