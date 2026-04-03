package com.rwazi.app.todo.di

import com.rwazi.app.todo.base.manager.AppErrorManager
import com.rwazi.app.todo.base.manager.IErrorManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ErrorManagerModule {

    @Binds
    @Singleton
    abstract fun bindErrorManager(
        appErrorManager: AppErrorManager
    ): IErrorManager
}
