package com.rwazi.app.todo.domain.model

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val backgroundColor: Int,
    val isDeleted: Boolean = false
)
