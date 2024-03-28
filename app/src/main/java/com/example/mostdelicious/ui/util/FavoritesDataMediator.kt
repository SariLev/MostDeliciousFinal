package com.example.mostdelicious.ui.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.mostdelicious.models.FeedData
import com.example.mostdelicious.models.MealPost
import com.example.mostdelicious.models.OtherUser
import com.example.mostdelicious.models.User
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


class FavoritesDataMediator(
    private val allPosts: LiveData<List<MealPost>?>,
    private val allUsers: LiveData<List<OtherUser>?>,
    private val currentUser: LiveData<User?>,
) : ReadOnlyProperty<Any?, MediatorLiveData<FeedData>> {

    private val favoritePosts by lazy {
        MediatorLiveData<List<MealPost>?>().apply {
            value = listOf()
            addSource(currentUser) { user ->
                if (user == null) return@addSource
                val liked = HashSet(user.favoriteMeals)
                value = allPosts.value?.filter { post ->
                    liked.contains(post.id)
                }
            }
        }
    }

    private val favoritesData by lazy {
        MediatorLiveData<FeedData>().apply {
            value = FeedData()

            addSource(favoritePosts) { posts ->
                val currentData = value ?: FeedData()
                posts?.let {
                    value = currentData.copy(posts = posts)
                }
            }

            addSource(allUsers) { users ->
                val currentData = value ?: FeedData()
                users?.let {
                    value = currentData.copy(allUsers = users)
                }
            }

            addSource(currentUser) { user ->
                val currentData = value ?: FeedData()
                user?.let {
                    value = currentData.copy(userLikedPosts = user.favoriteMeals)
                }
            }
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): MediatorLiveData<FeedData> {
        return favoritesData
    }
}
