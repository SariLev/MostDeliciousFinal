package com.example.mostdelicious.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mostdelicious.database.common.UserRepository
import com.example.mostdelicious.dto.UserRegisterForm
import com.example.mostdelicious.helpers.LoadingState
import com.example.mostdelicious.ui.util.AuthListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private var _exceptions = MutableLiveData<Exception?>()
    val exceptions: LiveData<Exception?> get() = _exceptions

    val currentUser = userRepository.listenCurrentUser()

    private var _loadingState = MutableLiveData<LoadingState>(LoadingState.Loaded)
    val loadingState: LiveData<LoadingState> get() = _loadingState

    // active current user listener
    // updates whenever the current user is updated
    private val authListener = AuthListener(viewModelScope, userRepository)
        .apply { attach() }

    fun createUser(form: UserRegisterForm, positiveCallback: () -> Unit) {
        viewModelScope.launch {
            try {
                // now loading
                _loadingState.postValue(LoadingState.Loading)
                val user = userRepository.createUser(form)
                positiveCallback()
            } catch (e: Exception) {
                _exceptions.postValue(e)
            } finally {
                // stop loading
                _loadingState.postValue(LoadingState.Loaded)
            }
        }
    }

    fun signIn(email: String, password: String, positiveCallback: () -> Unit) {
        viewModelScope.launch {
            try {
                // now loading
                _loadingState.postValue(LoadingState.Loading)
                userRepository.signIn(email, password)
                positiveCallback()
            } catch (e: Exception) {
                _exceptions.postValue(e)
            } finally {
                // stop loading
                _loadingState.postValue(LoadingState.Loaded)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        authListener.detach()
    }

    fun logOut() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userRepository.logOut()
            }
        }
    }

}