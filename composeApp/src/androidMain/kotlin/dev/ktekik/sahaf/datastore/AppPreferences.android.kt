package dev.ktekik.sahaf.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

fun createDataStore(context: Context): DataStore<Preferences> {
    return createDataStore(
        producePath = {
            // Produces: /data/user/0/com.package/files/app_prefs.preferences_pb
            context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
        }
    )
}