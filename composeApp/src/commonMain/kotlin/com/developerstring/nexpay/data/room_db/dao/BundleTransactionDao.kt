package com.developerstring.nexpay.data.room_db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.developerstring.nexpay.data.room_db.model.BundleTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface BundleTransactionDao {
    @Query("SELECT * FROM bundletransaction")
    fun getBundlesTrans(): Flow<List<BundleTransaction>>

    @Insert
    suspend fun insertBundlesTrans(bundleTransaction: BundleTransaction)
}

