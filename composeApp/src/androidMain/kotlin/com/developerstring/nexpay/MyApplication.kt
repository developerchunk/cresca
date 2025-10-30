package com.developerstring.nexpay

import android.app.Application
import com.developerstring.nexpay.data.koin.initKoin
import org.koin.android.ext.koin.androidContext
import com.developerstring.nexpay.data.data_store.initPreferencesDataStore
//import qrgenerator.AppContext

//import qrgenerator.AppContext

class MyApplication : Application() {

//    companion object {
//        lateinit var INSTANCE: MyApplication
//    }

    override fun onCreate() {
        super.onCreate()

        // Initialize preferences DataStore with the application context early so
        // platform `createPreferencesDataStore()` can use it when Koin creates the DataStore.
        initPreferencesDataStore(this@MyApplication)

        initKoin(
            appDeclaration = { androidContext(this@MyApplication) },
        )

//        INSTANCE = this
//        AppContext.apply { set(applicationContext) }
    }
}