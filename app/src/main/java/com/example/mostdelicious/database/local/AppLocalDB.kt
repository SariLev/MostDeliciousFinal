package com.example.mostdelicious.database.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mostdelicious.models.MealPost
import com.example.mostdelicious.models.OtherUser
import com.example.mostdelicious.models.User

@Database(entities = [User::class, OtherUser::class, MealPost::class], version = 5)
@TypeConverters(Converters::class)
abstract class AppLocalDB : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun otherUserDao(): OtherUserDao

    abstract fun mealPostsDao(): MealPostDao

    companion object {
        @Volatile
        private var instance: AppLocalDB? = null
        fun getInstance(context: Context): AppLocalDB {
            if (instance == null) {
                instance = Room.databaseBuilder(context, AppLocalDB::class.java, "db")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }
    }
}