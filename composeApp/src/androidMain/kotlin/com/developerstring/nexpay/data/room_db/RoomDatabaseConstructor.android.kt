package com.developerstring.nexpay.data.room_db

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.developerstring.nexpay.Constants
import kotlinx.coroutines.Dispatchers

actual fun createDatabase(): AppDatabase {
    // This should be called with proper context from your Android application
    // You'll need to modify this to accept context or get it from Application class
    throw IllegalStateException("Database should be created using createDatabaseWithContext(context)")
}

fun createDatabaseWithContext(context: Context): AppDatabase {
    return Room.databaseBuilder<AppDatabase>(
        context = context,
        name = context.getDatabasePath(Constants.DATABASE_NAME).absolutePath
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
