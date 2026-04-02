package com.rwazi.app.todo.domain.usecase

import androidx.paging.PagingData
import com.rwazi.app.todo.data.local.NoteEntity
import com.rwazi.app.todo.data.repository.NoteRepository
import com.rwazi.app.todo.util.SortOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(query: String, sortOrder: SortOrder): Flow<PagingData<NoteEntity>> {
        return repository.getNotesPaged(query, sortOrder)
    }
}
