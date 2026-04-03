package com.rwazi.app.todo.data.mapper

import com.rwazi.app.todo.data.local.NoteEntity
import com.rwazi.app.todo.data.local.SyncStatus
import com.rwazi.app.todo.data.remote.NoteRemote
import com.rwazi.app.todo.ui.model.Note
import java.util.UUID

// ─── NoteEntity ↔ Domain ────────────────────────────────────────────────────

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
    backgroundColor = backgroundColor,
    isDeleted = isDeleted
)

fun Note.toEntity(syncStatus: SyncStatus = SyncStatus.PENDING): NoteEntity = NoteEntity(
    id = id.ifBlank { UUID.randomUUID().toString() },
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
    backgroundColor = backgroundColor,
    isDeleted = isDeleted,
    syncStatus = syncStatus
)

// ─── NoteEntity ↔ Remote ────────────────────────────────────────────────────

fun NoteEntity.toRemote(): NoteRemote = NoteRemote(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
    backgroundColor = backgroundColor,
    deleted = isDeleted
)

fun NoteRemote.toEntity(syncStatus: SyncStatus = SyncStatus.SYNCED): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
    backgroundColor = backgroundColor,
    isDeleted = deleted,
    syncStatus = syncStatus
)
