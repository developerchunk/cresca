package com.developerstring.nexpay.nfc

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NFCCardReader {

    companion object {
        private val WALLET_AID = byteArrayOf(
            0xA0.toByte(), 0x00, 0x00, 0x02, 0x47, 0x10, 0x01
        )

        private val SELECT_COMMAND = byteArrayOf(
            0x00, 0xA4.toByte(), 0x04, 0x00, 0x07
        ) + WALLET_AID
    }

    suspend fun readWalletAddressFromCard(tag: Tag): String? = withContext(Dispatchers.IO) {
        try {
            val isoDep = IsoDep.get(tag)
            if (isoDep != null) {
                isoDep.connect()

                println("NFC Debug: Connected to HCE card, sending SELECT command")

                // Send SELECT command to the HCE service
                val response = isoDep.transceive(SELECT_COMMAND)

                isoDep.close()

                // Check if we got a successful response (ends with 9000)
                if (response.size >= 2 &&
                    response[response.size - 2] == 0x90.toByte() &&
                    response[response.size - 1] == 0x00.toByte()) {

                    // Extract wallet address (everything except the last 2 status bytes)
                    val walletAddressBytes = ByteArray(response.size - 2)
                    System.arraycopy(response, 0, walletAddressBytes, 0, response.size - 2)
                    val walletAddress = String(walletAddressBytes, Charsets.UTF_8)

                    println("NFC Debug: Successfully read wallet address from HCE: $walletAddress")
                    return@withContext walletAddress
                } else {
                    println("NFC Debug: HCE response indicates error or no data")
                    return@withContext null
                }
            } else {
                println("NFC Debug: Tag does not support ISO-DEP (HCE)")
                return@withContext null
            }
        } catch (e: Exception) {
            println("NFC Debug: Error reading from HCE card: ${e.message}")
            return@withContext null
        }
    }
}
