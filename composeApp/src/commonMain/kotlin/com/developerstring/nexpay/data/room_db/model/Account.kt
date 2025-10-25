package com.developerstring.nexpay.data.room_db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val walletAddress: String = "",
    val privateKey: String = "",
    val publicKey: String = "",
    val accountName: String = "",
    val createdAt: Long = 0L,
    val isActive: Boolean = false,
)
