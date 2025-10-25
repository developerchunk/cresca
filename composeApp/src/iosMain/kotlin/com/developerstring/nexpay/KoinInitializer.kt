package com.developerstring.nexpay

import com.developerstring.nexpay.data.koin.initKoin

// Function to initialize Koin for iOS - accessible as KoinInitializerKt.doInitKoinIos() from Swift
fun doInitKoinIos() = initKoin(appDeclaration = {})
