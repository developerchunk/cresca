package com.developerstring.nexpay.repository

import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import xyz.mcxross.kaptos.account.Account
import xyz.mcxross.kaptos.account.Ed25519Account
import xyz.mcxross.kaptos.core.crypto.Ed25519PrivateKey
import xyz.mcxross.kaptos.core.crypto.PrivateKey
import xyz.mcxross.kaptos.account.Account as AptosAccount

expect fun hashPin(pin: String): String

class
AppLockRepository(
    private val encryptedDataStore: DataStore<Preferences>
) {
    companion object {
        private val APP_LOCK_ENABLED_KEY = booleanPreferencesKey("app_lock_enabled")
        private val PIN_HASH_KEY = stringPreferencesKey("pin_hash")
        private val BIOMETRIC_ENABLED_KEY = booleanPreferencesKey("biometric_enabled")
        private val ACCOUNT_ADDRESS = stringPreferencesKey("aptos_account_address")
        private val HAS_ACCOUNT = booleanPreferencesKey("has_persistent_account")
        private val LAST_ID = intPreferencesKey("last_id")

    }

    suspend fun setPin(pin: String) {
        val hashedPin = hashPin(pin)
        encryptedDataStore.edit { prefs ->
            prefs[PIN_HASH_KEY] = hashedPin
            prefs[APP_LOCK_ENABLED_KEY] = true
        }
    }

    // TODO: Fix last id storage
    suspend fun setLastId(id: Int): Int {
        encryptedDataStore.edit { prefs ->
            prefs[LAST_ID] = id
        }
        return id
    }

    suspend fun getLastId(): Int {
        return encryptedDataStore.data.map { preferences ->
            preferences[LAST_ID] ?: 0
        }.first()
    }


    suspend fun validatePin(pin: String): Boolean {
        val hashedPin = hashPin(pin)
        val storedHash = encryptedDataStore.data.map { prefs ->
            prefs[PIN_HASH_KEY]
        }.first()
        return storedHash == hashedPin
    }

    suspend fun isPinSet(): Boolean {
        return encryptedDataStore.data.map { prefs ->
            prefs[PIN_HASH_KEY] != null
        }.first()
    }

    suspend fun removePin() {
        encryptedDataStore.edit { prefs ->
            prefs.remove(PIN_HASH_KEY)
            prefs[APP_LOCK_ENABLED_KEY] = false
            prefs[BIOMETRIC_ENABLED_KEY] = false
        }
    }

    suspend fun setAppLockEnabled(enabled: Boolean) {
        encryptedDataStore.edit { prefs ->
            prefs[APP_LOCK_ENABLED_KEY] = enabled
        }
    }

    fun isAppLockEnabled(): Flow<Boolean> = encryptedDataStore.data.map { prefs ->
        prefs[APP_LOCK_ENABLED_KEY] ?: false
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        encryptedDataStore.edit { prefs ->
            prefs[BIOMETRIC_ENABLED_KEY] = enabled
        }
    }

    fun isBiometricEnabled(): Flow<Boolean> = encryptedDataStore.data.map { prefs ->
        prefs[BIOMETRIC_ENABLED_KEY] ?: false
    }

    // Store a reference to the current account in memory
    private var persistentAccount: AptosAccount? = null

    suspend fun initializePersistentAccount(
        userPrivateKey: String?,
        privateKey: String = ""
    ): AptosAccount {
        return if (persistentAccount != null) {
            persistentAccount!!
        } else {
            val hasExistingAccount = hasStoredAccount()
            if (hasExistingAccount) {
                // 0x838cc962666ec87131c76954029edd8373c077343c3eb3798e0c9d5521479a56
                // 0x397a1ce0ddd72e5a09107f52ef50286084442f6b1ac68605fb452656c1a930fb

                val newAccount = Ed25519Account(
                    Ed25519PrivateKey(getWalletAddress()),
                )
                persistentAccount = newAccount
                newAccount
            } else {
                val account = if (userPrivateKey != null) {
                    Ed25519Account(
                        Ed25519PrivateKey(userPrivateKey)
                    )
                } else {
                    Ed25519Account.generate()
                }
                persistentAccount = account
                markAccountAsStored(account)
                setAccountAddress(account.privateKey.toString())
                account
            }
        }
    }

    suspend fun setAccountAddress(walletAddress: String) {
        encryptedDataStore.edit { prefs ->
            prefs[ACCOUNT_ADDRESS] = walletAddress
        }
    }

    suspend fun getWalletAddress(): String {
        return encryptedDataStore.data.map { preferences ->
            preferences[ACCOUNT_ADDRESS] ?: ""
        }.first()
    }

    suspend fun getPersistentAccount(): AptosAccount? {

        return persistentAccount ?: run {
            if (hasStoredAccount()) {
                initializePersistentAccount(userPrivateKey = null,privateKey = getWalletAddress())
            } else {
                null
            }
        }
    }

    private suspend fun markAccountAsStored(account: AptosAccount) {
        encryptedDataStore.edit { preferences ->
            preferences[HAS_ACCOUNT] = true
            preferences[ACCOUNT_ADDRESS] = account.accountAddress.toString()
        }
    }

    suspend fun hasStoredAccount(): Boolean {
        return encryptedDataStore.data.map { preferences ->
            preferences[HAS_ACCOUNT] ?: false
        }.first()
    }

    suspend fun clearAccount() {
        persistentAccount = null
        encryptedDataStore.edit { preferences ->
            preferences.remove(HAS_ACCOUNT)
            preferences.remove(ACCOUNT_ADDRESS)
        }
    }
}
