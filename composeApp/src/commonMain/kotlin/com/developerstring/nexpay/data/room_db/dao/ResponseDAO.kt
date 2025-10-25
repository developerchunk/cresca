package com.developerstring.nexpay.data.room_db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.developerstring.nexpay.data.room_db.model.Response
import kotlinx.coroutines.flow.Flow

@Dao
interface ResponseDao {
    @Query("SELECT * FROM response")
    fun getResponses(): Flow<List<Response>>

    @Insert
    suspend fun insertResponse(response: Response)

//    @Query("DELETE FROM chat")
//    suspend fun deleteChats()
}