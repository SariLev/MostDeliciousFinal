package com.example.mostdelicious.helpers.extensions

import android.net.Uri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Firebase
suspend inline fun <reified T> DocumentReference.getAsync(): T? =
    withContext(Dispatchers.IO) {
        val deferred = CompletableDeferred<T?>()
        this@getAsync.get()
            .addOnSuccessListener { documentSnapshot ->
                deferred.complete(documentSnapshot.toObject(T::class.java))
            }
            .addOnFailureListener { exception ->
                deferred.completeExceptionally(exception)
            }
        deferred.await()
    }

suspend inline fun <T : Any> DocumentReference.setAsync(data: T) =
    withContext(Dispatchers.IO) {
        val deferred = CompletableDeferred<Void?>()
        this@setAsync.set(data)
            .addOnSuccessListener {
                deferred.complete(null)
            }
            .addOnFailureListener { exception ->
                deferred.completeExceptionally(exception)
            }
        deferred.await()
    }

suspend inline fun <reified T> Query.getAsync(): List<T> =
    withContext(Dispatchers.IO) {
        val deferred = CompletableDeferred<List<T>>()
        this@getAsync.get()
            .addOnSuccessListener { documentSnapshot ->
                deferred.complete(documentSnapshot.toObjects(T::class.java))
            }
            .addOnFailureListener { exception ->
                deferred.completeExceptionally(exception)
            }
        deferred.await()
    }

inline fun <reified T> Query.listenToAll(
    crossinline callback: (List<T>?) -> Unit,
): ListenerRegistration {
    return addSnapshotListener { snap, _ ->
        callback.invoke(snap?.let {
            val posts = it.toObjects(T::class.java)
            posts
        })
    }
}


suspend inline fun <reified T> CollectionReference.getOneAsync(id: String): T? =
    document(id).getAsync()

suspend fun StorageReference.putAsync(uri: Uri): String =
    withContext(Dispatchers.IO) {
        val deferred = CompletableDeferred<String>()
        putFile(uri)
            .addOnSuccessListener {
                this@putAsync.downloadUrl.addOnSuccessListener { url ->
                    deferred.complete(url.toString())
                }.addOnFailureListener { err ->
                    deferred.completeExceptionally(err)
                }.addOnFailureListener {
                    deferred.completeExceptionally(it)
                }
            }
        deferred.await()
    }
