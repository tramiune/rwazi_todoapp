package com.rwazi.app.todo.ui.settings

import androidx.lifecycle.viewModelScope
import com.rwazi.app.todo.base.BaseViewModel
import com.rwazi.app.todo.data.local.DataStorageManager
import com.rwazi.app.todo.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStorageManager: DataStorageManager,
    private val authRepository: AuthRepository
) : BaseViewModel() {

    val themeFlow: StateFlow<Boolean?> = dataStorageManager.themeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun setThemeMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            dataStorageManager.setThemeMode(isDarkMode)
        }
    }

    suspend fun signOut() {
        authRepository.signOut()
    }
}