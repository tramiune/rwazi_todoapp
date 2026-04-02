package com.rwazi.app.todo.domain.usecase

import com.rwazi.app.todo.data.local.NoteEntity
import com.rwazi.app.todo.data.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNoteFlowUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(id: String): Flow<NoteEntity?> {
        return repository.getNoteFlow(id)
    }
}
