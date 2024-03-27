package com.example.mostdelicious.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Serializable
@Entity(tableName = "meal_posts")
data class MealPost(
    @PrimaryKey val id: String = "",
    val image: String = "",
    val name: String = "",
    val content: String = "",
    val ratingUsers: List<String> = listOf(),
    val averageRating: Double = 0.0,
    val postUserId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)