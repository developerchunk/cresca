package com.developerstring.nexpay.data.koin

import com.developerstring.nexpay.authentication.AndroidBiometricAuthenticator
import com.developerstring.nexpay.authentication.BiometricAuthenticator
import com.developerstring.nexpay.data.room_db.AppDatabase
import com.developerstring.nexpay.data.room_db.getDatabaseBuilder
import com.developerstring.nexpay.data.room_db.getRoomDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<AppDatabase> {
        val builder = getDatabaseBuilder(context = get())
        getRoomDatabase(builder)
    }
    single<BiometricAuthenticator> { AndroidBiometricAuthenticator(context = get()) }
}