package com.example.andy.di

import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.example.andy.data.repository.ApiRepository
import com.example.andy.data.repository.GenAIRepository
import com.example.andy.data.repository.impl.GenAIRepositoryImpl
import com.example.andy.util.AndyGenAIConfig
import com.example.andy.util.ApiKeys
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
object GenAIModule {

    @Provides
    @Singleton
    fun provideGenAIConfig(): AndyGenAIConfig {
        return AndyGenAIConfig(
            gptToken = ApiKeys.GPT_API_KEY,
            gptCompletionApi = "https://api.openai.com/v1/chat/completions"
        )
    }

    @Provides
    @Singleton
    fun provideGenAIRepository(
        gson: Gson,
        genAIConfig: AndyGenAIConfig,
        apiRepository: ApiRepository,
    ): GenAIRepository {
        return GenAIRepositoryImpl(gson, genAIConfig, apiRepository)
    }
}
