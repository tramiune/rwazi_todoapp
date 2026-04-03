package com.rwazi.app.todo.data.remote

import com.google.firebase.firestore.DocumentChange
import kotlinx.coroutines.flow.Flow

interface NoteRemoteDataSource {
    fun getNoteChanges(uid: String): Flow<List<Pair<DocumentChange.Type, NoteRemote>>>
    suspend fun saveNote(uid: String, note: NoteRemote)
    suspend fun updateNoteField(uid: String, noteId: String, field: String, value: Any)
}
