package com.example.mostdelicious.helpers

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.serialization.json.Json


val json = Json {
    ignoreUnknownKeys = true
}

class FirebaseLiveData<T>(
    private val registration: ListenerRegistration,
    private val liveData: LiveData<T>,
) {
    fun stopListening() {
        registration.remove()
    }

    fun get(): LiveData<T> {
        return liveData
    }
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

fun <T> LiveData<List<T>>.nullIfEmpty(): LiveData<List<T>?> {
    return map { userList ->
        userList.takeIf { it.isNotEmpty() }
    }
}
