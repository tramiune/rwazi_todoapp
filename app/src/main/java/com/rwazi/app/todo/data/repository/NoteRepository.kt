package com.rwazi.app.todo.data.repository

import androidx.paging.PagingData
import com.rwazi.app.todo.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotesPaged(query: String, sortOrder: com.rwazi.app.todo.util.SortOrder): Flow<PagingData<NoteEntity>>
    suspend fun addNote(note: NoteEntity)
    suspend fun updateNote(note: NoteEntity)
    suspend fun deleteNote(id: String)
    suspend fun syncNotes()
}
