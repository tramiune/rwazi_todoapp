package com.rwazi.app.todo.domain.repository

import androidx.paging.PagingData
import com.rwazi.app.todo.domain.model.Note
import com.rwazi.app.todo.domain.model.SortOrder
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotesPaged(query: String, sortOrder: SortOrder): Flow<PagingData<Note>>
    suspend fun addNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(id: String)
    suspend fun clearLocalData()
    suspend fun getNoteById(id: String): Note?
    fun getNoteFlow(id: String): Flow<Note?>
    suspend fun syncNotes()
}
