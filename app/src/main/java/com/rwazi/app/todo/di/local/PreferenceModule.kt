package com.rwazi.app.todo.di.local

import android.content.Context
import com.rwazi.app.todo.data.local.DataStorageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {

    @Provides
    @Singleton
    fun provideDataStorageManager(@ApplicationContext context: Context): DataStorageManager {
        return DataStorageManager(context)
    }
}
