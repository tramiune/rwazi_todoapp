package com.rwazi.app.todo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rwazi.app.todo.data.local.NoteEntity
import com.rwazi.app.todo.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _sortOrder = MutableStateFlow(com.rwazi.app.todo.util.SortOrder.NEWEST_FIRST)
    val sortOrder: StateFlow<com.rwazi.app.todo.util.SortOrder> = _sortOrder

    @OptIn(ExperimentalCoroutinesApi::class)
    val notes: Flow<PagingData<NoteEntity>> = kotlinx.coroutines.flow.combine(
        _searchQuery,
        _sortOrder
    ) { query, sort ->
        query to sort
    }.flatMapLatest { (query, sort) ->
        repository.getNotesPaged(query, sort)
    }.cachedIn(viewModelScope)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun setSortOrder(order: com.rwazi.app.todo.util.SortOrder) {
        _sortOrder.value = order
    }

    fun addNote(title: String, content: String, backgroundColor: Int) {
        viewModelScope.launch {
            val note = NoteEntity(
                title = title,
                content = content,
                backgroundColor = backgroundColor
            )
            repository.addNote(note)
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }
}
