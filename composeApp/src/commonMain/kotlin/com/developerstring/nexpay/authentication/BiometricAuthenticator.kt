package com.developerstring.nexpay.authentication

sealed class BiometricAuthResult {
    object Success : BiometricAuthResult()
    object Failed : BiometricAuthResult()
    object Error : BiometricAuthResult()
    object Unavailable : BiometricAuthResult()
    object Cancelled : BiometricAuthResult()
}

interface BiometricAuthenticator {
    suspend fun isBiometricAvailable(): Boolean
    suspend fun authenticate(
        title: String,
        subtitle: String,
        description: String,
        negativeButtonText: String
    ): BiometricAuthResult
}
