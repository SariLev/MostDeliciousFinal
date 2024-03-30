package com.example.mostdelicious.database.common

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.mostdelicious.database.local.AppLocalDB
import com.example.mostdelicious.database.remote.FirebasePostManager
import com.example.mostdelicious.dto.PostDto
import com.example.mostdelicious.helpers.FirebaseLiveData
import com.example.mostdelicious.helpers.RecipeRequest
import com.example.mostdelicious.helpers.nullIfEmpty
import com.example.mostdelicious.models.ApiResponse
import com.example.mostdelicious.models.MealPost
import com.example.mostdelicious.models.MealsResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostRepository(
    private val localDatabase: AppLocalDB,
    private val remoteDatabase: FirebasePostManager,
    private val sp: SharedPreferences,
) {
    companion object {
        const val POST_LAST_UPDATE_KEY = "post_last_update"
    }

    suspend fun createUpdatePost(form: PostDto) = withContext(Dispatchers.IO) {
        val post = remoteDatabase.createUpdatePost(form)
        localDatabase.mealPostsDao().insert(post)
    }

    suspend fun ratePost(
        coroutineScope: CoroutineScope,
        post: MealPost,
        rating: Float,
    ) = withContext(Dispatchers.IO) {
        remoteDatabase.ratePost(post, rating) {
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    localDatabase.mealPostsDao().insert(post)
                }
            }
        }
    }


    suspend fun recipeRequest(post: MealPost): ApiResponse<MealsResponse> {
        val request = RecipeRequest(post.name)
        return request.get()
    }

    fun listenAllPosts(
        coroutineScope: CoroutineScope,
    ): FirebaseLiveData<List<MealPost>?> {
        val lastUpdated = sp.getLong(POST_LAST_UPDATE_KEY, 0)
        return FirebaseLiveData(
            remoteDatabase.listenToAllPosts(lastUpdated) { posts ->
                posts?.let {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            cachePostsLocally(posts)
                            sp.edit()
                                .putLong(POST_LAST_UPDATE_KEY, System.currentTimeMillis())
                                .apply()
                        }
                    }
                }
            },
            localDatabase.mealPostsDao()
                .listenAllPosts()
                .nullIfEmpty()
                .map {
                    it?.sortedByDescending { meal ->
                        meal.createdAt
                    }
                }
        )
    }

    fun listenCurrentUserPosts(): LiveData<List<MealPost>?> {
        val currentUserId = FirebaseAuth.getInstance().uid ?: ""
        return localDatabase.mealPostsDao().listenUserPosts(currentUserId).nullIfEmpty()
    }


    private suspend fun cachePostsLocally(posts: List<MealPost>) = withContext(Dispatchers.IO) {
        localDatabase.mealPostsDao().insert(posts)
    }
}