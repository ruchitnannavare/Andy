package com.example.andy.data.repository.impl


import com.example.andy.data.models.Hourly
import com.example.andy.data.models.WeatherResponse
import com.example.andy.data.repository.ApiRepository
import com.example.andy.data.repository.WeatherRepository
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiRepository: ApiRepository,
    private val gson: Gson
) : WeatherRepository {

    override suspend fun getWeather(lat: Double, lon: Double): WeatherResponse? {
        val url = "https://api.open-meteo.com/v1/forecast" +
                "?latitude=$lat&longitude=$lon&hourly=rain,showers,snowfall,precipitation,uv_index,temperature_2m"

        // Make the network call (no token needed)
        val currentWeather = getNextHourWeather(gson.fromJson(apiRepository.getJson(url) ?: return null, WeatherResponse::class.java))

        // Parse JSON into our WeatherResponse model
        return currentWeather
    }

    private fun getNextHourWeather(weatherResponse: WeatherResponse): WeatherResponse {
        // Get current time in GMT (UTC)
        val nowUtc = LocalDateTime.now(ZoneOffset.UTC)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

        // Consider only the first 24 entries (today's forecast)
        val timesToday = weatherResponse.hourly.time.take(24)

        // Find the first forecast time that is after now
        val nextIndex = timesToday.indexOfFirst { timeStr ->
            val forecastTime = LocalDateTime.parse(timeStr, formatter)
            forecastTime.isAfter(nowUtc)
        }

        // If no future time is found (for example, if the forecast has already passed), use the last available entry
        val index = if (nextIndex >= 0) nextIndex else 23

        // Create a new Hourly object that contains only one element for each forecast field
        val newHourly = Hourly(
            time = listOf(weatherResponse.hourly.time[index]),
            rain = listOf(weatherResponse.hourly.rain[index]),
            showers = listOf(weatherResponse.hourly.showers[index]),
            snowfall = listOf(weatherResponse.hourly.snowfall[index]),
            precipitation = listOf(weatherResponse.hourly.precipitation[index]),
            uv_index = listOf(weatherResponse.hourly.uv_index[index]),
            temperature_2m = listOf(weatherResponse.hourly.temperature_2m[index])
        )

        // Return a new WeatherResponse with the updated hourly forecast.
        return weatherResponse.copy(hourly = newHourly)
    }
}