package com.example.mostdelicious.models

data class FeedData(
    var posts: List<MealPost>? = null,
    var allUsers: List<User>? = null,
    var userLikedPosts: List<String>? = null,
) {
    fun allResourcesAvailable() = posts != null && allUsers != null && userLikedPosts != null
}
