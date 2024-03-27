package com.example.mostdelicious.helpers

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.example.mostdelicious.database.remote.FirebaseUserManager
import com.example.mostdelicious.models.OtherUser
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


val json = Json {
    ignoreUnknownKeys = true
}

// LifeCycle
inline fun <reified T : FragmentActivity> Fragment.typedActivity(): T {
    return requireActivity() as T
}


fun Long.toDateString(
    format: String = "yyyy-MM-dd HH:mm:ss",
    locale: Locale = Locale.getDefault(),
): String {
    val formatter = SimpleDateFormat(format, locale)
    val date = Date(this)
    return formatter.format(date)
}

inline fun <reified T : ViewBinding> Fragment.viewBinding(
    noinline bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> T = { inflater, _, _ ->
        val method = T::class.java.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        method.invoke(null, inflater, null, false) as T
    },
): AutoClearedViewBinding<T> = AutoClearedViewBinding(this, bindingInflater)


// Firebase
suspend inline fun <reified T> DocumentReference.getAsync(): T? =
    withContext(Dispatchers.IO) {
        val deferred = CompletableDeferred<T?>()
        this@getAsync.get()
            .addOnSuccessListener { documentSnapshot ->
                deferred.complete(documentSnapshot.toObject(T::class.java)) // Complete with the result
            }
            .addOnFailureListener { exception ->
                deferred.completeExceptionally(exception) // Complete with exception on failure
            }
        deferred.await() // Await and return the result
    }

suspend inline fun <T : Any> DocumentReference.setAsync(data: T) =
    withContext(Dispatchers.IO) {
        val deferred = CompletableDeferred<Void?>()
        this@setAsync.set(data)
            .addOnSuccessListener {
                deferred.complete(null) // Complete with the result
            }
            .addOnFailureListener { exception ->
                deferred.completeExceptionally(exception) // Complete with exception on failure
            }
        deferred.await() // Await and return the result
    }

suspend inline fun <reified T> Query.getAsync(): List<T> =
    withContext(Dispatchers.IO) {
        val deferred = CompletableDeferred<List<T>>() // Create a CompletableDeferred
        this@getAsync.get()
            .addOnSuccessListener { documentSnapshot ->
                deferred.complete(documentSnapshot.toObjects(T::class.java)) // Complete with the result
            }
            .addOnFailureListener { exception ->
                deferred.completeExceptionally(exception) // Complete with exception on failure
            }
        deferred.await() // Await and return the result
    }

inline fun <reified T> CollectionReference.listenToAll(

    crossinline callback: (List<T>) -> Unit,
): ListenerRegistration {
    return addSnapshotListener { snap, _ ->
        snap?.let {
            val posts = it.toObjects(T::class.java)
            callback.invoke(posts)
        }
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

// UI
fun TextInputEditText.attachErrorWatcher(layout: TextInputLayout) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            layout.error = null
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    })
}


// Live Data

fun <T> LiveData<T?>.observeNotNull(owner: LifecycleOwner, observer: Observer<in T>) {
    observe(owner) {
        if (it == null) return@observe
        observer.onChanged(it)
    }
}

fun <T> LiveData<T?>.observeOnlyNull(owner: LifecycleOwner, observer: Observer<in T?>) {
    observe(owner) {
        if (it != null) return@observe
        observer.onChanged(null)
    }
}


// Etc

const val EMPTY_VALUE = ""