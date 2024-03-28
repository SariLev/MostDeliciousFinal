package com.example.mostdelicious.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.mostdelicious.database.common.PostRepository
import com.example.mostdelicious.database.common.UserRepository
import com.example.mostdelicious.database.local.AppLocalDB
import com.example.mostdelicious.database.remote.FirebasePostManager
import com.example.mostdelicious.database.remote.FirebaseUserManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class SharedModule {
    @Singleton
    @Provides
    fun provideLocalDB(@ApplicationContext context: Context): AppLocalDB {
        return AppLocalDB.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideFirebaseUserManager(): FirebaseUserManager {
        return FirebaseUserManager()
    }

    @Singleton
    @Provides
    fun provideFirebasePostManager(): FirebasePostManager {
        return FirebasePostManager()
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("com.example.mostdelicious", MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        localDatabase: AppLocalDB,
        remoteDatabase: FirebaseUserManager,
        sharedPreferences: SharedPreferences
    ): UserRepository {
        return UserRepository(localDatabase, remoteDatabase, sharedPreferences)
    }


    @Singleton
    @Provides
    fun providePostRepository(
        localDatabase: AppLocalDB,
        remoteDatabase: FirebasePostManager,
        sharedPreferences: SharedPreferences
    ): PostRepository {
        return PostRepository(localDatabase, remoteDatabase, sharedPreferences)
    }
}