package com.developerstring.nexpay.data.room_db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.developerstring.nexpay.Constants

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<com.developerstring.nexpay.data.room_db.AppDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(Constants.DATABASE_NAME)

    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}
