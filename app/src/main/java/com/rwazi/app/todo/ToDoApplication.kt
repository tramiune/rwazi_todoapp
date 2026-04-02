package com.rwazi.app.todo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ToDoApplication : Application(), androidx.work.Configuration.Provider {

    @javax.inject.Inject
    lateinit var workerFactory: androidx.hilt.work.HiltWorkerFactory

    override val workManagerConfiguration: androidx.work.Configuration
        get() = androidx.work.Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        
        val sharedPrefs = getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
        if (sharedPrefs.contains("dark_mode")) {
            val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)
            if (isDarkMode) {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

}