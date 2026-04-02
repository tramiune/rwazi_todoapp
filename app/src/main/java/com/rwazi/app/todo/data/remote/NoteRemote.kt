package com.rwazi.app.todo.data.remote

data class NoteRemote(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val backgroundColor: Int = 0,
    val deleted: Boolean = false
)
