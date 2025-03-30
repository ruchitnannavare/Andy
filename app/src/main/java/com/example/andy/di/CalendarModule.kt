package com.example.andy.di

import android.content.Context
import com.example.andy.data.repository.CalendarRepository
import com.example.andy.data.repository.impl.CalendarRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CalendarModule {

    @Provides
    @Singleton
    fun provideCalendarService(
        @ApplicationContext context: Context
    ): CalendarRepository {
        return CalendarRepositoryImpl(context)
    }
}