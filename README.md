# Cresca

A smart wallet built with Kotlin Multiplatform that enables one-click perpetual basket trading, seamless cross-chain swaps, and scheduled payments — all in one place.

Automated, synchronized, and designed to simplify how users trade, pay, and manage their crypto portfolios across Android and iOS.

## Features

- **One-Click Perpetual Basket Trading** - Execute complex trading strategies with a single tap
- **Cross-Chain Swaps** - Seamlessly swap assets across different blockchain networks
- **Scheduled Payments** - Set up automated recurring crypto payments
- **Unified Portfolio Management** - Track and manage all your crypto assets in one place
- **Cross-Platform** - Built with Kotlin Multiplatform for native Android and iOS experiences

## Kotlin Multiplatform Architecture

- **Kotlin Multiplatform (KMP)** - Shared business logic across platforms
- **Android** - Native Android implementation
- **iOS** - Native iOS implementation

## Development Setup

### Prerequisites

Before you begin, ensure you have the following installed:

#### For Android Development
- [Android Studio](https://developer.android.com/studio) (latest version)
- Android SDK 36
- Android Emulator

#### For iOS Development
- [Xcode](https://developer.apple.com/xcode/) (latest version) and Xcode Command Line Tools
- iOS Simulator (latest version)
- [Homebrew](https://brew.sh/)
- [CocoaPods](https://cocoapods.org/) (install via Homebrew: `brew install cocoapods`)

#### For KMP Development
- [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- Kotlin Multiplatform (KMP) Plugin

### Installation Steps

1. **Install Android Studio**
   ```bash
   # Download from https://developer.android.com/studio
   # Install Android SDK 36
   # Set up Android Emulator
   ```

2. **Install Xcode and iOS Tools**
   ```bash
   # Install Xcode from App Store or https://developer.apple.com/xcode/
   # Install Xcode Command Line Tools
   xcode-select --install

   # Install latest iOS Simulator from Xcode
   ```

3. **Install CocoaPods**
   ```bash
   # Install Homebrew if not already installed
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

   # Install CocoaPods
   brew install cocoapods
   ```

4. **Install IntelliJ IDEA**
   ```bash
   # Download from https://www.jetbrains.com/idea/
   # Install KMP Plugin from Settings → Plugins → Marketplace → Search "Kotlin Multiplatform"
   ```

5. **Clone the Repository**
   ```bash
   # Import project via Version Control in IntelliJ IDEA
   # File → New → Project from Version Control
   # Enter repository URL: https://github.com/developerchunk/cresca/
   ```

6. **Build and Run**
    - For Android: Open project in Android Studio and run on emulator or device
    - For iOS: Open iOS project in Xcode and run on simulator or device

## Quick Installation (Testing)

Want to test the app directly without setting up the development environment?

### Android APK

Download and install the APK directly:

1. Download the APK from [https://cresca.in/cresca.apk](https://cresca.in/cresca.apk)
2. Open the downloaded file
3. If prompted, allow installation from unknown sources for your browser
4. Install the app

**Note:** You may need to enable "Install unknown apps" permission for Chrome or your download manager in Android settings.

## Project Structure

```
cresca/
├── androidApp/          # Android-specific code
├── iosApp/             # iOS-specific code
├── shared/             # Shared KMP code
├── build.gradle.kts    # Build configuration
└── README.md
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

[Add your license here]

## Contact

- GitHub: [@developerchunk](https://github.com/developerchunk)
- Website: [cresca.in](https://cresca.in)

## Support

For issues, questions, or suggestions, please open an issue on GitHub.

---

Built with ❤️ using Kotlin Multiplatform


