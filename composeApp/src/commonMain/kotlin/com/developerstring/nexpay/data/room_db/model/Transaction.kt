package com.developerstring.nexpay.data.room_db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fromWalletAddress: String = "",
    val toWalletAddress: String = "",
    val amount: String = "",
    val cryptoType: String = "", // e.g., "BTC", "ETH", "APT"
    val transactionHash: String = "", // Empty for scheduled transactions
    val status: TransactionStatus = TransactionStatus.PENDING,
    val createdAt: Long = 0L,
    val scheduledAt: Long? = null, // Null for immediate transactions
    val executedAt: Long? = null, // When the transaction was actually executed
    val gasPrice: String = "",
    val gasFee: String = "",
    val notes: String = "",
    val isScheduled: Boolean = false,
    val accountId: Int = 0, // Reference to the Account that created this transaction
)

enum class TransactionStatus {
    PENDING,     // Created but not sent yet
    SCHEDULED,   // Scheduled for future execution
    PROCESSING,  // Currently being processed on blockchain
    COMPLETED,   // Successfully completed
    FAILED,      // Failed to execute
    CANCELLED    // Cancelled by user
}
