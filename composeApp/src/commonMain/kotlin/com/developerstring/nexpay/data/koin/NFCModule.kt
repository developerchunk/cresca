package com.developerstring.nexpay.data.koin

import com.developerstring.nexpay.ui.nfc.NFCViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect fun platformNFCModule(): Module

val nfcModule = module {
    includes(platformNFCModule())
    viewModel { NFCViewModel() }
}
