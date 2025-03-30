package com.example.andy.di

import com.example.andy.data.repository.ApiRepository
import com.example.andy.data.repository.impl.ApiRepositoryImpl
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideApiRepository(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): ApiRepository {
        return ApiRepositoryImpl(okHttpClient, gson)
    }
}