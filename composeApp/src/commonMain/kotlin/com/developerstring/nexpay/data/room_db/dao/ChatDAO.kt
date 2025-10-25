package com.developerstring.nexpay.data.room_db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.developerstring.nexpay.data.room_db.model.Chat
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat")
    fun getChats(): Flow<List<Chat>>

    @Insert
    suspend fun insertChat(chat: Chat)

//    @Query("DELETE FROM chat")
//    suspend fun deleteChats()
}