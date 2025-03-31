package com.example.andy.data.models.args

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class LaunchAppArgs(
    val packageName: String
)