package com.example.mostdelicious.database.common

import androidx.lifecycle.LiveData
import com.example.mostdelicious.database.local.AppLocalDB
import com.example.mostdelicious.database.remote.FirebaseUserManager
import com.example.mostdelicious.dto.UserRegisterForm
import com.example.mostdelicious.helpers.listenToAll
import com.example.mostdelicious.models.MealPost
import com.example.mostdelicious.models.OtherUser
import com.example.mostdelicious.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class UserRepository(
    private val localDatabase: AppLocalDB,
    private val remoteDatabase: FirebaseUserManager,
) {

    fun getCurrentUser(): LiveData<User?> {
        return localDatabase.userDao().listenUser()
    }

    fun listenAllUsers(
        coroutineScope: CoroutineScope,
    ): Pair<ListenerRegistration, LiveData<List<OtherUser>>> {
        val listener = remoteDatabase.listenToAllUsers { users ->
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    localDatabase.otherUserDao().deleteAllExcept(users.map(OtherUser::id))
                    localDatabase.userDao().deleteAllExcept(users.map(OtherUser::id))
                    localDatabase.otherUserDao().insert(users)
                }
            }
        }
        return Pair(
            listener,
            localDatabase.otherUserDao().listenAll()
        )
    }

    suspend fun cacheUserLocally(user: User) = withContext(Dispatchers.IO) {
        localDatabase.userDao().insert(user)
        localDatabase.otherUserDao().insert(OtherUser(user.id, user.email, user.name))
    }

    @Throws(Exception::class)
    suspend fun createUser(form: UserRegisterForm): User {
        return remoteDatabase.createUser(form)
    }

    @Throws(Exception::class)
    suspend fun signIn(email: String, password: String) {
        remoteDatabase.signIn(email, password)
    }

}