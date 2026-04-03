package com.rwazi.app.todo.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllNotesPagedDesc(): PagingSource<Int, NoteEntity>

    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY createdAt ASC")
    fun getAllNotesPagedAsc(): PagingSource<Int, NoteEntity>

    @Query("""
        SELECT notes.* FROM notes
        JOIN notes_fts ON notes.id = notes_fts.entityId
        WHERE notes.isDeleted = 0 AND notes_fts MATCH :query || '*'
        ORDER BY notes.createdAt DESC
    """)
    fun searchNotesPagedDesc(query: String): PagingSource<Int, NoteEntity>

    @Query("""
        SELECT notes.* FROM notes
        JOIN notes_fts ON notes.id = notes_fts.entityId
        WHERE notes.isDeleted = 0 AND notes_fts MATCH :query || '*'
        ORDER BY notes.createdAt ASC
    """)
    fun searchNotesPagedAsc(query: String): PagingSource<Int, NoteEntity>

    @Query("SELECT * FROM notes WHERE syncStatus = 'PENDING' OR isDeleted = 1")
    suspend fun getNotesToSync(): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: String): NoteEntity?

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteFlow(id: String): Flow<NoteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteOnly(note: NoteEntity)

    @androidx.room.Transaction
    suspend fun insertNote(note: NoteEntity) {
        insertNoteOnly(note)
        // Update FTS table
        deleteNoteFts(note.id)
        if (!note.isDeleted) {
            insertNoteFts(NoteFtsEntity(note.id, note.title, note.content))
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotesOnly(notes: List<NoteEntity>)

    @androidx.room.Transaction
    suspend fun insertNotes(notes: List<NoteEntity>) {
        insertNotesOnly(notes)
        notes.forEach { note ->
            deleteNoteFts(note.id)
            if (!note.isDeleted) {
                insertNoteFts(NoteFtsEntity(note.id, note.title, note.content))
            }
        }
    }

    @Update
    suspend fun updateNoteOnly(note: NoteEntity)

    @androidx.room.Transaction
    suspend fun updateNote(note: NoteEntity) {
        updateNoteOnly(note)
        deleteNoteFts(note.id)
        if (!note.isDeleted) {
            insertNoteFts(NoteFtsEntity(note.id, note.title, note.content))
        }
    }

    @Query("UPDATE notes SET isDeleted = 1, syncStatus = 'PENDING' WHERE id = :id")
    suspend fun softDeleteNoteOnly(id: String)

    @androidx.room.Transaction
    suspend fun softDeleteNote(id: String) {
        softDeleteNoteOnly(id)
        deleteNoteFts(id)
    }

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNotePermanentlyOnly(id: String)

    @androidx.room.Transaction
    suspend fun deleteNotePermanently(id: String) {
        deleteNotePermanentlyOnly(id)
        deleteNoteFts(id)
    }

    @Query("DELETE FROM notes")
    suspend fun clearAllNotesOnly()

    @androidx.room.Transaction
    suspend fun clearAllNotes() {
        clearAllNotesOnly()
        clearAllNotesFts()
    }

    // ─── FTS Auxiliary Methods ──────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteFts(fts: NoteFtsEntity)

    @Query("DELETE FROM notes_fts WHERE entityId = :id")
    suspend fun deleteNoteFts(id: String)

    @Query("DELETE FROM notes_fts")
    suspend fun clearAllNotesFts()
}

