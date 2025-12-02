package dev.ktekik.sahaf.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

fun createDataStore(): DataStore<Preferences> {
    return createDataStore(
        producePath = {
//            val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
//                directory = NSDocumentDirectory,
//                inDomain = NSUserDomainMask,
//                appropriateForURL = null,
//                create = false,
//                error = null,
//            )
//            requireNotNull(documentDirectory).path +
           "/$DATA_STORE_FILE_NAME"
        }
    )
}