package com.example.andy.data.repository


import com.example.andy.data.models.WeatherResponse

interface WeatherRepository {
    /**
     * Retrieves the weather info for the given [lat] and [lon].
     * Returns [WeatherResponse] or null if the call fails.
     */
    suspend fun getWeather(lat: Double, lon: Double): WeatherResponse?
}