package com.developerstring.nexpay.repository

import java.security.MessageDigest

actual fun hashPin(pin: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashedBytes = digest.digest(pin.toByteArray())
    return hashedBytes.joinToString("") { "%02x".format(it) }
}
