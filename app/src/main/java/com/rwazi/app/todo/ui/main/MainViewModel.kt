package com.rwazi.app.todo.ui.main

import com.rwazi.app.todo.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class MainViewModel @Inject constructor(
) : BaseViewModel() {

    private val _isLightMode = MutableStateFlow(false)
    val isLightMode: StateFlow<Boolean> = _isLightMode.asStateFlow()

    fun setLightMode(isLight: Boolean) {
        _isLightMode.value = isLight
    }
}