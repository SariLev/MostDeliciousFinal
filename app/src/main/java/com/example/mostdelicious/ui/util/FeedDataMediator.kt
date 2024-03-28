package com.example.mostdelicious.ui.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.mostdelicious.models.FeedData
import com.example.mostdelicious.models.MealPost
import com.example.mostdelicious.models.OtherUser
import com.example.mostdelicious.models.User
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FeedDataMediator(
    private val postsLiveData: LiveData<List<MealPost>?>,
    private val usersLiveData: LiveData<List<OtherUser>?>,
    private val currentUserLiveData: LiveData<User?>,
) : ReadOnlyProperty<Any?, MediatorLiveData<FeedData>> {

    private val mediatorLiveData by lazy {
        MediatorLiveData<FeedData>().apply {
            value = FeedData()

            addSource(postsLiveData) { posts ->
                val currentData = value ?: FeedData()
                posts?.let {
                    value = currentData.copy(posts = posts)
                }
            }

            addSource(usersLiveData) { users ->
                val currentData = value ?: FeedData()
                users?.let {
                    value = currentData.copy(allUsers = users)
                }
            }

            addSource(currentUserLiveData) { user ->
                val currentData = value ?: FeedData()
                user?.let {
                    value = currentData.copy(userLikedPosts = user.favoriteMeals)
                }
            }
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): MediatorLiveData<FeedData> {
        return mediatorLiveData
    }
}
