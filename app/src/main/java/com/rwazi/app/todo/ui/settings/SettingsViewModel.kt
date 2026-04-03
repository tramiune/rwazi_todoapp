package com.rwazi.app.todo.ui.settings

import com.rwazi.app.todo.base.BaseViewModel
import com.rwazi.app.todo.data.repository.AuthRepository
import com.rwazi.app.todo.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val noteRepository: NoteRepository
) : BaseViewModel() {

    suspend fun signOut() {
        noteRepository.clearLocalData()
        authRepository.signOut()
    }
}