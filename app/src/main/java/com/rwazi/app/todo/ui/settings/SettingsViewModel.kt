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
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStorageManager: DataStorageManager,
    private val themeUtils: DynamicBackgroundUtils,
    private val noteRepository: NoteRepository
) : BaseViewModel() {

    private val _effect = kotlinx.coroutines.flow.MutableSharedFlow<SettingsEffect>()
    val effect = _effect.asSharedFlow()

    sealed class SettingsEffect {
        object SignedOut : SettingsEffect()
        object ThemeChanged : SettingsEffect()
    }

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

    val themePalettes = selectedThemeResId.map { selectedId ->
        themeUtils.getPalettes().map { palette ->
            palette.copy(isSelected = palette.themeResId == selectedId)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = themeUtils.getPalettes()
    )

    fun updateAutoTheme(isAuto: Boolean) {
        viewModelScope.launch {
            dataStorageManager.updateAutoTheme(isAuto)
            if (isAuto) {
                _effect.emit(SettingsEffect.ThemeChanged)
            }
        }
    }

    fun updateSelectedTheme(themeResId: Int) {
        viewModelScope.launch {
            dataStorageManager.updateSelectedTheme(themeResId)
            _effect.emit(SettingsEffect.ThemeChanged)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            noteRepository.clearLocalData()
            authRepository.signOut()
            _effect.emit(SettingsEffect.SignedOut)
        }
    }
}