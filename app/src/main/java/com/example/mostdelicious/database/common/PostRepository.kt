package com.example.mostdelicious.database.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mostdelicious.database.local.AppLocalDB
import com.example.mostdelicious.database.local.MealPostDao
import com.example.mostdelicious.database.remote.FirebasePostManager
import com.example.mostdelicious.dto.PostDto
import com.example.mostdelicious.models.MealPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostRepository(
    private val localDatabase: AppLocalDB,
    private val remoteDatabase: FirebasePostManager,
) {

    suspend fun createPost(form: PostDto) = withContext(Dispatchers.IO) {
        val post = remoteDatabase.createPost(form)
        localDatabase.mealPostsDao().insert(post)
    }

    suspend fun getAllPosts(): List<MealPost> {
        return localDatabase.mealPostsDao().getAll()
    }

    fun listenAllPosts(
        coroutineScope: CoroutineScope,
    ): Pair<ListenerRegistration, LiveData<List<MealPost>>> {
        val listener = remoteDatabase.listenToAllPosts { posts ->
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    localDatabase.mealPostsDao().deleteAllExcept(posts.map(MealPost::id))
                    localDatabase.mealPostsDao().insert(posts)
                }
            }
        }
        return Pair(
            listener,
            localDatabase.mealPostsDao().listenAllMeals()
        )
    }


    fun listenCurrentUserPosts(): LiveData<List<MealPost>> {
        val currentUserId = FirebaseAuth.getInstance().uid ?: ""
        return localDatabase.mealPostsDao().listenUserMeals(currentUserId)
    }
}