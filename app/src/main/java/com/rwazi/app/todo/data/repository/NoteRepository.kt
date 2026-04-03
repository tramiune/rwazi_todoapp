package com.rwazi.app.todo.data.repository

import androidx.paging.PagingData
import com.rwazi.app.todo.ui.model.Note
import com.rwazi.app.todo.util.SortOrder
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotesPaged(query: String, sortOrder: SortOrder): Flow<PagingData<Note>>
    suspend fun addNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(id: String)
    suspend fun getNoteById(id: String): Note?
    fun getNoteFlow(id: String): Flow<Note?>
    suspend fun syncNotes()
    suspend fun clearLocalData()
}
