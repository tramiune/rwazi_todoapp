package com.rwazi.app.todo.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rwazi.app.todo.base.BaseViewModel
import com.rwazi.app.todo.data.local.NoteEntity
import com.rwazi.app.todo.domain.usecase.AddNoteUseCase
import com.rwazi.app.todo.domain.usecase.DeleteNoteUseCase
import com.rwazi.app.todo.domain.usecase.GetNotesUseCase
import com.rwazi.app.todo.util.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : BaseViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _sortOrder = MutableStateFlow(SortOrder.NEWEST_FIRST)

    @OptIn(ExperimentalCoroutinesApi::class)
    val notes: Flow<PagingData<NoteEntity>> = combine(
        _searchQuery,
        _sortOrder
    ) { query, sort ->
        query to sort
    }.flatMapLatest { (query, sort) ->
        getNotesUseCase(query, sort)
    }.cachedIn(viewModelScope)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun addNote(title: String, content: String, backgroundColor: Int) {
        val note = NoteEntity(
            title = title,
            content = content,
            backgroundColor = backgroundColor
        )
        justExecute(
            action = { addNoteUseCase(note) },
            onSuccess = { /* Success auto-updates via Flow */ }
        )
    }

    fun deleteNote(id: String) {
        justExecute(
            action = { deleteNoteUseCase(id) },
            onSuccess = { /* Success auto-updates via Flow */ }
        )
    }
}