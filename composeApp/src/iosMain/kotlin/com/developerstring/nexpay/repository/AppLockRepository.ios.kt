package com.developerstring.nexpay.repository

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import platform.CommonCrypto.CC_SHA256
import platform.CommonCrypto.CC_SHA256_DIGEST_LENGTH
import platform.Foundation.NSData
import platform.Foundation.dataUsingEncoding
import platform.Foundation.NSUTF8StringEncoding

@OptIn(ExperimentalForeignApi::class)
actual fun hashPin(pin: String): String {
    val data = pin.encodeToByteArray()
    val digest = ByteArray(CC_SHA256_DIGEST_LENGTH)

    data.usePinned { pinnedData ->
        digest.usePinned { pinnedDigest ->
            CC_SHA256(pinnedData.addressOf(0), data.size.toUInt(), pinnedDigest.addressOf(0))
        }
    }

    return digest.joinToString("") { "%02x".format(it.toUByte()) }
}
