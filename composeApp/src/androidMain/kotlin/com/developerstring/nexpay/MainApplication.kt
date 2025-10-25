package com.developerstring.nexpay

import android.app.Application
import com.developerstring.nexpay.data.koin.initKoin
import org.koin.android.ext.koin.androidContext
import com.developerstring.nexpay.data.data_store.initPreferencesDataStore

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize preferences DataStore with the application context early so
        // platform `createPreferencesDataStore()` can use it when Koin creates the DataStore.
        initPreferencesDataStore(this@MainApplication)

        initKoin(
            appDeclaration = { androidContext(this@MainApplication) },
        )
    }
}