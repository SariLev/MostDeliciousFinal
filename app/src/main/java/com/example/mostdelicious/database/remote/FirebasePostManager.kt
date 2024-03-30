package com.example.mostdelicious.database.remote

import android.net.Uri
import com.example.mostdelicious.dto.PostDto
import com.example.mostdelicious.helpers.extensions.LAST_UPDATED_KEY
import com.example.mostdelicious.helpers.extensions.listenToAll
import com.example.mostdelicious.helpers.extensions.putAsync
import com.example.mostdelicious.models.MealPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebasePostManager @Inject constructor() {

    companion object {
        const val DEFAULT_IMAGE =
            "https://png.pngtree.com/png-vector/20190820/ourmid/pngtree-no-image-vector-illustration-isolated-png-image_1694547.jpg"
        const val POSTS_COLLECTION_STORAGE_PATH = "post_images"
        const val POSTS_COLLECTION_DB_PATH = "posts"
    }

    fun listenToAllPosts(
        lastUpdated: Long,
        callback: (List<MealPost>?) -> Unit,
    ): ListenerRegistration {

        return FirebaseFirestore.getInstance()
            .collection(POSTS_COLLECTION_DB_PATH)
            .whereGreaterThanOrEqualTo(LAST_UPDATED_KEY, lastUpdated - 10 * 1000 * 60)
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


    suspend fun ratePost(post: MealPost, rating: Float, callback: (MealPost) -> Unit): MealPost =
        withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<MealPost>()
            val userId = FirebaseAuth.getInstance().uid ?: return@withContext post
            val currentAverageRating = post.averageRating

            val ratingUsers = post.ratingUsers

            val sumAllRatings = currentAverageRating * ratingUsers.size

            ratingUsers.add(userId)

            val newRatingAverage = (sumAllRatings + rating) / (ratingUsers.size)

            val updateFields = mutableMapOf<String, Any>()
            updateFields[LAST_UPDATED_KEY] = System.currentTimeMillis()
            post.lastUpdated = System.currentTimeMillis()
            post.averageRating = newRatingAverage
            updateFields[MealPost.AVERAGE_RATING_KEY] = newRatingAverage
            updateFields[MealPost.RATING_USERS_KEY] = ratingUsers

            FirebaseFirestore.getInstance()
                .collection(POSTS_COLLECTION_DB_PATH)
                .document(post.id)
                .update(updateFields)
                .addOnSuccessListener {
                    deferred.complete(post)
                    callback(post)
                }
                .addOnFailureListener {
                    deferred.completeExceptionally(it)
                }

            deferred.await()
        }

    suspend fun createUpdatePost(form: PostDto): MealPost = withContext(Dispatchers.IO) {
        val deferred = CompletableDeferred<MealPost>()
        try {
            val url: String?
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
                updateFields[LAST_UPDATED_KEY] = System.currentTimeMillis()
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