package com.rwazi.app.todo.base.type

sealed class ProgressType(val uuid: Int) {
    object NoProgress : ProgressType(0)
    object ProgressDialog : ProgressType(1)
    object SwipeRefresh : ProgressType(2)
}
