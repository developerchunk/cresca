package com.developerstring.nexpay.data.data_store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import okio.Path.Companion.toPath
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

internal const val encryptedDataStoreFileName = "encrypted_app_lock.preferences_pb"

actual fun createEncryptedPreferencesDataStore(): DataStore<Preferences> {
    return object : KoinComponent {
        val context: Context by inject()
    }.run {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val file = File(context.filesDir, encryptedDataStoreFileName)

        PreferenceDataStoreFactory.createWithPath {
            file.absolutePath.toPath()
        }
    }
}