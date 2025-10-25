package com.developerstring.nexpay.ui.nfc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developerstring.nexpay.nfc.NFCManager
import com.developerstring.nexpay.nfc.NFCResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class NFCUiState(
    val isNFCAvailable: Boolean = false,
    val isNFCEnabled: Boolean = false,
    val isReading: Boolean = false,
    val isSharing: Boolean = false,
    val isLoading: Boolean = false,
    val receivedWalletAddress: String? = null,
    val currentWalletAddress: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class NFCViewModel : ViewModel(), KoinComponent {

    private val nfcManager: NFCManager by inject()

    private val _uiState = MutableStateFlow(NFCUiState())
    val uiState: StateFlow<NFCUiState> = _uiState.asStateFlow()

    init {
        checkNFCAvailability()
    }

    private fun checkNFCAvailability() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val isAvailable = nfcManager.isNFCAvailable()
            val isEnabled = if (isAvailable) nfcManager.isNFCEnabled() else false

            _uiState.value = _uiState.value.copy(
                isNFCAvailable = isAvailable,
                isNFCEnabled = isEnabled,
                isLoading = false,
                errorMessage = when {
                    !isAvailable -> "NFC is not available on this device"
                    !isEnabled -> "Please enable NFC in your device settings"
                    else -> null
                }
            )
        }
    }

    fun setWalletAddress(address: String) {
        _uiState.value = _uiState.value.copy(currentWalletAddress = address)
    }

    fun startReading() {
        if (!_uiState.value.isNFCAvailable || !_uiState.value.isNFCEnabled) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "NFC is not available or enabled"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isReading = true,
                errorMessage = null,
                receivedWalletAddress = null
            )

            val result = nfcManager.startReading { walletAddress ->
                println("NFC Debug: startSharing called with address: ${walletAddress}")
                _uiState.value = _uiState.value.copy(
                    receivedWalletAddress = walletAddress,
                    isReading = false,
                    successMessage = "Wallet address received successfully!"
                )
            }

            when (result) {
                is NFCResult.Success -> {
                    // Reading started successfully, keep the reading state
                }
                is NFCResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isReading = false,
                        errorMessage = result.message
                    )
                }
                is NFCResult.NotAvailable -> {
                    _uiState.value = _uiState.value.copy(
                        isReading = false,
                        errorMessage = "NFC is not available on this device"
                    )
                }
                is NFCResult.NotEnabled -> {
                    _uiState.value = _uiState.value.copy(
                        isReading = false,
                        errorMessage = "Please enable NFC in your device settings"
                    )
                }
                is NFCResult.PermissionDenied -> {
                    _uiState.value = _uiState.value.copy(
                        isReading = false,
                        errorMessage = "NFC permission denied"
                    )
                }
            }
        }
    }

    fun stopReading() {
        viewModelScope.launch {
            val result = nfcManager.stopReading()
            _uiState.value = _uiState.value.copy(
                isReading = false,
                errorMessage = if (result is NFCResult.Error) result.message else null
            )
        }
    }

    fun startSharing() {
        val walletAddress = _uiState.value.currentWalletAddress
        println("NFC Debug: startSharing called with address: $walletAddress")

        if (walletAddress.isBlank()) {
            println("NFC Debug: No wallet address to share")
            _uiState.value = _uiState.value.copy(
                errorMessage = "No wallet address to share"
            )
            return
        }

        if (!_uiState.value.isNFCAvailable || !_uiState.value.isNFCEnabled) {
            println("NFC Debug: NFC not available or enabled")
            _uiState.value = _uiState.value.copy(
                errorMessage = "NFC is not available or enabled"
            )
            return
        }

        viewModelScope.launch {
            println("NFC Debug: Setting sharing state to true")
            _uiState.value = _uiState.value.copy(
                isSharing = true,
                errorMessage = null
            )

            println("NFC Debug: Calling nfcManager.startSharing")
            val result = nfcManager.startSharing(walletAddress)

            println("NFC Debug: startSharing result: $result")
            when (result) {
                is NFCResult.Success -> {
                    println("NFC Debug: Sharing started successfully")
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Ready to share wallet address via NFC"
                    )
                }
                is NFCResult.Error -> {
                    println("NFC Debug: Sharing error: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        isSharing = false,
                        errorMessage = result.message
                    )
                }
                is NFCResult.NotAvailable -> {
                    _uiState.value = _uiState.value.copy(
                        isSharing = false,
                        errorMessage = "NFC is not available on this device"
                    )
                }
                is NFCResult.NotEnabled -> {
                    _uiState.value = _uiState.value.copy(
                        isSharing = false,
                        errorMessage = "Please enable NFC in your device settings"
                    )
                }
                is NFCResult.PermissionDenied -> {
                    _uiState.value = _uiState.value.copy(
                        isSharing = false,
                        errorMessage = "NFC permission denied"
                    )
                }
            }
        }
    }

    fun stopSharing() {
        viewModelScope.launch {
            val result = nfcManager.stopSharing()
            _uiState.value = _uiState.value.copy(
                isSharing = false,
                errorMessage = if (result is NFCResult.Error) result.message else null
            )
        }
    }

    fun writeWalletAddressToTag(walletAddress: String) {
        if (!_uiState.value.isNFCAvailable || !_uiState.value.isNFCEnabled) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "NFC is not available or enabled"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = nfcManager.writeWalletAddress(walletAddress)

            when (result) {
                is NFCResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Wallet address written to NFC tag successfully"
                    )
                }
                is NFCResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NFCResult.NotAvailable -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "NFC is not available on this device"
                    )
                }
                is NFCResult.NotEnabled -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Please enable NFC in your device settings"
                    )
                }
                is NFCResult.PermissionDenied -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "NFC permission denied"
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    fun clearReceivedAddress() {
        _uiState.value = _uiState.value.copy(
            receivedWalletAddress = null
        )
    }

    fun refreshNFCStatus() {
        checkNFCAvailability()
    }
}
