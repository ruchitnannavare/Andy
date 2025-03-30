package com.example.andy.di

import android.content.Context
import com.example.andy.data.repository.SocialsRepository
import com.example.andy.data.repository.impl.SocialsRepositoryImpl
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SocialsModule {

    @Provides
    @Singleton
    fun provideSocialsRepository(
        @ApplicationContext context: Context,
        gson: Gson
    ): SocialsRepository {
        return SocialsRepositoryImpl(context, gson)
    }
}