package com.rwazi.app.todo.base.manager

import android.content.Context
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
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
        return when (throwable) {
            is UnknownHostException,
            is ConnectException,
            is SocketTimeoutException,
            is SSLHandshakeException,
            is CertificateException,
            is FirebaseNetworkException -> {
                if (context.isConnected()) {
                    ViewState.Error(ERROR_CODE_TIMEOUT, context.getString(R.string.unknown_error))
                } else {
                    ViewState.Error(
                        ERROR_CODE_OFFLINE,
                        context.getString(R.string.check_your_internet_connection_and_try_again),
                        isOffline = true
                    )
                }
            }

            is GetCredentialCancellationException -> {
                // User cancelled the flow, usually we don't show an error toast
                ViewState.Error(error = "", showError = false)
            }

            is GetCredentialException -> {
                ViewState.Error(error = context.getString(R.string.error_google_sign_in_failed))
            }

            is FirebaseAuthException -> {
                handleFirebaseAuthError(throwable)
            }

            is ApiException -> {
                ViewState.Error(error = throwable.message ?: context.getString(R.string.unknown_error))
            }

            else -> {
                ViewState.Error(error = context.getString(R.string.unknown_error))
            }
        }
    }

    private fun handleFirebaseAuthError(e: FirebaseAuthException): ViewState.Error {
        val messageRes = when (e) {
            is FirebaseAuthInvalidUserException -> R.string.error_user_not_found
            is FirebaseAuthInvalidCredentialsException -> R.string.error_wrong_password
            is FirebaseAuthUserCollisionException -> R.string.error_auth_failed
            is FirebaseAuthRecentLoginRequiredException -> R.string.error_auth_failed
            else -> {
                when (e.errorCode) {
                    "ERROR_WRONG_PASSWORD" -> R.string.error_wrong_password
                    "ERROR_USER_NOT_FOUND" -> R.string.error_user_not_found
                    "ERROR_TOO_MANY_REQUESTS" -> R.string.error_too_many_requests
                    else -> R.string.error_auth_failed
                }
            }
        }
        return ViewState.Error(error = context.getString(messageRes))
    }

}

interface IErrorManager {
    fun handleError(throwable: Throwable): ViewState.Error
}