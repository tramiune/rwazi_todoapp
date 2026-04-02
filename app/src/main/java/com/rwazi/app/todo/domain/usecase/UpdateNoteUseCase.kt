package com.rwazi.app.todo.domain.usecase

import com.rwazi.app.todo.data.local.NoteEntity
import com.rwazi.app.todo.data.repository.NoteRepository
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: NoteEntity) {
        repository.updateNote(note)
    }
}
