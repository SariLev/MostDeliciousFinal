package com.example.mostdelicious.database.remote

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.mostdelicious.dto.PostDto
import com.example.mostdelicious.helpers.getAsync
import com.example.mostdelicious.helpers.listenToAll
import com.example.mostdelicious.helpers.putAsync
import com.example.mostdelicious.models.MealPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebasePostManager {

    companion object {
        const val DEFAULT_IMAGE =
            "https://png.pngtree.com/png-vector/20190820/ourmid/pngtree-no-image-vector-illustration-isolated-png-image_1694547.jpg"
        const val POSTS_COLLECTION_STORAGE_PATH = "post_images"
        const val POSTS_COLLECTION_DB_PATH = "posts"
    }

    suspend fun getAllPosts(): List<MealPost> = withContext(Dispatchers.IO) {
        return@withContext FirebaseFirestore.getInstance()
            .collection(POSTS_COLLECTION_DB_PATH)
            .getAsync<MealPost>()
    }

    fun listenToAllPosts(
        callback: (List<MealPost>) -> Unit,
    ): ListenerRegistration {
        return FirebaseFirestore.getInstance()
            .collection(POSTS_COLLECTION_DB_PATH)
            .listenToAll(callback)
    }


    private suspend fun uploadImage(
        docName: String,
        uri: Uri,
    ): String = withContext(Dispatchers.IO) {
        return@withContext FirebaseStorage.getInstance()
            .getReference(POSTS_COLLECTION_STORAGE_PATH)
            .child(docName)
            .putAsync(uri)
    }

    suspend fun createPost(form: PostDto): MealPost = withContext(Dispatchers.IO) {
        val deferred = CompletableDeferred<MealPost>()
        try {
            var url: String? = null

            if (form.id == null) { //  new post
                val newRef = FirebaseFirestore.getInstance()
                    .collection(POSTS_COLLECTION_DB_PATH)
                    .document()

                url = form.uri?.let { imageUri ->
                    uploadImage(newRef.id, imageUri)
                }

                val post = MealPost(
                    newRef.id,
                    image = url ?: DEFAULT_IMAGE,
                    name = form.mealName!!,
                    content = form.content!!,
                    postUserId = FirebaseAuth.getInstance().uid ?: ""
                )
                newRef.set(post)
                    .addOnSuccessListener {
                        deferred.complete(post)
                    }
                    .addOnFailureListener {
                        deferred.completeExceptionally(it)
                    }
            } else { // update existing post
                url = form.uri?.let { imageUri ->
                    uploadImage(form.id, imageUri)
                }
                val updateFields = mutableMapOf<String, Any>()
                form.content?.let {
                    updateFields["content"] = it
                }
                form.mealName?.let {
                    updateFields["mealName"] = it
                }
                url?.let {
                    updateFields["image"] = it
                }

                FirebaseFirestore.getInstance()
                    .collection(POSTS_COLLECTION_DB_PATH)
                    .document(form.id)
                    .update(updateFields)
                    .addOnSuccessListener {
                        FirebaseFirestore.getInstance()
                            .collection(POSTS_COLLECTION_DB_PATH)
                            .document(form.id)
                            .get()
                            .addOnSuccessListener {
                                it.toObject<MealPost>()?.let { meal ->
                                    deferred.complete(meal)
                                } ?: run {
                                    deferred.completeExceptionally(Exception("Unknown error occurred when updating post"))
                                }
                            }
                    }
                    .addOnFailureListener {
                        deferred.completeExceptionally(it)
                    }
            }

        } catch (e: Exception) {
            deferred.completeExceptionally(e)
        }
        deferred.await()
    }


}