package com.example.andy.data.repository

import com.example.andy.data.models.chats.ChatCompletionRequest
import com.example.andy.data.models.chats.StructuredChatChoice
import com.example.andy.data.models.chats.StructuredChatCompletionRequest


interface GenAIRepository {
    suspend fun chatCompletion(model: String, query: String): String
    suspend fun structuredCompletion(request: StructuredChatCompletionRequest): StructuredChatChoice?
}
