package com.rwazi.app.todo.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rwazi.app.todo.base.BaseViewModel
import com.rwazi.app.todo.data.repository.NoteRepository
import com.rwazi.app.todo.domain.model.Note
import com.rwazi.app.todo.util.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : BaseViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _sortOrder = MutableStateFlow(SortOrder.NEWEST_FIRST)

    @OptIn(ExperimentalCoroutinesApi::class)
    val notes: Flow<PagingData<Note>> = combine(
        _searchQuery,
        _sortOrder
    ) { query, sort ->
        query to sort
    }.flatMapLatest { (query, sort) ->
        noteRepository.getNotesPaged(query, sort)
    }.cachedIn(viewModelScope)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun addNote(title: String, content: String, backgroundColor: Int) {
        val note = Note(
            id = UUID.randomUUID().toString(),
            title = title,
            content = content,
            backgroundColor = backgroundColor,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        justExecute(
            action = { noteRepository.addNote(note) },
            onSuccess = { /* Success auto-updates via Flow */ }
        )
    }

    fun deleteNote(id: String) {
        justExecute(
            action = { noteRepository.deleteNote(id) },
            onSuccess = { /* Success auto-updates via Flow */ }
        )
    }
}