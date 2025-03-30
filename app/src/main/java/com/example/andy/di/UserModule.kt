package com.example.andy.di

import android.content.Context
import com.example.andy.data.repository.UserRepository
import com.example.andy.data.repository.impl.UserRepositoryImpl
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    @Provides
    @Singleton
    fun provideUserService(
        @ApplicationContext context: Context,
        gson: Gson
    ): UserRepository {
        return UserRepositoryImpl(context, gson)
    }
}