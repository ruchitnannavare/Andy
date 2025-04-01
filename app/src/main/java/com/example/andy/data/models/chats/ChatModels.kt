package com.example.andy.data.models.chats

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

data class LLMModel(val name: String, val model: String)

@OptIn(InternalSerializationApi::class)
@Serializable
data class Message(
    val role: String,               // "user", "assistant", or "system" (and "function" if needed)
    var content: String? = null,    // message text (null if this message is a function call)
    var tool_calls: List<ToolCall>? = null // for assistant messages that invoke a function
)
@OptIn(InternalSerializationApi::class)
@Serializable
data class ToolCall(
    val type: String,
    val function: FunctionCall,      // JSON string of arguments (the model outputs this as JSON text)
)

// Model for calling functions
@OptIn(InternalSerializationApi::class)
@Serializable
data class FunctionCall(
    val name: String,
    val arguments: String
)

// Model for registering functions
@OptIn(InternalSerializationApi::class)
@Serializable
data class Function(
    val name: String,
    val description: String,
    val parameters: JsonElement
)
@OptIn(InternalSerializationApi::class)
@Serializable
data class ToolSpec(
    val type: String,
    val function: Function,
)
@OptIn(InternalSerializationApi::class)
@Serializable
data class StructuredChatCompletionRequest(
    var model: String,                   // e.g. "gpt-4-0613"
    val messages: List<Message>,
    val temperature: Double? = null,
    val tools: List<ToolSpec>? = null,
)
@OptIn(InternalSerializationApi::class)
@Serializable
data class StructuredChatCompletionResponse(
    val id: String?,
    val model: String?,
    val choices: List<StructuredChatChoice>
)
@OptIn(InternalSerializationApi::class)
@Serializable
data class StructuredChatChoice(
    val message: Message,
    val index: Int? = null,
)
@OptIn(InternalSerializationApi::class)
@Serializable
data class ChatCompletionRequest(
    val model: String,                   // e.g. "gpt-4-0613"
    val messages: List<Message>,
    val temperature: Double? = null
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class ChatCompletionResponse(
    val id: String?,
    val model: String?,
    val choices: List<StructuredChatChoice>
)