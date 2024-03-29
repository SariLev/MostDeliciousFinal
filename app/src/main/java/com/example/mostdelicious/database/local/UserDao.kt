package com.example.mostdelicious.database.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mostdelicious.models.User
import com.google.firebase.auth.FirebaseAuth


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: List<User>)

    @Query("SELECT * from current_users LIMIT 1")
    fun listenUser(): LiveData<User?>

    @Query("SELECT * from current_users LIMIT 1")
    suspend fun getUser(): User?


    @Query("DELETE FROM current_users WHERE id NOT IN (:idsToKeep)")
    suspend fun deleteAllExcept(idsToKeep: List<String>)

    @Query("DELETE from current_users")
    fun deleteCurrentUser()
}