package com.example.mostdelicious.ui.util

import com.example.mostdelicious.database.common.UserRepository
import com.example.mostdelicious.database.remote.FirebaseUserManager
import com.example.mostdelicious.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AuthListener(
    coroutineScope: CoroutineScope,
    repository: UserRepository,
) {
    private var userDocumentListener: ListenerRegistration? = null
    private val authListener: FirebaseAuth.AuthStateListener =
        FirebaseAuth.AuthStateListener { userAuth ->
            if (userAuth.currentUser != null) {
                userDocumentListener?.remove()
                userDocumentListener = FirebaseFirestore.getInstance()
                    .collection(FirebaseUserManager.USER_COLLECTION_PATH)
                    .document(userAuth.currentUser!!.uid)
                    .addSnapshotListener { value, error ->
                        error?.printStackTrace()
                        if (value != null) {
                            val user = value.toObject(User::class.java)
                            if (user != null) {
                                coroutineScope.launch {
                                    try {
                                        repository.cacheUserLocally(user)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }
                    }
            } else {
                userDocumentListener?.remove()
            }
        }

    fun attach() {
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
    }

    fun detach() {
        FirebaseAuth.getInstance().removeAuthStateListener(authListener)
        userDocumentListener?.remove()
    }
}