package com.example.mostdelicious.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.mostdelicious.database.common.PostRepository
import com.example.mostdelicious.database.common.UserRepository
import com.example.mostdelicious.helpers.LoadingState
import com.example.mostdelicious.models.MealPost
import com.example.mostdelicious.models.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class FeedData(
    var posts: List<MealPost>? = null,
    var allUsers: List<User>? = null,
    var userLikedPosts: List<String>? = null,
) {
    fun isAllMet(): Boolean {
        return posts != null && allUsers != null && userLikedPosts != null
    }
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
) : ViewModel() {

    val currentUser = userRepository.getCurrentUser()
    private val allUsersData = userRepository.listenAllUsers(viewModelScope)
    private val allPostsData = postRepository.listenAllPosts(viewModelScope)
    val currentUserPosts = postRepository.listenCurrentUserPosts()
    val loadingState = MutableLiveData<LoadingState>(LoadingState.Loaded)

    private val _feedData = MediatorLiveData<FeedData>().apply {
        value = FeedData()

        addSource(allPostsData.second) { posts ->
            val currentData = value ?: FeedData()
            value = currentData.copy(posts = posts)
        }

        addSource(allUsersData.second) { users ->
            val currentData = value ?: FeedData()
            value = currentData.copy(allUsers = users)
        }

        addSource(currentUser) { user ->
            val currentData = value ?: FeedData()
            value = currentData.copy(userLikedPosts = user?.favoriteMeals)
        }
    }

    val feedData: LiveData<FeedData> = _feedData

    override fun onCleared() {
        super.onCleared()
        allUsersData.first.remove()
        allPostsData.first.remove()
    }
}