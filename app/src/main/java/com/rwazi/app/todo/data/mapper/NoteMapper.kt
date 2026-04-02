package com.rwazi.app.todo.data.mapper

import com.rwazi.app.todo.data.local.NoteEntity
import com.rwazi.app.todo.data.local.SyncStatus
import com.rwazi.app.todo.data.remote.NoteRemote

fun NoteEntity.toRemote(): NoteRemote {
    return NoteRemote(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        backgroundColor = backgroundColor,
        deleted = isDeleted
    )
}

fun NoteRemote.toEntity(syncStatus: SyncStatus = SyncStatus.SYNCED): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        backgroundColor = backgroundColor,
        isDeleted = deleted,
        syncStatus = syncStatus
    )
}
