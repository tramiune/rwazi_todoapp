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
        SELECT * FROM notes 
        WHERE isDeleted = 0 
        AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%')
        ORDER BY createdAt DESC
    """)
    fun searchNotesPagedDesc(query: String): PagingSource<Int, NoteEntity>

    @Query("""
        SELECT * FROM notes 
        WHERE isDeleted = 0 
        AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%')
        ORDER BY createdAt ASC
    """)
    fun searchNotesPagedAsc(query: String): PagingSource<Int, NoteEntity>

    @Query("SELECT * FROM notes WHERE syncStatus = 'PENDING' OR isDeleted = 1")
    suspend fun getNotesToSync(): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: String): NoteEntity?

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteFlow(id: String): Flow<NoteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("UPDATE notes SET isDeleted = 1, syncStatus = 'PENDING' WHERE id = :id")
    suspend fun softDeleteNote(id: String)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNotePermanently(id: String)
}
