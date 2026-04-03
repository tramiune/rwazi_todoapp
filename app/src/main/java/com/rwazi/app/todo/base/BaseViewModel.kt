package com.rwazi.app.todo.base


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rwazi.app.todo.base.manager.IErrorManager
import com.rwazi.app.todo.base.type.ProgressType
import com.rwazi.app.todo.base.type.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var appErrorManager: IErrorManager

    val viewStateFlow = MutableSharedFlow<ViewState>(
        extraBufferCapacity = 2, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    open fun showProgress(progressType: ProgressType?) {
        viewStateFlow.tryEmit(ViewState.Loading(progressType ?: ProgressType.NoProgress))
    }

    open fun hideProgress() {
        viewStateFlow.tryEmit(ViewState.Idle)
    }

    fun handleError(throwable: Throwable, shouldNotify: Boolean = true): ViewState.Error {
        hideProgress()
        val error = appErrorManager.handleError(throwable).apply { this.showError = shouldNotify }

        if (shouldNotify) {
            viewStateFlow.tryEmit(error)
        }
        return error
    }


    protected fun <T> justSubscribe(
        source: Flow<T>,
        progressType: ProgressType? = ProgressType.NoProgress,
        onLoading: ((Boolean) -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ): Flow<T> {
        return source
            .flowOn(Dispatchers.IO)
            .onStart {
                onLoading?.invoke(true)
                showProgress(progressType)
            }
            .catch { throwable ->
                Timber.e(throwable, "justSubscribeAIPhoto failed")
                handleError(throwable)
                onError?.invoke(throwable.message.toString())

            }
            .onCompletion {
                hideProgress()
                onLoading?.invoke(false)
            }
    }

    // this is for normal call API
    protected fun <T> justExecute(
        action: suspend () -> T,
        progressType: ProgressType? = ProgressType.NoProgress,
        onLoading: ((Boolean) -> Unit)? = null,
        onSuccess: (T) -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                onLoading?.invoke(true)
                showProgress(progressType)

                val result = withContext(Dispatchers.IO) {
                    action()
                }

                onSuccess(result)

            } catch (e: Throwable) {
                Timber.e(e, "justExecute failed")
                handleError(e)
                onError?.invoke(e.message.toString())
            } finally {
                hideProgress()
                onLoading?.invoke(false)
            }
        }
    }

}