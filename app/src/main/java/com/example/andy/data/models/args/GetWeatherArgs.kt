package com.example.andy.data.models.args

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class GetWeatherArgs(
    val lat: Double,
    val lon: Double
)