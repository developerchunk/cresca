# App Lock Implementation Guide

## Overview
This App Lock system provides secure 6-digit PIN and biometric authentication for both Android and iOS platforms using encrypted data storage and the existing Koin dependency injection.

## Features Implemented

### ✅ Security Features
- **6-digit PIN**: Secure PIN with SHA-256 hashing
- **Biometric Authentication**: Touch ID, Face ID, Fingerprint support
- **Encrypted Storage**: Platform-specific encryption (Android Security Crypto, iOS Keychain)
- **Auto-lock**: App locks when backgrounded (configurable)

### ✅ Platform Support
- **Android**: Uses AndroidX Biometric + Security Crypto libraries
- **iOS**: Uses LocalAuthentication + Keychain services
- **Shared Logic**: Common business logic in SharedViewModel

## Quick Start

### 1. Add App Lock to Settings
```kotlin
// In your settings screen
Button(
    onClick = { navController.navigate("app_lock_settings") }
) {
    Text("App Lock Settings")
}
```

### 2. Integrate with App Lifecycle
```kotlin
@Composable
fun YourMainApp() {
    val sharedViewModel: SharedViewModel = koinViewModel()
    val isAppLocked by sharedViewModel.isAppLocked.collectAsState()
    val appLockState by sharedViewModel.appLockState.collectAsState()

    if (isAppLocked && appLockState.isAppLockEnabled) {
        AppLockScreen()
    } else {
        // Your main app content
        YourMainContent()
    }
}
```

### 3. Lock App on Background
```kotlin
// In your MainActivity (Android) or App lifecycle
override fun onPause() {
    super.onPause()
    if (appLockEnabled) {
        sharedViewModel.lockApp()
    }
}
```

## API Reference

### SharedViewModel Methods
```kotlin
// PIN Management
fun setupPin(pin: String, confirmPin: String)
fun validatePin(pin: String)
fun disableAppLock()

// Biometric
fun authenticateWithBiometric()
fun toggleBiometric(enabled: Boolean)

// App State
fun lockApp()
fun addPinDigit(digit: String)
fun removePinDigit()
fun clearPin()
```

### AppLockState Properties
```kotlin
data class AppLockState(
    val isAppLockEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val isBiometricAvailable: Boolean = false,
    val isSettingUp: Boolean = false,
    val isAuthenticating: Boolean = false,
    val authenticationError: String? = null,
    val isPinValid: Boolean = true
)
```

## Security Details

### PIN Security
- **Hashing**: SHA-256 with platform-specific implementations
- **Storage**: Encrypted DataStore (Android) / Keychain (iOS)
- **Length**: Fixed 6-digit requirement
- **Validation**: Secure comparison with stored hash

### Biometric Security
- **Android**: Uses AndroidX Biometric with BIOMETRIC_WEAK policy
- **iOS**: Uses LocalAuthentication with device owner authentication
- **Fallback**: PIN entry if biometric fails
- **Privacy**: Biometric data never leaves the device

### Data Encryption
- **Android**: AES256_GCM encryption via Security Crypto library
- **iOS**: Keychain services with device-specific encryption
- **Key Management**: Platform-handled master keys

## Navigation Integration

The system includes three main screens:
1. **AppLockScreen**: Main unlock interface
2. **PinSetupScreen**: PIN creation and confirmation
3. **AppLockSettingsScreen**: Settings management

All screens are integrated with the existing SharedViewModel and follow Material 3 design principles.

## Dependencies Added
- `androidx.biometric:biometric:1.1.0` (Android)
- `androidx.security:security-crypto:1.1.0-alpha06` (Android)
- Platform crypto libraries automatically included for iOS

## Testing
- Test PIN setup flow
- Test biometric authentication (if available)
- Test app lock/unlock cycle
- Test settings enable/disable functionality
- Test encrypted storage persistence
