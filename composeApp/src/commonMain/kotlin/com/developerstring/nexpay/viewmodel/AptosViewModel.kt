package com.developerstring.nexpay.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerstring.nexpay.repository.AppLockRepository
import com.developerstring.nexpay.repository.AptosAccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xyz.mcxross.kaptos.Aptos
import xyz.mcxross.kaptos.model.AccountAddress
import xyz.mcxross.kaptos.model.AptosConfig
import xyz.mcxross.kaptos.model.AptosSettings
import xyz.mcxross.kaptos.model.Network
import xyz.mcxross.kaptos.account.Account as AptosAccount


class AptosViewModel(
    private val appLockRepository: AppLockRepository,
): ViewModel() {

        private val _uiState = MutableStateFlow(AptosWalletUiState())
    val uiState: StateFlow<AptosWalletUiState> = _uiState.asStateFlow()

    private var aptosClient: Aptos? = null
    private var currentAccount: AptosAccount? = null

    init {
        // Initialize Kaptos client with mainnet using AptosConfig
        try {
            val config = AptosConfig(AptosSettings(network = Network.MAINNET))
            aptosClient = Aptos(config)
        } catch (_: Exception) {
            // Fallback initialization if AptosConfig doesn't work as expected
            aptosClient = null
        }

        // Check for existing account on initialization
        loadExistingAccount()
    }

    private fun loadExistingAccount() {
        viewModelScope.launch {
            try {
                val existingAccount = appLockRepository.getPersistentAccount()
                if (existingAccount != null) {
                    currentAccount = existingAccount
                    val balance = getAccountBalance(existingAccount.accountAddress.toString())

                    _uiState.value = _uiState.value.copy(
                        isConnected = true,
                        walletAddress = existingAccount.accountAddress.toString(),
                        balance = balance
                    )
                }
            } catch (_: Exception) {
                // If loading fails, we'll create a new account when connectWallet is called
            }
        }
    }

    fun connectWallet() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                // Get or create a persistent account
                val account = appLockRepository.initializePersistentAccount()

                currentAccount = account
                val balance = getAccountBalance(account.accountAddress.toString())

                _uiState.value = _uiState.value.copy(
                    isConnected = true,
                    isLoading = false,
                    walletAddress = account.accountAddress.toString(),
                    balance = balance,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to connect wallet"
                )
            }
        }
    }

    private suspend fun getAccountBalance(address: String): String {
        return try {
            val accountAddress = AccountAddress.fromString(address)
            val balance = aptosClient?.getAccountAPTAmount(accountAddress)
            // Convert balance from octas to APT (1 APT = 100,000,000 octas)
            val balanceInApt = when {
                balance is Long -> balance.toDouble() / 100000000.0
                balance is Int -> balance.toDouble() / 100000000.0
                balance != null -> balance.toString().toDoubleOrNull()?.div(100000000.0) ?: 0.0
                else -> 0.0
            }
            "$balanceInApt APT"
        } catch (_: Exception) {
            "0.0000 APT"
        }
    }
    fun refreshBalance() {
        currentAccount?.let { account ->
            viewModelScope.launch {
                try {
                    val balance = getAccountBalance(account.accountAddress.toString())
                    _uiState.value = _uiState.value.copy(balance = balance)
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to refresh balance: ${e.message}"
                    )
                }
            }
        }
    }

    fun disconnectWallet() {
        viewModelScope.launch {
            try {
                appLockRepository.clearAccount()
                currentAccount = null
                _uiState.value = AptosWalletUiState()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to disconnect wallet: ${e.message}"
                )
            }
        }
    }

}

data class AptosWalletUiState(
    val isConnected: Boolean = false,
    val isLoading: Boolean = false,
    val walletAddress: String? = null,
    val balance: String = "0.0000 APT",
    val error: String? = null
)
