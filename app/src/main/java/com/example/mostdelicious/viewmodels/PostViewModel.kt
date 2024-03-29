package com.example.mostdelicious.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mostdelicious.database.common.PostRepository
import com.example.mostdelicious.database.common.UserRepository
import com.example.mostdelicious.dto.PostDto
import com.example.mostdelicious.helpers.LoadingState
import com.example.mostdelicious.models.ApiResponse
import com.example.mostdelicious.models.Meal
import com.example.mostdelicious.models.MealPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _exceptions = MutableLiveData<Exception?>(null)
    val exceptions: LiveData<Exception?> get() = _exceptions
    val loadingState = MutableLiveData<LoadingState>(LoadingState.Loaded)

    val recipeLiveData = MutableLiveData<Meal?>()


    fun createOrUpdatePost(form: PostDto, callback: () -> Unit) {
        viewModelScope.launch {
            try {
                loadingState.postValue(LoadingState.Loading)
                postRepository.createUpdatePost(form)
                loadingState.postValue(LoadingState.Loaded)
                callback()
            } catch (e: Exception) {
                _exceptions.postValue(e)
            }
        }
    }

    fun ratePost(post: MealPost, rating: Float, callback: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    postRepository.ratePost(viewModelScope, post, rating)
                    val user = userRepository.getCurrentUser() ?: return@withContext
                    user.ratedPosts.add(post.id)
                    userRepository.saveUser(user)
                    callback()
                } catch (e: Exception) {
                    _exceptions.postValue(e)
                }
            }
        }
    }

    fun recipeRequest(post: MealPost) {
        viewModelScope.launch {
            when (val response = postRepository.recipeRequest(post)) {
                is ApiResponse.Success -> {
                    if (response.data.isEmpty()) {
                        recipeLiveData.postValue(null)
                    } else {
                        recipeLiveData.postValue(response.data.meals!![0])
                    }
                }

                is ApiResponse.Failure -> {
                    Log.d("Recipe request fail", "Status code ${response.status}")
                    _exceptions.postValue(Exception(response.message))
                }
            }
        }
    }
}