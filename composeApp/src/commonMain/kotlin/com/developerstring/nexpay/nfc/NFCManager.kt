package com.developerstring.nexpay.nfc

/**
 * Common interface for NFC operations across platforms
 */
interface NFCManager {

    /**
     * Check if NFC is available on the device
     */
    suspend fun isNFCAvailable(): Boolean

    /**
     * Check if NFC is enabled on the device
     */
    suspend fun isNFCEnabled(): Boolean

    /**
     * Start NFC reading mode to detect NFC tags
     */
    suspend fun startReading(onTagReceived: (String) -> Unit): NFCResult

    /**
     * Stop NFC reading mode
     */
    suspend fun stopReading(): NFCResult

    /**
     * Write wallet address to NFC tag (for receiver mode)
     */
    suspend fun writeWalletAddress(walletAddress: String): NFCResult

    /**
     * Start NFC writing mode to share wallet address
     */
    suspend fun startSharing(walletAddress: String): NFCResult

    /**
     * Stop NFC sharing mode
     */
    suspend fun stopSharing(): NFCResult
}

/**
 * Platform-specific NFC manager implementation
 */
expect class PlatformNFCManager() : NFCManager {
    override suspend fun isNFCAvailable(): Boolean
    override suspend fun isNFCEnabled(): Boolean
    override suspend fun startReading(onTagReceived: (String) -> Unit): NFCResult
    override suspend fun stopReading(): NFCResult
    override suspend fun writeWalletAddress(walletAddress: String): NFCResult
    override suspend fun startSharing(walletAddress: String): NFCResult
    override suspend fun stopSharing(): NFCResult
}

/**
 * Result of NFC operations
 */
sealed class NFCResult {
    object Success : NFCResult()
    data class Error(val message: String) : NFCResult()
    object NotAvailable : NFCResult()
    object NotEnabled : NFCResult()
    object PermissionDenied : NFCResult()
}
