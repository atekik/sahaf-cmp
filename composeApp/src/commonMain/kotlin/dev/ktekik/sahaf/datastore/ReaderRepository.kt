package dev.ktekik.sahaf.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

data class ReaderIdZipcodePair(val id: String, val zipcode: String)

open class ReaderRepository(private val datastore: DataStore<Preferences>) {
    private val readerIdKey = stringPreferencesKey("reader_id")
    private val zipcodeKey = stringPreferencesKey("zipcode")

    open val readerId: Flow<String?> = datastore.data
        .map { prefs ->
            prefs[readerIdKey]
        }

    open val zipcode: Flow<String?> = datastore.data
        .map { prefs ->
            prefs[zipcodeKey]
        }

    open suspend fun savePair(pair: ReaderIdZipcodePair) {
        datastore.edit { preferences ->
            preferences[readerIdKey] = pair.id
            preferences[zipcodeKey] = pair.zipcode
        }
    }

    open fun getPair(): Flow<ReaderIdZipcodePair?> {
        return readerId.combine(zipcode) { id, zipcode ->
            if (id != null && zipcode != null) {
                ReaderIdZipcodePair(id, zipcode)
            } else {
                null
            }
        }
    }


}