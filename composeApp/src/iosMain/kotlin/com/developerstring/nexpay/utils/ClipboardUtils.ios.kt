package com.developerstring.nexpay.utils

import platform.UIKit.*
import platform.Foundation.*

actual fun copyToClipboard(text: String) {
    val pasteboard = UIPasteboard.generalPasteboard
    pasteboard.string = text
}

actual fun showToast(message: String) {
    // iOS doesn't have built-in toast, we could implement a custom one
    // For now, we'll just print to console
    println("Toast: $message")
}
