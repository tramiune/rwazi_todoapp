package com.rwazi.app.todo.di

import com.rwazi.app.todo.util.DynamicBackgroundUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ThemeModule {

    @Provides
    @Singleton
    fun provideDynamicBackgroundUtils(): DynamicBackgroundUtils {
        return DynamicBackgroundUtils()
    }
}
