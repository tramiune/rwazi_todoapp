package com.rwazi.app.todo.base.manager

import android.content.Context
import com.google.android.gms.common.api.ApiException
import com.rwazi.app.todo.R
import com.rwazi.app.todo.base.extension.isConnected
import com.rwazi.app.todo.base.type.ViewState
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.security.cert.CertificateException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException

class AppErrorManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : IErrorManager {

    companion object {
        const val ERROR_CODE_OFFLINE = "offline"
        const val ERROR_CODE_TIMEOUT = "timeout"
    }

    override fun handleError(throwable: Throwable): ViewState.Error {
        when (throwable) {
            is UnknownHostException,
            is ConnectException,
            is SocketTimeoutException,
            is SSLHandshakeException,
            is CertificateException,
                -> {
                return if (context.isConnected()) {
                    ViewState.Error(
                        ERROR_CODE_TIMEOUT, context.getString(R.string.unknown_error)
                    )
                } else {
                    ViewState.Error(
                        ERROR_CODE_OFFLINE,
                        context.getString(R.string.check_your_internet_connection_and_try_again),
                        isOffline = true
                    )
                }
            }

            is ApiException -> {
                return ViewState.Error(error = throwable.message ?: "")
            }

            else -> {
                return ViewState.Error(error = context.getString(R.string.unknown_error))
            }
        }
    }

}

interface IErrorManager {
    fun handleError(throwable: Throwable): ViewState.Error
}