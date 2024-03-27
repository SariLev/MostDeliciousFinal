package com.example.mostdelicious.database.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mostdelicious.models.OtherUser
import com.example.mostdelicious.models.User

@Dao
interface OtherUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg user: OtherUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: List<OtherUser>)

    @Query("SELECT * from other_users WHERE id = :id ")
    fun listenUser(id: String): LiveData<OtherUser?>

    @Query("SELECT * from other_users")
    fun listenAll(): LiveData<List<OtherUser>>

    @Query("DELETE FROM other_users WHERE id NOT IN (:idsToKeep)")
    suspend fun deleteAllExcept(idsToKeep: List<String>)

    @Query("DELETE from other_users")
    suspend fun deleteCurrentUser(): Int

    @Query("SELECT * from other_users")
    suspend fun getAll(): List<OtherUser>
}