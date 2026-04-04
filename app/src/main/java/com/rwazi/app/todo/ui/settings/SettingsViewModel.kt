package com.rwazi.app.todo.ui.settings

import androidx.lifecycle.viewModelScope
import com.rwazi.app.todo.base.BaseViewModel
import com.rwazi.app.todo.data.local.DataStorageManager
import com.rwazi.app.todo.domain.repository.AuthRepository
import com.rwazi.app.todo.domain.repository.NoteRepository
import com.rwazi.app.todo.util.DynamicBackgroundUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStorageManager: DataStorageManager,
    private val themeUtils: DynamicBackgroundUtils,
    private val noteRepository: NoteRepository
) : BaseViewModel() {

    val isAutoTheme = dataStorageManager.isAutoTheme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val selectedThemeResId = dataStorageManager.selectedThemeResId.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val themePalettes = themeUtils.getPalettes()

    fun updateAutoTheme(isAuto: Boolean) {
        viewModelScope.launch {
            dataStorageManager.updateAutoTheme(isAuto)
        }
    }

    fun updateSelectedTheme(themeResId: Int) {
        viewModelScope.launch {
            dataStorageManager.updateSelectedTheme(themeResId)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            noteRepository.clearLocalData()
            authRepository.signOut()
        }
    }
}