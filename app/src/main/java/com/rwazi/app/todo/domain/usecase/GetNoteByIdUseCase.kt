package com.rwazi.app.todo.domain.usecase

import com.rwazi.app.todo.data.local.NoteEntity
import com.rwazi.app.todo.data.repository.NoteRepository
import javax.inject.Inject

class GetNoteByIdUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: String): NoteEntity? {
        return repository.getNoteById(id)
    }
}
