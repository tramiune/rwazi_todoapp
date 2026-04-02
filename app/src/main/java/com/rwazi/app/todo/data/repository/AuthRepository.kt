package com.rwazi.app.todo.data.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: FirebaseUser?
    fun getAuthState(): Flow<FirebaseUser?>
    suspend fun signInWithCredential(credential: AuthCredential): Result<FirebaseUser>
    suspend fun signOut()
}
