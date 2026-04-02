package com.rwazi.app.todo.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.rwazi.app.todo.base.BaseViewModel
import com.rwazi.app.todo.data.local.NoteEntity
import com.rwazi.app.todo.domain.usecase.GetNoteByIdUseCase
import com.rwazi.app.todo.domain.usecase.GetNoteFlowUseCase
import com.rwazi.app.todo.domain.usecase.UpdateNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val getNoteFlowUseCase: GetNoteFlowUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase
) : BaseViewModel() {

    fun getNoteFlow(id: String): Flow<NoteEntity?> = getNoteFlowUseCase(id)

    suspend fun getNoteById(id: String): NoteEntity? = getNoteByIdUseCase(id)

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            updateNoteUseCase(note)
        }
    }
}
