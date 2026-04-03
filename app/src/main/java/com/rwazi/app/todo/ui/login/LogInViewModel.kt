package com.rwazi.app.todo.ui.login

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.rwazi.app.todo.base.BaseViewModel
import com.rwazi.app.todo.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    data class LoginUiState(
        val isLoading: Boolean = false
    )

    sealed interface LoginEffect {
        data class Success(val user: FirebaseUser) : LoginEffect
        data class Error(val message: String) : LoginEffect
    }

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _effect = MutableSharedFlow<LoginEffect>()
    val effect = _effect.asSharedFlow()

    fun isLoggedIn(): Boolean = authRepository.currentUser != null

    fun signInWithGoogle(credential: AuthCredential) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            authRepository.signInWithCredential(credential)
                .onSuccess {
                    _uiState.update { state -> state.copy(isLoading = false) }
                    _effect.emit(LoginEffect.Success(it))
                }
                .onFailure {
                    _uiState.update { state -> state.copy(isLoading = false) }
                    _effect.emit(LoginEffect.Error(it.message ?: "SignIn Failed"))
                }
        }
    }
}