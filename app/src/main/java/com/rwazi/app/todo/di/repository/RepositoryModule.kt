package com.rwazi.app.todo.di.repository

import com.rwazi.app.todo.data.remote.NoteRemoteDataSource
import com.rwazi.app.todo.data.remote.NoteRemoteDataSourceImpl
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
    abstract fun bindNoteRemoteDataSource(
        impl: NoteRemoteDataSourceImpl
    ): NoteRemoteDataSource
}
