package com.developerstring.nexpay.data.room_db

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.developerstring.nexpay.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSHomeDirectory

actual fun createDatabase(): AppDatabase {
    val dbFilePath = NSHomeDirectory() + "/${Constants.DATABASE_NAME}"
    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath,
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
