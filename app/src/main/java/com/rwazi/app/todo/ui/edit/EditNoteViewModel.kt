package com.rwazi.app.todo.ui.edit

import com.rwazi.app.todo.base.BaseViewModel
import com.rwazi.app.todo.domain.repository.NoteRepository

import com.rwazi.app.todo.domain.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : BaseViewModel() {

    fun getNoteFlow(id: String): Flow<Note?> = noteRepository.getNoteFlow(id)

    suspend fun getNoteById(id: String): Note? = noteRepository.getNoteById(id)

    fun updateNote(note: Note) {
        justExecute(
            action = { noteRepository.updateNote(note) },
            onSuccess = { /* Data update handled via Flow */ }
        )
    }
}