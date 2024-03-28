package com.example.mostdelicious.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mostdelicious.helpers.extensions.EMPTY_VALUE
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "current_users")
open class User(
    @PrimaryKey var id: String = EMPTY_VALUE,
    var email: String = EMPTY_VALUE,
    var name: String = EMPTY_VALUE,
    var favoriteMeals: MutableList<String> = mutableListOf(),
    var ratedPosts: MutableList<String> = mutableListOf(),
    var lastUpdated: Long = System.currentTimeMillis(),
)
