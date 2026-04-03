package com.rwazi.app.todo.data.local

import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "notes_fts")
@Fts4(notIndexed = ["entityId"])
data class NoteFtsEntity(
    val entityId: String,
    val title: String,
    val content: String
)
