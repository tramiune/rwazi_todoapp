package com.rwazi.app.todo.ui.settings

import com.rwazi.app.todo.base.BaseViewModel
import com.rwazi.app.todo.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    suspend fun signOut() {
        authRepository.signOut()
    }
}