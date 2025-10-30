package com.developerstring.nexpay.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

private lateinit var applicationContext: Context

fun initializeUtils(context: Context) {
    applicationContext = context
}
actual fun copyToClipboard(text: String) {
    val clipboard = applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Wallet Address", text)
    clipboard.setPrimaryClip(clip)
}

actual fun showToast(message: String) {
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
}


