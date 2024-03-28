package com.example.mostdelicious.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mostdelicious.database.common.PostRepository
import com.example.mostdelicious.database.common.UserRepository
import com.example.mostdelicious.helpers.LoadingState
import com.example.mostdelicious.ui.util.FavoritesDataMediator
import com.example.mostdelicious.ui.util.FeedDataMediator
import dagger.hilt.android.lifecycle.HiltViewModel

import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
) : ViewModel() {
    val loadingState = MutableLiveData<LoadingState>(LoadingState.Loading)

    // current user, for internal usage only
    val currentUser = userRepository.listenCurrentUser()

    // all the app's users
    private val allUsers = userRepository.listenAllUsers(viewModelScope)

    // all the app's posts
    private val allPosts = postRepository.listenAllPosts(viewModelScope)

    // data used for feed page
    val feedData by FeedDataMediator(
        allPosts.get(),
        allUsers.get(),
        currentUser
    )

    // data used for favorites page
    val favoritesData by FavoritesDataMediator(
        allPosts.get(),
        allUsers.get(),
        currentUser
    )

    //  data used for profile page (show current user's posts)
    val profilePostsData by FeedDataMediator(
        postRepository.listenCurrentUserPosts(),
        allUsers.get(),
        currentUser
    )


    fun likeTogglePost(postId: String) {
        currentUser.value?.let { user ->
            userRepository.likePostToggle(viewModelScope, user, postId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        allPosts.stopListening()
        allUsers.stopListening()
    }

}