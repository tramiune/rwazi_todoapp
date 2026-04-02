package com.rwazi.app.todo.ui.viewmodel

import com.rwazi.app.todo.base.BaseViewModel
import com.rwazi.app.todo.data.local.DataStorageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStorageManager: DataStorageManager
) : BaseViewModel() {

    val themeFlow: Flow<Boolean?> = dataStorageManager.themeFlow
}
