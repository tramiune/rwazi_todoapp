package com.rwazi.app.todo.data.local

import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "notes_fts")
@Fts4(contentEntity = NoteEntity::class)
data class NoteFtsEntity(
    val title: String,
    val content: String
)
