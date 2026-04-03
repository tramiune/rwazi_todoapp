package com.rwazi.app.todo.data.repository

import com.google.firebase.auth.AuthCredential
import com.rwazi.app.todo.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: User?
    fun getAuthState(): Flow<User?>
    suspend fun signInWithCredential(credential: AuthCredential): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signOut()
}
