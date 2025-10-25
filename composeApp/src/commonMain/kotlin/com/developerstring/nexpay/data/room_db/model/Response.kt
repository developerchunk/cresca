package com.developerstring.nexpay.data.room_db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Response(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val chatId: Long = 0L,
    val userEntry: String = "",
    val response: String = "",
)