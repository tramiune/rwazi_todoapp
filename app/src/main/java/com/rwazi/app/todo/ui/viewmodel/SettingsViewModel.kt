package com.rwazi.app.todo.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.rwazi.app.todo.base.BaseViewModel
import com.rwazi.app.todo.data.local.DataStorageManager
import com.rwazi.app.todo.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStorageManager: DataStorageManager,
    private val authRepository: AuthRepository
) : BaseViewModel() {

    val themeFlow: Flow<Boolean?> = dataStorageManager.themeFlow

    fun setThemeMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            dataStorageManager.setThemeMode(isDarkMode)
        }
    }

    suspend fun signOut() {
        authRepository.signOut()
    }
}
