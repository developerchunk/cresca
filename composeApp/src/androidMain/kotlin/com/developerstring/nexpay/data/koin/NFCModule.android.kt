package com.developerstring.nexpay.data.koin

import android.content.Context
import com.developerstring.nexpay.nfc.NFCManager
import com.developerstring.nexpay.nfc.PlatformNFCManager
import org.koin.dsl.module

actual fun platformNFCModule() = module {
    single<NFCManager> {
        val manager = PlatformNFCManager()
        // Initialize with Android context immediately
        val context: Context = get()
        println("NFC Debug: Koin injecting context: ${context::class.java.simpleName}")
        manager.setContext(context)
        manager
    }
}
