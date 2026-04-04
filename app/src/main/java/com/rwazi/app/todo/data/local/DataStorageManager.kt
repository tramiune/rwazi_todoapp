package com.rwazi.app.todo.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "todo_settings")

@Singleton
class DataStorageManager @Inject constructor(private val context: Context) {

    private object PreferencesKeys {
        val IS_AUTO_THEME = booleanPreferencesKey("is_auto_theme")
        val SELECTED_THEME_RES_ID = intPreferencesKey("selected_theme_res_id")
    }

    val isAutoTheme: Flow<Boolean> = context.dataStore.data
        .catch { exception: Throwable ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences: Preferences ->
            preferences[PreferencesKeys.IS_AUTO_THEME] ?: true
        }

    val selectedThemeResId: Flow<Int> = context.dataStore.data
        .catch { exception: Throwable ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences: Preferences ->
            preferences[PreferencesKeys.SELECTED_THEME_RES_ID] ?: 0
        }

    suspend fun updateAutoTheme(isAuto: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_AUTO_THEME] = isAuto
        }
    }

    suspend fun updateSelectedTheme(themeResId: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_THEME_RES_ID] = themeResId
        }
    }
}
