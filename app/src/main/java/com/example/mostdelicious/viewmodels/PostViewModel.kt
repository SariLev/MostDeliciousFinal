package com.example.mostdelicious.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mostdelicious.database.common.PostRepository
import com.example.mostdelicious.database.common.UserRepository
import com.example.mostdelicious.dto.PostDto
import com.example.mostdelicious.helpers.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
) : ViewModel() {


    private val _exceptions = MutableLiveData<Exception?>(null)
    val exceptions: LiveData<Exception?> get() = _exceptions

    val loadingState = MutableLiveData<LoadingState>(LoadingState.Loaded)

    fun createOrUpdatePost(form: PostDto, callback: () -> Unit) {
        viewModelScope.launch {
            try {
                loadingState.postValue(LoadingState.Loading)
                postRepository.createPost(form)
                loadingState.postValue(LoadingState.Loaded)
                callback()
            } catch (e: Exception) {
                _exceptions.postValue(e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

}