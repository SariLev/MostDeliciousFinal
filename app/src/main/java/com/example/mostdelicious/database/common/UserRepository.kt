package com.example.mostdelicious.database.common

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.example.mostdelicious.database.local.AppLocalDB
import com.example.mostdelicious.database.remote.FirebaseUserManager
import com.example.mostdelicious.dto.UserRegisterForm
import com.example.mostdelicious.helpers.FirebaseLiveData
import com.example.mostdelicious.helpers.nullIfEmpty
import com.example.mostdelicious.models.OtherUser
import com.example.mostdelicious.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class UserRepository(
    private val localDatabase: AppLocalDB,
    private val remoteDatabase: FirebaseUserManager,
    private val sp: SharedPreferences,
) {

    companion object {
        const val USER_LAST_UPDATE_KEY = "user_last_update"
    }

    suspend fun getCurrentUser(): User? {
        return localDatabase.userDao().getUser()
    }

    fun listenCurrentUser(): LiveData<User?> {
        return localDatabase.userDao().listenUser()
    }

    fun listenAllUsers(
        coroutineScope: CoroutineScope,
    ): FirebaseLiveData<List<OtherUser>?> {
        val lastUpdated = sp.getLong(USER_LAST_UPDATE_KEY, 0)
        return FirebaseLiveData(
            remoteDatabase.listenToAllUsers(lastUpdated) { users ->
                users?.let {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            cacheUsersLocally(users)
                            sp.edit()
                                .putLong(USER_LAST_UPDATE_KEY, System.currentTimeMillis())
                                .apply()
                        }
                    }
                }
            },
            localDatabase.otherUserDao()
                .listenAll()
                .nullIfEmpty()
        )
    }


    suspend fun cacheUserLocally(user: User) = withContext(Dispatchers.IO) {
        localDatabase.userDao().insert(user)
        localDatabase.otherUserDao().insert(OtherUser(user.id, user.email, user.name, user.favoriteMeals, user.ratedPosts))
    }

    private suspend fun cacheUsersLocally(users: List<OtherUser>) = withContext(Dispatchers.IO) {
        localDatabase.otherUserDao().insert(users)
    }

    @Throws(Exception::class)
    fun likePostToggle(
        coroutineScope: CoroutineScope,
        currentUser: User,
        postId: String,
    ) {
        val index = currentUser.favoriteMeals.indexOf(postId)
        if (index != -1) {
            currentUser.favoriteMeals.removeAt(index)
        } else {
            currentUser.favoriteMeals.add(postId)
        }

        coroutineScope.launch {
            remoteDatabase.saveUser(currentUser)
        }
    }


    @Throws(Exception::class)
    suspend fun createUser(form: UserRegisterForm): User = withContext(Dispatchers.IO) {
        localDatabase.userDao().deleteCurrentUser()
        remoteDatabase.createUser(form)
    }

    @Throws(Exception::class)
    suspend fun signIn(email: String, password: String) = withContext(Dispatchers.IO) {
        localDatabase.userDao().deleteCurrentUser()
        remoteDatabase.signIn(email, password)
    }

    suspend fun logOut() = withContext(Dispatchers.IO) {
        localDatabase.userDao().deleteCurrentUser() // (forget)
        FirebaseAuth.getInstance().signOut()
    }

    suspend fun saveUser(user: User) = withContext(Dispatchers.IO) {
        remoteDatabase.saveUser(user)
    }

}