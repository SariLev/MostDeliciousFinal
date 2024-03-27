package com.example.mostdelicious.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mostdelicious.database.common.UserRepository
import com.example.mostdelicious.database.remote.FirebaseUserManager
import com.example.mostdelicious.dto.UserRegisterForm
import com.example.mostdelicious.helpers.LoadingState
import com.example.mostdelicious.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {

    val currentUser = repository.getCurrentUser()

    private var _exceptions = MutableLiveData<Exception?>()
    val exceptions: LiveData<Exception?> get() = _exceptions

    private var _loadingState = MutableLiveData<LoadingState>(LoadingState.Loaded)
    val loadingState: LiveData<LoadingState> get() = _loadingState

    private var userDocumentListener: ListenerRegistration? = null
    private val authListener: FirebaseAuth.AuthStateListener =
        FirebaseAuth.AuthStateListener { userAuth ->
            if (userAuth.currentUser != null) {
                userDocumentListener?.remove()

                userDocumentListener = FirebaseFirestore.getInstance()
                    .collection(FirebaseUserManager.USER_COLLECTION_PATH)
                    .document(userAuth.currentUser!!.uid)
                    .addSnapshotListener { value, error ->
                        if (value != null) {
                            val user = value.toObject(User::class.java)
                            if (user != null) {
                                viewModelScope.launch {
                                    repository.cacheUserLocally(user)
                                }
                            }
                        }
                    }
            } else {
                userDocumentListener?.remove()
            }
        }

    init {
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
    }

    fun createUser(form: UserRegisterForm) {
        viewModelScope.launch {
            try {
                // now loading
                _loadingState.postValue(LoadingState.Loading)
                val user = repository.createUser(form)
                // stop loading
                _loadingState.postValue(LoadingState.Loaded)
            } catch (e: Exception) {
                _exceptions.postValue(e)
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                // now loading
                _loadingState.postValue(LoadingState.Loading)
                repository.signIn(email, password)
                // stop loading
                _loadingState.postValue(LoadingState.Loaded)
            } catch (e: Exception) {
                _exceptions.postValue(e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        userDocumentListener?.remove()
        FirebaseAuth.getInstance().removeAuthStateListener(authListener)
    }

}