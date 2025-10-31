package com.developerstring.nexpay.data.room_db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bundletransaction")
data class BundleTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hex: String = "",
    val gasFees: Long = 0L,
    val success: Boolean = false,
    val message: String = "",
    val btcPrice: Double = 0.0,
    val ethPrice: Double = 0.0,
    val solPrice: Double = 0.0,
    val timestamp: Long = 0L,
    val btcWeight: Int = 50,
    val ethWeight: Int = 30,
    val solWeight: Int = 20,
    val amount: String = "",
    val leverage: Int = 0,
    val isLong: Boolean = false,
)
