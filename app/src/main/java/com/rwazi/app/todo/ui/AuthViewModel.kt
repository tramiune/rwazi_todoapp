package com.rwazi.app.todo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.rwazi.app.todo.base.BaseViewModel
import com.rwazi.app.todo.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Initial)
    val authState: StateFlow<AuthResult> = _authState

    fun signInWithGoogle(credential: AuthCredential) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            authRepository.signInWithCredential(credential)
                .onSuccess {
                    _authState.value = AuthResult.Success(it)
                }
                .onFailure {
                    _authState.value = AuthResult.Error(it.message ?: "SignIn Failed")
                }
        }
    }

    sealed class AuthResult {
        object Initial : AuthResult()
        object Loading : AuthResult()
        data class Success(val user: FirebaseUser) : AuthResult()
        data class Error(val message: String) : AuthResult()
    }
}
