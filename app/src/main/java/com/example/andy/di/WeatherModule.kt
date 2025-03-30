package com.example.andy.di

import com.example.andy.data.repository.WeatherRepository
import com.example.andy.data.repository.impl.WeatherRepositoryImpl
import com.example.andy.data.repository.ApiRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(
        apiRepository: ApiRepository,
        gson: Gson
    ): WeatherRepository {
        return WeatherRepositoryImpl(apiRepository, gson)
    }
}