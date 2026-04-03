package com.rwazi.app.todo.base.extension

import android.os.SystemClock
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible

private var timeDelayDefault = 300L
private var lastClickTime: Long = 0

fun View.click(timeDelay: Long = timeDelayDefault, action: (view: View?) -> Unit) {
    this.setOnClickListener { view ->
        onNextEventActionAfterTimeDelay(timeDelay, view, action = {
            action.invoke(view)
        })
    }
}

private fun onNextEventActionAfterTimeDelay(
    timeDelay: Long,
    view: View,
    action: (view: View) -> Unit
) {
    if (SystemClock.elapsedRealtime() - lastClickTime < timeDelay) return
    else action.invoke(view)
    lastClickTime = SystemClock.elapsedRealtime()
}

fun View.invisibleView() {
    if (isVisible) visibility = View.INVISIBLE
}

fun View.goneView() {
    if (isVisible || isInvisible) visibility = View.GONE
}

fun View.visibleView() {
    if (isInvisible || isGone) visibility = View.VISIBLE
}