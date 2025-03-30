package com.example.andy.data.models

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Int,
    val hourly_units: HourlyUnits,
    val hourly: Hourly
)

data class HourlyUnits(
    val time: String,
    val rain: String,
    val temperature_2m: String,
    val showers: String,
    val snowfall: String,
    val precipitation: String,
    val uv_index: String
)

data class Hourly(
    val time: List<String>,
    val rain: List<Double>,
    val showers: List<Double>,
    val snowfall: List<Double>,
    val precipitation: List<Double>,
    val uv_index: List<Double>,
    val temperature_2m: List<Double>
)
