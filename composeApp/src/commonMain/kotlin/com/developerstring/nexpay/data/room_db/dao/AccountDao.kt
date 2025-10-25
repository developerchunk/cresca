package com.developerstring.nexpay.data.room_db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.developerstring.nexpay.data.room_db.model.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM account")
    fun getAccounts(): Flow<List<Account>>

    @Query("SELECT * FROM account WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveAccount(): Account?

    @Query("SELECT * FROM account WHERE walletAddress = :address LIMIT 1")
    suspend fun getAccountByAddress(address: String): Account?

    @Insert
    suspend fun insertAccount(account: Account): Long

    @Update
    suspend fun updateAccount(account: Account)

    @Query("UPDATE account SET isActive = 0")
    suspend fun deactivateAllAccounts()

    @Query("UPDATE account SET isActive = 1 WHERE id = :accountId")
    suspend fun setActiveAccount(accountId: Int)
}
