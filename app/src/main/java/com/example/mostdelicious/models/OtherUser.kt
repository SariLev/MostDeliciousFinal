package com.example.mostdelicious.models

import androidx.room.Entity
import com.example.mostdelicious.helpers.extensions.EMPTY_VALUE

@Entity(tableName = "other_users")
class OtherUser(
    id: String = EMPTY_VALUE,
    email: String = EMPTY_VALUE,
    name: String = EMPTY_VALUE,
    favoriteMeals: MutableList<String> = mutableListOf(),
    ratedPosts: MutableList<String> = mutableListOf(),
) : User(id, email, name, favoriteMeals, ratedPosts)
