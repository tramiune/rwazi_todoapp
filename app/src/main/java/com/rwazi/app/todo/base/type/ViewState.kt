package com.rwazi.app.todo.base.type

sealed class ViewState {
    object Idle : ViewState()
    data class Loading(val progress: ProgressType) : ViewState()
    data class Error(
        val code: String? = null,
        val error: String,
        var showError: Boolean = false,
        var isOffline: Boolean = false,
    ) : ViewState()
}
