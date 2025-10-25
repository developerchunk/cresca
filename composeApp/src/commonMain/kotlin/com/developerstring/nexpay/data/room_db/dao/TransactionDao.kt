package com.developerstring.nexpay.data.room_db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.developerstring.nexpay.data.room_db.model.Transaction
import com.developerstring.nexpay.data.room_db.model.TransactionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM `transaction` ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM `transaction` WHERE accountId = :accountId ORDER BY createdAt DESC")
    fun getTransactionsByAccount(accountId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM `transaction` WHERE isScheduled = 1 AND status = :status ORDER BY scheduledAt ASC")
    fun getScheduledTransactions(status: TransactionStatus = TransactionStatus.SCHEDULED): Flow<List<Transaction>>

    @Query("SELECT * FROM `transaction` WHERE scheduledAt <= :currentTime AND status = :status")
    suspend fun getDueScheduledTransactions(
        currentTime: Long,
        status: TransactionStatus = TransactionStatus.SCHEDULED
    ): List<Transaction>

    @Query("SELECT * FROM `transaction` WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: Int): Transaction?

    @Query("SELECT * FROM `transaction` WHERE transactionHash = :hash")
    suspend fun getTransactionByHash(hash: String): Transaction?

    @Query("SELECT * FROM `transaction` WHERE status = :status ORDER BY createdAt DESC")
    fun getTransactionsByStatus(status: TransactionStatus): Flow<List<Transaction>>

    @Insert
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Query("UPDATE `transaction` SET status = :status WHERE id = :transactionId")
    suspend fun updateTransactionStatus(transactionId: Int, status: TransactionStatus)

    @Query("UPDATE `transaction` SET transactionHash = :hash, status = :status, executedAt = :executedAt WHERE id = :transactionId")
    suspend fun updateTransactionExecution(
        transactionId: Int,
        hash: String,
        status: TransactionStatus,
        executedAt: Long
    )

    @Query("DELETE FROM `transaction` WHERE id = :transactionId")
    suspend fun deleteTransaction(transactionId: Int)

    @Query("DELETE FROM `transaction` WHERE status = :status")
    suspend fun deleteTransactionsByStatus(status: TransactionStatus)

    // Statistics queries
    @Query("SELECT COUNT(*) FROM `transaction` WHERE accountId = :accountId")
    suspend fun getTransactionCountByAccount(accountId: Int): Int

    @Query("SELECT COUNT(*) FROM `transaction` WHERE status = :status AND accountId = :accountId")
    suspend fun getTransactionCountByAccountAndStatus(accountId: Int, status: TransactionStatus): Int
}

