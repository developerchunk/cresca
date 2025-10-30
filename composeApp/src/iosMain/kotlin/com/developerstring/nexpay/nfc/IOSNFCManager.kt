package com.developerstring.nexpay.nfc

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreNFC.NFCNDEFReaderSession

@OptIn(ExperimentalForeignApi::class)
actual class PlatformNFCManager actual constructor() : NFCManager {

    private var nfcSession: NFCNDEFReaderSession? = null
    private var onTagReceivedCallback: ((String) -> Unit)? = null

    actual override suspend fun isNFCAvailable(): Boolean {
        return NFCNDEFReaderSession.readingAvailable
    }

    actual override suspend fun isNFCEnabled(): Boolean {
        // On iOS, if NFC is available, it's typically enabled
        return isNFCAvailable()
    }

    actual override suspend fun startReading(onTagReceived: (String) -> Unit): NFCResult {
        if (!isNFCAvailable()) {
            return NFCResult.NotAvailable
        }

        onTagReceivedCallback = onTagReceived

        return try {
            // Create NFC session for reading
            nfcSession = NFCNDEFReaderSession()
            // Note: Actual iOS implementation would require proper delegate setup
            // This is a simplified version
            NFCResult.Success
        } catch (e: Exception) {
            NFCResult.Error(e.message ?: "Failed to start NFC reading")
        }
    }

    actual override suspend fun stopReading(): NFCResult {
        return try {
            nfcSession?.invalidateSession()
            nfcSession = null
            onTagReceivedCallback = null
            NFCResult.Success
        } catch (e: Exception) {
            NFCResult.Error(e.message ?: "Failed to stop NFC reading")
        }
    }

    actual override suspend fun writeWalletAddress(walletAddress: String): NFCResult {
        // iOS NFC writing is limited and requires specific conditions
        return NFCResult.Error("NFC writing not fully supported on iOS")
    }

    actual override suspend fun startSharing(walletAddress: String): NFCResult {
        // iOS doesn't support NFC writing in the same way as Android
        // We could use alternative methods like QR codes or AirDrop
        return NFCResult.Error("NFC sharing not supported on iOS, consider using QR code")
    }

    actual override suspend fun stopSharing(): NFCResult {
        return NFCResult.Success
    }


}
