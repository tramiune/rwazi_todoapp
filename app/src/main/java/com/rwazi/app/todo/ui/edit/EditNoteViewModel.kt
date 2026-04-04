package com.rwazi.app.todo.ui.edit

import androidx.lifecycle.viewModelScope
import com.rwazi.app.todo.base.BaseViewModel
import com.rwazi.app.todo.domain.repository.NoteRepository

import com.rwazi.app.todo.domain.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : BaseViewModel() {

    private val _effect = kotlinx.coroutines.flow.MutableSharedFlow<EditEffect>()
    val effect = _effect.asSharedFlow()

    sealed class EditEffect {
        object UpdateSuccess : EditEffect()
    }

    fun getNoteFlow(id: String): Flow<Note?> = noteRepository.getNoteFlow(id)

    fun updateNote(id: String, title: String, content: String) {
        if (title.isBlank() && content.isBlank()) return

        justExecute(
            action = { 
                val currentNote = noteRepository.getNoteById(id)
                currentNote?.let {
                    noteRepository.updateNote(it.copy(title = title, content = content))
                }
            },
            onSuccess = { 
                viewModelScope.launch {
                    _effect.emit(EditEffect.UpdateSuccess)
                }
            }
        )
    }
}