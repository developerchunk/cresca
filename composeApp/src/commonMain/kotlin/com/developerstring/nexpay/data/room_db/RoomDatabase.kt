package com.developerstring.nexpay.data.room_db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.developerstring.nexpay.Constants
import com.developerstring.nexpay.data.room_db.dao.AccountDao
import com.developerstring.nexpay.data.room_db.dao.ChatDao
import com.developerstring.nexpay.data.room_db.dao.ResponseDao
import com.developerstring.nexpay.data.room_db.dao.TransactionDao
import com.developerstring.nexpay.data.room_db.model.Account
import com.developerstring.nexpay.data.room_db.model.Chat
import com.developerstring.nexpay.data.room_db.model.Response
import com.developerstring.nexpay.data.room_db.model.Transaction


@Database(entities = [Chat::class, Response::class, Account::class, Transaction::class], version = Constants.DATABASE_VERSION, exportSchema = true)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getChatDao(): ChatDao
    abstract fun getResponseDao(): ResponseDao
    abstract fun getAccountDao(): AccountDao
    abstract fun getTransactionDao(): TransactionDao
}

// Platform-specific database creation function
expect fun createDatabase(): AppDatabase
