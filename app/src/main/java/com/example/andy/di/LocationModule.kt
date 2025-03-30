package com.example.andy.di

import android.content.Context
import com.example.andy.data.repository.LocationRepository
import com.example.andy.data.repository.impl.LocationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Singleton
    @Provides
    fun provideLocationRepository(@ApplicationContext context: Context): LocationRepository {
        return LocationRepositoryImpl(context)
    }
}
