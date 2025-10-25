package com.developerstring.nexpay.data.data_store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect fun createEncryptedPreferencesDataStore(): DataStore<Preferences>
