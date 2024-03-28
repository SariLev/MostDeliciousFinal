package com.example.mostdelicious.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mostdelicious.helpers.extensions.EMPTY_VALUE
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "meal_posts")
data class MealPost(
    @PrimaryKey val id: String = EMPTY_VALUE,
    var image: String = EMPTY_VALUE,
    var name: String = EMPTY_VALUE,
    var content: String = EMPTY_VALUE,
    var ratingUsers: MutableList<String> = mutableListOf(),
    var averageRating: Double = 0.0,
    var postUserId: String = EMPTY_VALUE,
    var lastUpdated: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
) {
    companion object {
        const val AVERAGE_RATING_KEY = "averageRating"
        const val RATING_USERS_KEY = "ratingUsers"
    }
}