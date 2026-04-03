package com.rwazi.app.todo.di.auth

import android.content.Context
import com.rwazi.app.todo.R
import com.rwazi.app.todo.ui.login.GoogleAuthManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideGoogleAuthManager(
        @ApplicationContext context: Context
    ): GoogleAuthManager {
        val serverClientId = context.getString(R.string.default_web_client_id)
        return GoogleAuthManager(context, serverClientId)
    }
}
