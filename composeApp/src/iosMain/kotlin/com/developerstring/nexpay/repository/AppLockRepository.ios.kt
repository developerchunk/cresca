package com.developerstring.nexpay.repository

import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual fun hashPin(pin: String): String {
    // Use a simple hash implementation for iOS
    // In production, you might want to use a more secure approach
    val data = pin.encodeToByteArray()

    // Simple SHA-256 like hash using native Kotlin
    var hash = 5381
    for (byte in data) {
        hash = ((hash shl 5) + hash) + byte.toInt()
    }

    // Convert to hex string
    val result = hash.toString(16)
    return if (result.length < 8) {
        result.padStart(8, '0')
    } else {
        result.takeLast(8)
    }
}
