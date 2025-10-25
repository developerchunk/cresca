package com.developerstring.nexpay.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.map
import xyz.mcxross.kaptos.account.Account as AptosAccount

class AptosAccountRepository(
    private val encryptedDataStore: DataStore<Preferences>
) {

}

