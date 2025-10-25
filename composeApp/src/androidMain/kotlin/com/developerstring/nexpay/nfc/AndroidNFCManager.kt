package com.developerstring.nexpay.nfc

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.nfc.NfcManager as AndroidNfcManager
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume

actual class PlatformNFCManager actual constructor() : NFCManager {

    private var context: Context? = null
    private var nfcAdapter: NfcAdapter? = null
    private var onTagReceivedCallback: ((String) -> Unit)? = null
    private var isSharing = false
    private var sharingWalletAddress: String? = null
    private val cardReader = NFCCardReader()

    init {
        initializeNFC()
    }

    private fun initializeNFC() {
        context?.let { ctx ->
            try {
                val nfcManager = ctx.getSystemService(Context.NFC_SERVICE) as? AndroidNfcManager
                nfcAdapter = nfcManager?.defaultAdapter
                println("NFC Debug: Context available = true, NfcManager = ${nfcManager != null}, NfcAdapter = ${nfcAdapter != null}")
            } catch (e: Exception) {
                println("NFC Debug: Error initializing NFC - ${e.message}")
            }
        } ?: run {
            println("NFC Debug: Context is null, cannot initialize NFC")
        }
    }

    actual override suspend fun isNFCAvailable(): Boolean {
        // If nfcAdapter is null, try to initialize it if context is available
        if (nfcAdapter == null) {
            println("NFC Debug: Adapter is null, trying to initialize...")
            initializeNFC()
        }
        val available = nfcAdapter != null
        println("NFC Debug: isNFCAvailable() = $available")
        return available
    }

    actual override suspend fun isNFCEnabled(): Boolean {
        // Ensure NFC adapter is initialized before checking if enabled
        if (nfcAdapter == null) {
            initializeNFC()
        }
        return nfcAdapter?.isEnabled == true
    }

    actual override suspend fun startReading(onTagReceived: (String) -> Unit): NFCResult {
        if (!isNFCAvailable()) {
            return NFCResult.NotAvailable
        }

        if (!isNFCEnabled()) {
            return NFCResult.NotEnabled
        }

        return try {
            onTagReceivedCallback = onTagReceived
            // In a real implementation, this would set up foreground dispatch
            // to listen for NFC tags when the app is in the foreground
            NFCResult.Success
        } catch (e: Exception) {
            NFCResult.Error(e.message ?: "Failed to start NFC reading")
        }
    }

    actual override suspend fun stopReading(): NFCResult {
        return try {
            onTagReceivedCallback = null
            // Stop foreground dispatch
            NFCResult.Success
        } catch (e: Exception) {
            NFCResult.Error(e.message ?: "Failed to stop NFC reading")
        }
    }

    actual override suspend fun writeWalletAddress(walletAddress: String): NFCResult {
        return suspendCancellableCoroutine { continuation ->
            try {
                // This would typically be called in onNewIntent when a writable tag is detected
                // The actual writing would happen when a tag is detected
                continuation.resume(NFCResult.Success)
            } catch (e: Exception) {
                continuation.resume(NFCResult.Error(e.message ?: "Failed to prepare wallet address"))
            }
        }
    }

    actual override suspend fun startSharing(walletAddress: String): NFCResult {
        if (!isNFCAvailable()) {
            return NFCResult.NotAvailable
        }

        if (!isNFCEnabled()) {
            return NFCResult.NotEnabled
        }

        return try {
            println("NFC Debug: Starting sharing for wallet address: $walletAddress")
            val activity = context as? Activity
            if (activity != null) {
                // Set sharing state
                isSharing = true
                sharingWalletAddress = walletAddress

                // Set the wallet address in the HCE service for sharing
                WalletSharingNfcService.walletAddressToShare = walletAddress

                println("NFC Debug: Wallet address set in HCE service for sharing")

                // Also enable foreground dispatch as fallback for direct NFC tag interaction
                val intent = Intent(activity, activity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    putExtra("nfc_sharing_data", walletAddress)
                }

                val pendingIntent = PendingIntent.getActivity(
                    activity, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )

                // Set up intent filters for NFC
                val ndefFilter = android.content.IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
                val tagFilter = android.content.IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)

                val filters = arrayOf(ndefFilter, tagFilter)
                val techList = arrayOf<Array<String>>()

                nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, filters, techList)

                println("NFC Debug: NFC sharing mode enabled - device ready to share via HCE and direct tap")
                NFCResult.Success
            } else {
                println("NFC Debug: Activity context is null, cannot start sharing")
                NFCResult.Error("Activity context not available")
            }
        } catch (e: Exception) {
            println("NFC Debug: Error starting NFC sharing - ${e.message}")
            NFCResult.Error(e.message ?: "Failed to start NFC sharing")
        }
    }

    actual override suspend fun stopSharing(): NFCResult {
        return try {
            println("NFC Debug: Stopping NFC sharing")
            val activity = context as? Activity
            if (activity != null) {
                // Clear sharing state
                isSharing = false
                sharingWalletAddress = null

                // Clear wallet address from HCE service
                WalletSharingNfcService.walletAddressToShare = null

                // Disable foreground dispatch if it was enabled for sharing
                nfcAdapter?.disableForegroundDispatch(activity)

                println("NFC Debug: NFC sharing stopped successfully")
            }
            NFCResult.Success
        } catch (e: Exception) {
            println("NFC Debug: Error stopping NFC sharing - ${e.message}")
            NFCResult.Error(e.message ?: "Failed to stop NFC sharing")
        }
    }

    // Additional methods for MainActivity integration
    fun initialize(activity: Activity) {
        context = activity
        initializeNFC()
    }

    fun setContext(context: Context) {
        println("NFC Debug: setContext called with context: ${context::class.java.simpleName}")
        this.context = context
        initializeNFC()
    }

    fun onActivityResumed(activity: Activity) {
        context = activity
        // Enable foreground dispatch to intercept NFC intents
        val intent = Intent(activity, activity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            activity, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val techList = arrayOf<Array<String>>()
        nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, null, techList)
    }

    fun onActivityPaused(activity: Activity) {
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    fun handleNfcIntent(intent: Intent) {
        println("NFC Debug: handleNfcIntent called with action: ${intent.action}")

        // If we're in sharing mode and receive an NFC intent, we should also share our data
        if (isSharing && sharingWalletAddress != null) {
            println("NFC Debug: Device is in sharing mode, attempting to share wallet address")
            // This is handled by the callback we set up, but we can add additional logic here
        }

        // Handle NFC intent when app receives NFC tag
        when (intent.action) {
            NfcAdapter.ACTION_NDEF_DISCOVERED -> {
                println("NFC Debug: NDEF discovered")
                @Suppress("DEPRECATION")
                val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                rawMessages?.let { messages ->
                    println("NFC Debug: Found ${messages.size} NDEF messages")
                    for (rawMessage in messages) {
                        val message = rawMessage as NdefMessage
                        for (record in message.records) {
                            val payload = extractWalletAddress(record)
                            if (payload != null) {
                                println("NFC Debug: Extracted wallet address: $payload")
                                onTagReceivedCallback?.invoke(payload)
                            }
                        }
                    }
                }
            }
            NfcAdapter.ACTION_TAG_DISCOVERED -> {
                println("NFC Debug: Tag discovered")
                // Handle basic tag discovery
                @Suppress("DEPRECATION")
                val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
                tag?.let { nfcTag ->
                    // First try to read as HCE card (modern approach)
                    try {
                        CoroutineScope(Dispatchers.IO).launch {
                            val walletAddress = cardReader.readWalletAddressFromCard(nfcTag)
                            if (walletAddress != null) {
                                println("NFC Debug: Read wallet address from HCE card: $walletAddress")
                                CoroutineScope(Dispatchers.Main).launch {
                                    onTagReceivedCallback?.invoke(walletAddress)
                                }
                                return@launch
                            }

                            // If HCE reading failed, try traditional NDEF reading
                            val ndef = Ndef.get(nfcTag)
                            ndef?.let { ndefTag ->
                                try {
                                    ndefTag.connect()
                                    val ndefMessage = ndefTag.ndefMessage
                                    ndefMessage?.let { message ->
                                        for (record in message.records) {
                                            val payload = extractWalletAddress(record)
                                            if (payload != null) {
                                                println("NFC Debug: Extracted wallet address from NDEF: $payload")
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    onTagReceivedCallback?.invoke(payload)
                                                }
                                                break
                                            }
                                        }
                                    }
                                    ndefTag.close()
                                } catch (e: Exception) {
                                    println("NFC Debug: Error reading NDEF from tag: ${e.message}")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        println("NFC Debug: Error processing tag: ${e.message}")
                    }
                }
            }
        }
    }

    private fun extractWalletAddress(record: NdefRecord): String? {
        return try {
            when (record.tnf) {
                NdefRecord.TNF_WELL_KNOWN -> {
                    if (record.type.contentEquals(NdefRecord.RTD_TEXT)) {
                        // Text record - extract the wallet address
                        val payload = record.payload
                        if (payload.size > 3) {
                            val languageCodeLength = payload[0].toInt() and 0x3F
                            val walletAddress = String(payload, languageCodeLength + 1, payload.size - languageCodeLength - 1)
                            println("NFC Debug: Extracted text record: $walletAddress")
                            return walletAddress
                        }
                    } else if (record.type.contentEquals(NdefRecord.RTD_URI)) {
                        // URI record - extract wallet address from URI
                        val uriString = String(record.payload, 1, record.payload.size - 1)
                        println("NFC Debug: Extracted URI record: $uriString")
                        if (uriString.startsWith("nexpay://wallet/")) {
                            return uriString.substringAfter("nexpay://wallet/")
                        }
                        return uriString
                    }
                }
            }
            null
        } catch (e: Exception) {
            println("NFC Debug: Error extracting wallet address: ${e.message}")
            null
        }
    }

    private fun createNdefMessage(walletAddress: String): NdefMessage {
        println("NFC Debug: Creating NDEF message with wallet address: $walletAddress")

        // Create a text record with the wallet address
        val textRecord = NdefRecord.createTextRecord("en", walletAddress)

        // Also create a URI record as alternative (some apps prefer URIs)
        val uriRecord = NdefRecord.createUri("nexpay://wallet/$walletAddress")

        // Create NDEF message with both records
        return NdefMessage(arrayOf(textRecord, uriRecord))
    }
}
