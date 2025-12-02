package dev.ktekik.sahaf.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReaderIdRepository(private val datastore: DataStore<Preferences>) {
    private val readerIdKey = stringPreferencesKey("reader_id")

    val readerId: Flow<String?> = datastore.data
        .map { prefs ->
            prefs[readerIdKey]
        }

    suspend fun saveId(id: String) {
        datastore.edit { preferences ->
            preferences[readerIdKey] = id
        }
    }
}