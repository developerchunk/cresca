package com.developerstring.nexpay.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerstring.nexpay.authentication.BiometricAuthResult
import com.developerstring.nexpay.authentication.BiometricAuthenticator
import com.developerstring.nexpay.data.room_db.dao.ChatDao
import com.developerstring.nexpay.data.room_db.dao.TransactionDao
import com.developerstring.nexpay.data.room_db.model.Transaction
import com.developerstring.nexpay.data.room_db.model.TransactionStatus
import com.developerstring.nexpay.repository.AppLockRepository
import kotlinx.coroutines.launch
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import xyz.mcxross.kaptos.Aptos
import xyz.mcxross.kaptos.account.Account
import xyz.mcxross.kaptos.model.AccountAddress
import xyz.mcxross.kaptos.model.AptosConfig
import xyz.mcxross.kaptos.model.AptosSettings
import xyz.mcxross.kaptos.model.Network
import kotlin.toString
import xyz.mcxross.kaptos.account.Account as AptosAccount
import com.developerstring.nexpay.data.model.CryptoBundle

data class AppLockState(
    val isAppLockEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val isBiometricAvailable: Boolean = false,
    val isSettingUp: Boolean = false,
    val isAuthenticating: Boolean = false,
    val authenticationError: String? = null,
    val isPinValid: Boolean = true
)

class SharedViewModel (
    private val chatDao: ChatDao,
    private val transactionDao: TransactionDao,
    private val dataStore: DataStore<Preferences>,
    private val appLockRepository: AppLockRepository,
    private val biometricAuthenticator: BiometricAuthenticator
) : ViewModel() {

    // App Lock State
    private val _appLockState = MutableStateFlow(AppLockState())
    val appLockState: StateFlow<AppLockState> = _appLockState.asStateFlow()

    private val _pinInput = MutableStateFlow("")
    val pinInput: StateFlow<String> = _pinInput.asStateFlow()

    private val _isAppLocked = MutableStateFlow(true)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    // Crypto Bundle State
    private val _selectedBundle = MutableStateFlow<CryptoBundle?>(null)
    val selectedBundle: StateFlow<CryptoBundle?> = _selectedBundle.asStateFlow()

    // NFC Receiver Wallet Address State
    private val _receiverWalletAddress = MutableStateFlow("")
    val receiverWalletAddress: StateFlow<String> = _receiverWalletAddress.asStateFlow()

    fun setSelectedBundle(bundle: CryptoBundle) {
        _selectedBundle.value = bundle
    }

    fun setReceiverWalletAddress(address: String) {
        _receiverWalletAddress.value = address
    }

    fun clearReceiverWalletAddress() {
        _receiverWalletAddress.value = ""
    }

    init {
        // Initialize app lock status - app starts locked by default if app lock is enabled
        viewModelScope.launch {
            appLockRepository.isAppLockEnabled().collect { enabled ->
                _appLockState.value = _appLockState.value.copy(isAppLockEnabled = enabled)
                // If app lock is enabled, keep app locked (true), otherwise unlock (false)
                if (enabled) {
                    _isAppLocked.value = true
                } else {
                    _isAppLocked.value = false
                }
            }
        }

        viewModelScope.launch {
            appLockRepository.isBiometricEnabled().collect { enabled ->
                _appLockState.value = _appLockState.value.copy(isBiometricEnabled = enabled)
            }
        }

        viewModelScope.launch {
            val isBiometricAvailable = biometricAuthenticator.isBiometricAvailable()
            _appLockState.value = _appLockState.value.copy(isBiometricAvailable = isBiometricAvailable)
        }
    }

    fun insertChat(chatName: String) {
        val chat = com.developerstring.nexpay.data.room_db.model.Chat(
            chatName = chatName
        )
        viewModelScope.launch {
            chatDao.insertChat(chat)
        }
    }

    fun getChats() = chatDao.getChats()


    // onboarding and user info handling using DataStore
    companion object {
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val COUNTRY_NAME_KEY = stringPreferencesKey("country_name")
        private val ONBOARDING_DONE_KEY = booleanPreferencesKey("onboarding_done")
    }

    fun setUserName(name: String) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[USER_NAME_KEY] = name
            }
        }
    }

    fun getUserName(): Flow<String?> = dataStore.data.map { prefs -> prefs[USER_NAME_KEY] }

    fun setCountryName(name: String) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[COUNTRY_NAME_KEY] = name
            }
        }
    }

    fun getCountryName(): Flow<String?> = dataStore.data.map { prefs -> prefs[COUNTRY_NAME_KEY] }

    fun setOnboardingDone(done: Boolean) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[ONBOARDING_DONE_KEY] = done
            }
        }
    }

    fun isOnboardingDone(): Flow<Boolean> = dataStore.data.map { prefs -> prefs[ONBOARDING_DONE_KEY] ?: false }

    // App Lock Methods
    fun addPinDigit(digit: String) {
        if (_pinInput.value.length < 6) {
            _pinInput.value += digit
        }
    }

    fun removePinDigit() {
        if (_pinInput.value.isNotEmpty()) {
            _pinInput.value = _pinInput.value.dropLast(1)
        }
    }

    fun clearPin() {
        _pinInput.value = ""
        _appLockState.value = _appLockState.value.copy(
            authenticationError = null,
            isPinValid = true
        )
    }

    fun setupPin(pin: String, confirmPin: String) {
        if (pin.length != 6 || confirmPin.length != 6) {
            _appLockState.value = _appLockState.value.copy(
                authenticationError = "PIN must be 6 digits"
            )
            return
        }

        if (pin != confirmPin) {
            _appLockState.value = _appLockState.value.copy(
                authenticationError = "PINs do not match"
            )
            return
        }

        viewModelScope.launch {
            try {
                _appLockState.value = _appLockState.value.copy(isSettingUp = true)
                appLockRepository.setPin(pin)
                _appLockState.value = _appLockState.value.copy(
                    isSettingUp = false,
                    isAppLockEnabled = true,
                    authenticationError = null
                )
            } catch (e: Exception) {
                _appLockState.value = _appLockState.value.copy(
                    isSettingUp = false,
                    authenticationError = "Failed to set PIN: ${e.message}"
                )
            }
        }
    }

    fun validatePin(pin: String) {
        if (pin.length != 6) {
            _appLockState.value = _appLockState.value.copy(
                authenticationError = "PIN must be 6 digits",
                isPinValid = false
            )
            return
        }

        viewModelScope.launch {
            try {
                _appLockState.value = _appLockState.value.copy(isAuthenticating = true)
                val isValid = appLockRepository.validatePin(pin)

                if (isValid) {
                    _isAppLocked.value = false
                    _appLockState.value = _appLockState.value.copy(
                        isAuthenticating = false,
                        authenticationError = null,
                        isPinValid = true
                    )
                    clearPin()
                } else {
                    _appLockState.value = _appLockState.value.copy(
                        isAuthenticating = false,
                        authenticationError = "Invalid PIN",
                        isPinValid = false
                    )
                    clearPin()
                }
            } catch (e: Exception) {
                _appLockState.value = _appLockState.value.copy(
                    isAuthenticating = false,
                    authenticationError = "Authentication failed: ${e.message}",
                    isPinValid = false
                )
                clearPin()
            }
        }
    }

    fun authenticateWithBiometric() {
        viewModelScope.launch {
            try {
                _appLockState.value = _appLockState.value.copy(isAuthenticating = true)

                val result = biometricAuthenticator.authenticate(
                    title = "Authenticate",
                    subtitle = "Use your biometric to unlock the app",
                    description = "Place your finger on the fingerprint sensor or look at the camera",
                    negativeButtonText = "Use PIN"
                )

                when (result) {
                    BiometricAuthResult.Success -> {
                        _isAppLocked.value = false
                        _appLockState.value = _appLockState.value.copy(
                            isAuthenticating = false,
                            authenticationError = null
                        )
                    }
                    BiometricAuthResult.Failed -> {
                        _appLockState.value = _appLockState.value.copy(
                            isAuthenticating = false,
                            authenticationError = "Biometric authentication failed"
                        )
                    }
                    BiometricAuthResult.Cancelled -> {
                        _appLockState.value = _appLockState.value.copy(
                            isAuthenticating = false,
                            authenticationError = null
                        )
                    }
                    BiometricAuthResult.Error, BiometricAuthResult.Unavailable -> {
                        _appLockState.value = _appLockState.value.copy(
                            isAuthenticating = false,
                            authenticationError = "Biometric authentication unavailable"
                        )
                    }
                }
            } catch (e: Exception) {
                _appLockState.value = _appLockState.value.copy(
                    isAuthenticating = false,
                    authenticationError = "Authentication error: ${e.message}"
                )
            }
        }
    }

    fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch {
            appLockRepository.setBiometricEnabled(enabled)
            _appLockState.value = _appLockState.value.copy(isBiometricEnabled = enabled)
        }
    }

    fun lockApp() {
        _isAppLocked.value = true
    }

    fun disableAppLock() {
        viewModelScope.launch {
            appLockRepository.removePin()
            _appLockState.value = _appLockState.value.copy(
                isAppLockEnabled = false,
                isBiometricEnabled = false
            )
        }
    }

    // Transaction Management Methods

    /**
     * Create an immediate transaction
     */
    fun createTransaction(
        fromWalletAddress: String,
        toWalletAddress: String,
        amount: String,
        cryptoType: String,
        gasPrice: String = "",
        gasFee: String = "",
        notes: String = "",
        accountId: Int
    ) {
        val transaction = Transaction(
            fromWalletAddress = fromWalletAddress,
            toWalletAddress = toWalletAddress,
            amount = amount,
            cryptoType = cryptoType,
            status = TransactionStatus.PENDING,
            gasPrice = gasPrice,
            gasFee = gasFee,
            notes = notes,
            isScheduled = false,
            accountId = accountId
        )

        viewModelScope.launch {
            transactionDao.insertTransaction(transaction)
        }
    }

    /**
     * Create a scheduled transaction for future execution
     */
    fun createScheduledTransaction(
        fromWalletAddress: String,
        toWalletAddress: String,
        amount: String,
        cryptoType: String,
        scheduledAt: Long,
        gasPrice: String = "",
        gasFee: String = "",
        notes: String = "",
        accountId: Int
    ) {
        val transaction = Transaction(
            fromWalletAddress = fromWalletAddress,
            toWalletAddress = toWalletAddress,
            amount = amount,
            cryptoType = cryptoType,
            status = TransactionStatus.SCHEDULED,
            scheduledAt = scheduledAt,
            gasPrice = gasPrice,
            gasFee = gasFee,
            notes = notes,
            isScheduled = true,
            accountId = accountId
        )

        viewModelScope.launch {
            transactionDao.insertTransaction(transaction)
        }
    }

    /**
     * Get all transactions
     */
    fun getAllTransactions() = transactionDao.getAllTransactions()

    /**
     * Get transactions by account
     */
    fun getTransactionsByAccount(accountId: Int) = transactionDao.getTransactionsByAccount(accountId)

    /**
     * Get scheduled transactions
     */
    fun getScheduledTransactions() = transactionDao.getScheduledTransactions()

    /**
     * Get transactions by status
     */
    fun getTransactionsByStatus(status: TransactionStatus) = transactionDao.getTransactionsByStatus(status)

    /**
     * Update transaction status
     */
    fun updateTransactionStatus(transactionId: Int, status: TransactionStatus) {
        viewModelScope.launch {
            transactionDao.updateTransactionStatus(transactionId, status)
        }
    }

    /**
     * Update transaction when it's executed on blockchain
     */
    fun updateTransactionExecution(
        transactionId: Int,
        transactionHash: String,
        status: TransactionStatus,
        executedAt: Long
    ) {
        viewModelScope.launch {
            transactionDao.updateTransactionExecution(transactionId, transactionHash, status, executedAt)
        }
    }

    /**
     * Cancel a scheduled transaction
     */
    fun cancelScheduledTransaction(transactionId: Int) {
        viewModelScope.launch {
            transactionDao.updateTransactionStatus(transactionId, TransactionStatus.CANCELLED)
        }
    }

    /**
     * Delete a transaction
     */
    fun deleteTransaction(transactionId: Int) {
        viewModelScope.launch {
            transactionDao.deleteTransaction(transactionId)
        }
    }

    /**
     * Get due scheduled transactions that should be executed now
     */
    suspend fun getDueScheduledTransactions(
        currentMillis: Long
    ): List<Transaction> {
        return transactionDao.getDueScheduledTransactions(currentMillis)
    }

    /**
     * Process scheduled transactions that are due for execution
     */
    fun processScheduledTransactions(
        currentMillis: Long
    ) {
        viewModelScope.launch {
            val dueTransactions = getDueScheduledTransactions(currentMillis)
            dueTransactions.forEach { transaction ->
                // Update status to processing
                updateTransactionStatus(transaction.id, TransactionStatus.PROCESSING)

                // Here you would integrate with your blockchain transaction logic
                // For now, we'll just mark it as ready for processing
                // The actual blockchain execution should be handled by your crypto service
            }
        }
    }

    /**
     * Get transaction statistics by account
     */
    suspend fun getTransactionCountByAccount(accountId: Int): Int {
        return transactionDao.getTransactionCountByAccount(accountId)
    }

    /**
     * Get transaction statistics by account and status
     */
    suspend fun getTransactionCountByAccountAndStatus(accountId: Int, status: TransactionStatus): Int {
        return transactionDao.getTransactionCountByAccountAndStatus(accountId, status)
    }

    /**
     * Get the current active account ID
     */
    suspend fun getCurrentAccountId(): Int? {
        return try {
            // This would need to be implemented based on your account management
            // For now returning a default value
            1
        } catch (e: Exception) {
            null
        }
    }

//    private val _uiState = MutableStateFlow(AptosWalletUiState())
//    val uiState: StateFlow<AptosWalletUiState> = _uiState.asStateFlow()
//
//    private var aptosClient: Aptos? = null
//    private var currentAccount: AptosAccount? = null
//
//    init {
//        // Initialize Kaptos client with mainnet using AptosConfig
//        try {
//            val config = AptosConfig(AptosSettings(network = Network.MAINNET))
//            aptosClient = Aptos(config)
//        } catch (_: Exception) {
//            // Fallback initialization if AptosConfig doesn't work as expected
//            aptosClient = null
//        }
//
//        // Check for existing account on initialization
//        loadExistingAccount()
//    }
//
//    private fun loadExistingAccount() {
//        viewModelScope.launch {
//            try {
//                val existingAccount = appLockRepository.getPersistentAccount()
//                if (existingAccount != null) {
//                    currentAccount = existingAccount
//                    val balance = getAccountBalance(existingAccount.accountAddress.toString())
//
//                    _uiState.value = _uiState.value.copy(
//                        isConnected = true,
//                        walletAddress = existingAccount.accountAddress.toString(),
//                        balance = balance
//                    )
//                }
//            } catch (_: Exception) {
//                // If loading fails, we'll create a new account when connectWallet is called
//            }
//        }
//    }
//
//    fun connectWallet() {
//        viewModelScope.launch {
//            try {
//                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
//
//                // Get or create a persistent account
//                val account = appLockRepository.initializePersistentAccount()
//
//                currentAccount = account
//                val balance = getAccountBalance(account.accountAddress.toString())
//
//                _uiState.value = _uiState.value.copy(
//                    isConnected = true,
//                    isLoading = false,
//                    walletAddress = account.accountAddress.toString(),
//                    balance = balance,
//                    error = null
//                )
//            } catch (e: Exception) {
//                _uiState.value = _uiState.value.copy(
//                    isLoading = false,
//                    error = e.message ?: "Failed to connect wallet"
//                )
//            }
//        }
//    }
//
//    private suspend fun getAccountBalance(address: String): String {
//        return try {
//            val accountAddress = AccountAddress.fromString(address)
//            val balance = aptosClient?.getAccountAPTAmount(accountAddress)
//            // Convert balance from octas to APT (1 APT = 100,000,000 octas)
//            val balanceInApt = when {
//                balance is Long -> balance.toDouble() / 100000000.0
//                balance is Int -> balance.toDouble() / 100000000.0
//                balance != null -> balance.toString().toDoubleOrNull()?.div(100000000.0) ?: 0.0
//                else -> 0.0
//            }
//            "$balanceInApt APT"
//        } catch (_: Exception) {
//            "0.0000 APT"
//        }
//    }
//    fun refreshBalance() {
//        currentAccount?.let { account ->
//            viewModelScope.launch {
//                try {
//                    val balance = getAccountBalance(account.accountAddress.toString())
//                    _uiState.value = _uiState.value.copy(balance = balance)
//                } catch (e: Exception) {
//                    _uiState.value = _uiState.value.copy(
//                        error = "Failed to refresh balance: ${e.message}"
//                    )
//                }
//            }
//        }
//    }
//
//    fun disconnectWallet() {
//        viewModelScope.launch {
//            try {
//                appLockRepository.clearAccount()
//                currentAccount = null
//                _uiState.value = AptosWalletUiState()
//            } catch (e: Exception) {
//                _uiState.value = _uiState.value.copy(
//                    error = "Failed to disconnect wallet: ${e.message}"
//                )
//            }
//        }
//    }
}
