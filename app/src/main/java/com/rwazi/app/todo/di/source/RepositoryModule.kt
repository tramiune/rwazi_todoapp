package com.rwazi.app.todo.di.source

import com.rwazi.app.todo.data.repository.AuthRepository
import com.rwazi.app.todo.data.repository.AuthRepositoryImpl
import com.rwazi.app.todo.data.repository.NoteRepository
import com.rwazi.app.todo.data.repository.NoteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNoteRepository(
        noteRepositoryImpl: NoteRepositoryImpl
    ): NoteRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}
