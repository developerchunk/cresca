package com.developerstring.nexpay.authentication

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAErrorUserCancel
import platform.LocalAuthentication.LAErrorUserFallback
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
class IOSBiometricAuthenticator : BiometricAuthenticator {

    override suspend fun isBiometricAvailable(): Boolean {
        val context = LAContext()
        return memScoped {
            val error = alloc<kotlinx.cinterop.ObjCObjectVar<NSError?>>()
            context.canEvaluatePolicy(
                LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                error = error.ptr
            )
        }
    }

    override suspend fun authenticate(
        title: String,
        subtitle: String,
        description: String,
        negativeButtonText: String
    ): BiometricAuthResult = suspendCancellableCoroutine { continuation ->

        val context = LAContext()
        context.localizedFallbackTitle = negativeButtonText

        context.evaluatePolicy(
            policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
            localizedReason = description
        ) { success, error ->
            when {
                success -> continuation.resume(BiometricAuthResult.Success)
                error != null -> {
                    when (error.code.toInt()) {
                        LAErrorUserCancel.toInt(), LAErrorUserFallback.toInt() ->
                            continuation.resume(BiometricAuthResult.Cancelled)
                        else -> continuation.resume(BiometricAuthResult.Failed)
                    }
                }
                else -> continuation.resume(BiometricAuthResult.Error)
            }
        }
    }
}
