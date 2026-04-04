package com.rwazi.app.todo.base.extension

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import timber.log.Timber

fun DialogFragment.showIfNotExists(fm: FragmentManager) {
    val tag = this::class.java.simpleName

    val existing = fm.findFragmentByTag(tag)
    if (existing == null || !existing.isAdded) {
        if (!fm.isStateSaved) {
            try {
                this.show(fm, tag)
            } catch (e: IllegalStateException) {
                try {
                    val ft: FragmentTransaction = fm.beginTransaction()
                    ft.add(this, tag)
                    ft.commitAllowingStateLoss()
                } catch (e2: Exception) {
                    Timber.e(e2, "Cannot show dialog: ${e2.message}")
                }
            }
        } else {
            Timber.w("Cannot show dialog: FragmentManager state already saved")
        }
    }
}

fun FragmentManager.dismissIfExist(tag: String) {
    val fragment = findFragmentByTag(tag)
    if (fragment is DialogFragment && fragment.dialog?.isShowing == true) {
        fragment.dismissAllowingStateLoss()
    }
}
