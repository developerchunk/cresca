package com.developerstring.nexpay.nfc

import android.nfc.cardemulation.HostApduService
import android.os.Bundle

class WalletSharingNfcService : HostApduService() {

    companion object {
        private const val WALLET_AID = "A0000002471001" // Application ID for our wallet sharing
        private const val SELECT_OK_SW = "9000"
        private const val UNKNOWN_CMD_SW = "0000"

        // Static variable to hold the wallet address to share
        @Volatile
        var walletAddressToShare: String? = null
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null) {
            return hexStringToByteArray(UNKNOWN_CMD_SW)
        }

        // Check if this is a SELECT command for our wallet AID
        val selectCommand = byteArrayOf(0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte())

        if (commandApdu.size >= 4 &&
            commandApdu[0] == selectCommand[0] &&
            commandApdu[1] == selectCommand[1] &&
            commandApdu[2] == selectCommand[2] &&
            commandApdu[3] == selectCommand[3]) {

            // This is a SELECT command, return our wallet address if available
            val walletAddress = walletAddressToShare
            return if (walletAddress != null) {
                // Return wallet address + success status
                val addressBytes = walletAddress.toByteArray(Charsets.UTF_8)
                val response = ByteArray(addressBytes.size + 2)
                System.arraycopy(addressBytes, 0, response, 0, addressBytes.size)
                response[addressBytes.size] = 0x90.toByte()
                response[addressBytes.size + 1] = 0x00.toByte()
                response
            } else {
                hexStringToByteArray(UNKNOWN_CMD_SW)
            }
        }

        // Unknown command
        return hexStringToByteArray(UNKNOWN_CMD_SW)
    }

    override fun onDeactivated(reason: Int) {
        // Called when the NFC link is lost
    }

    private fun hexStringToByteArray(hex: String): ByteArray {
        val len = hex.length
        val data = ByteArray(len / 2)
        for (i in 0 until len step 2) {
            data[i / 2] = ((Character.digit(hex[i], 16) shl 4) + Character.digit(hex[i + 1], 16)).toByte()
        }
        return data
    }
}
