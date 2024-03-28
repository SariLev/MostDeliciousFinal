package com.example.mostdelicious.database.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mostdelicious.models.MealPost

@Dao
interface MealPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg meal: MealPost)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meals: List<MealPost>)

    @Query("SELECT * from meal_posts WHERE postUserId = :id")
    fun listenUserPosts(id: String): LiveData<List<MealPost>>

    @Query("DELETE FROM meal_posts WHERE id NOT IN (:idsToKeep)")
    suspend fun deleteAllExcept(idsToKeep: List<String>)

    @Query("SELECT * from meal_posts")
    fun listenAllPosts(): LiveData<List<MealPost>>

    @Query("DELETE from meal_posts")
    suspend fun deleteAllPosts() : Int

    @Query("SELECT * from meal_posts")
    suspend fun getAll(): List<MealPost>
}