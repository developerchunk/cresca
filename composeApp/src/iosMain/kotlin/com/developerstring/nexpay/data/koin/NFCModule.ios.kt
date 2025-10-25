package com.developerstring.nexpay.data.koin

import com.developerstring.nexpay.nfc.NFCManager
import com.developerstring.nexpay.nfc.PlatformNFCManager
import org.koin.dsl.module

actual fun platformNFCModule() = module {
    single<NFCManager> { PlatformNFCManager() }
}
