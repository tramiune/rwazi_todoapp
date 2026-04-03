package com.rwazi.app.todo.base.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.let

fun <T> Flow<T>.collectInStarted(
    scope: CoroutineScope,
    lifecycle: Lifecycle,
    collector: suspend (T) -> Unit
) {
    scope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@collectInStarted.collect { collector(it) }
        }
    }
}

fun <T> Flow<T>.collectInStarted(
    fragment: Fragment,
    collector: suspend (T) -> Unit
) {
    fragment.viewLifecycleOwner.lifecycleScope.launch {
        fragment.viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@collectInStarted.collect { collector(it) }
        }
    }
}

fun <T> Flow<T>.collectInStarted(
    owner: LifecycleOwner,
    collector: suspend (T) -> Unit
) {
    owner.lifecycleScope.launch {
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@collectInStarted.collect { collector(it) }
        }
    }
}

fun <T> Flow<T>.collectOnce(
    fragment: Fragment,
    collector: suspend (T) -> Unit
) {
    fragment.viewLifecycleOwner.lifecycleScope.launch {
        this@collectOnce.firstOrNull()?.let { collector(it) }
    }
}


