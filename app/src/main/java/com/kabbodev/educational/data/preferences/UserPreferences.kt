package com.kabbodev.educational.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(context: Context) {

    private val applicationContext = context.applicationContext
    private val dataStore: DataStore<Preferences> = applicationContext.createDataStore(
        name = "user_data_store"
    )

    val selectedChapters: Flow<Set<String>?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_SELECTED_CHAPTERS]
        }

    suspend fun saveSelectedChapters(selectedChapters: Set<String>?) {
        dataStore.edit { mutablePreferences ->
            selectedChapters?.let {
                mutablePreferences[KEY_SELECTED_CHAPTERS] = it
            }
        }
    }

    suspend fun clear() {
        dataStore.edit { mutablePreferences ->
            mutablePreferences.clear()
        }
    }

    companion object {
        private val KEY_SELECTED_CHAPTERS = stringSetPreferencesKey("selected_chapters")
    }

}