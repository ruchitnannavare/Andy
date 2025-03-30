package com.example.andy.di

import android.content.Context
import com.example.andy.data.repository.ApiRepository
import com.example.andy.data.repository.SpotifyRepository
import com.example.andy.data.repository.impl.SpotifyRepositoryImpl
import com.example.andy.util.ApiKeys
import com.example.andy.util.SpotifyClientConfig
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpotifyModule {

    @Provides
    @Singleton
    fun provideSpotifyConfig(): SpotifyClientConfig {
        return SpotifyClientConfig(
            clientId = ApiKeys.SPOTIFY_CLIENT_ID,
            clientSecret = ApiKeys.SPOTIFY_CLIENT_SECRET
        )
    }

    @Provides
    @Singleton
    fun provideSpotifyRepository(
        @ApplicationContext context: Context,
        gson: Gson,
        apiRepository: ApiRepository,
        spotifyConfig : SpotifyClientConfig
    ): SpotifyRepository {
        return SpotifyRepositoryImpl(context,
            gson = gson,
            apiRepository = apiRepository,
            spotify = spotifyConfig)
    }
}