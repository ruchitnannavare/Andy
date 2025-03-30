package com.example.andy.data.repository.impl

import com.example.andy.data.models.chats.ChatCompletionRequest
import com.example.andy.data.models.chats.ChatCompletionResponse
import com.example.andy.data.models.chats.StructuredChatChoice
import com.example.andy.data.models.chats.StructuredChatCompletionRequest
import com.example.andy.data.models.chats.StructuredChatCompletionResponse
import com.example.andy.data.repository.ApiRepository
import com.example.andy.data.repository.GenAIRepository
import com.example.andy.util.AndyGenAIConfig
import com.google.gson.Gson
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.jvm.java

class GenAIRepositoryImpl @Inject constructor(
    private val gson: Gson,
    private val genAI: AndyGenAIConfig,
    private val apiRepository: ApiRepository
) : GenAIRepository {

    override suspend fun chatCompletion(chat: ChatCompletionRequest): String {

        val jsonBody = gson.toJson(chat)
        val headers = mapOf(
            "Content-Type" to "application/json",
            "Authorization" to "Bearer ${genAI.gptToken}"
        )
        val responseJson = apiRepository.postJson(genAI.gptCompletionApi, jsonBody, headers)
        val response = gson.fromJson(responseJson, ChatCompletionResponse::class.java)
        // Return the first assistant reply as plain text.
        return response.choices.firstOrNull()?.message?.content ?: ""
    }
    override suspend fun structuredCompletion(request: StructuredChatCompletionRequest): StructuredChatChoice? {
        try {
            val requestJson = Json.encodeToString(request)

            // 3. Call OpenAI ChatCompletion endpoint via ApiRepository
            val responseJson = apiRepository.postJson(
                url = genAI.gptCompletionApi,
                jsonBody = requestJson,
                headers = mapOf("Authorization" to "Bearer ${genAI.gptToken}")
            )
            // Decode the JSON response to our response model
            val responseObj = gson.fromJson(responseJson, StructuredChatCompletionResponse::class.java)

            // 4. If the assistant called a function, parse the arguments into T
            val choice = responseObj.choices.firstOrNull()

            // 5. If no function call, just return the response as-is (with result remaining null)
            return choice
        } catch (e: Exception) {
            val o = e
            TODO("Not yet implemented")
        }

    }
}
