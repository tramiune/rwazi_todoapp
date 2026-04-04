package com.rwazi.app.todo.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rwazi.app.todo.base.BaseViewModel
import com.rwazi.app.todo.domain.repository.NoteRepository

import com.rwazi.app.todo.domain.repository.AuthRepository
import com.rwazi.app.todo.domain.model.Note
import com.rwazi.app.todo.domain.model.SortOrder
import com.rwazi.app.todo.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val authRepository: AuthRepository
) : BaseViewModel() {

    val currentUser: Flow<User?> = authRepository.getAuthState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), authRepository.currentUser)

    private val _effect = kotlinx.coroutines.flow.MutableSharedFlow<HomeEffect>()
    val effect = _effect.asSharedFlow()

    sealed class HomeEffect {
        data class ShowToast(val messageResId: Int) : HomeEffect()
    }

    private val _searchQuery = MutableStateFlow("")
    private val _sortOrder = MutableStateFlow(SortOrder.NEWEST_FIRST)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val notes: Flow<PagingData<Note>> = combine(
        _searchQuery.debounce(300),
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
            onSuccess = { 
                viewModelScope.launch {
                    _effect.emit(HomeEffect.ShowToast(com.rwazi.app.todo.R.string.note_added))
                }
            }
        )
    }

    fun deleteNote(id: String) {
        justExecute(
            action = { noteRepository.deleteNote(id) },
            onSuccess = { 
                viewModelScope.launch {
                    _effect.emit(HomeEffect.ShowToast(com.rwazi.app.todo.R.string.note_deleted))
                }
            }
        )
    }
}