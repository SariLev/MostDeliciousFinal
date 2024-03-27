package com.example.mostdelicious.database.remote

import com.example.mostdelicious.dto.UserRegisterForm
import com.example.mostdelicious.helpers.getAsync
import com.example.mostdelicious.helpers.listenToAll
import com.example.mostdelicious.models.MealPost
import com.example.mostdelicious.models.OtherUser
import com.example.mostdelicious.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class FirebaseUserManager {

    companion object {
        const val USER_COLLECTION_PATH = "users"
    }


    suspend fun getAllUsers(): List<OtherUser> = withContext(Dispatchers.IO) {
        return@withContext FirebaseFirestore.getInstance()
            .collection(USER_COLLECTION_PATH)
            .getAsync<OtherUser>()
    }


    fun listenToAllUsers(
        callback: (List<OtherUser>) -> Unit,
    ): ListenerRegistration {
        return FirebaseFirestore.getInstance()
            .collection(USER_COLLECTION_PATH)
            .listenToAll(callback)
    }

    suspend fun createUser(form: UserRegisterForm) = withContext(Dispatchers.IO) {
        val deferred = CompletableDeferred<User>() // Create a Completable
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(form.email, form.password)
            .addOnSuccessListener {
                if (it.user == null) {
                    // error!
                    deferred.completeExceptionally(Exception("Unknown error"))
                    return@addOnSuccessListener
                }

                val user = User(it.user!!.uid, form.email, form.fullName)

                FirebaseFirestore
                    .getInstance()
                    .collection(USER_COLLECTION_PATH)
                    .document(user.id)
                    .set(user)
                    .addOnSuccessListener {
                        deferred.complete(user)
                    }
            }
            .addOnFailureListener {
                deferred.completeExceptionally(it)
            }

        deferred.await()
    }

    suspend fun signIn(email: String, password: String) = withContext(Dispatchers.IO) {
        val deferred = CompletableDeferred<Void?>() // Create a Completable
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                if (it.user == null) {
                    // error!
                    deferred.completeExceptionally(Exception("Unknown error"))
                    return@addOnSuccessListener
                }
                deferred.complete(null)
            }
            .addOnFailureListener {
                deferred.completeExceptionally(it)
            }
        deferred.await()
    }

}