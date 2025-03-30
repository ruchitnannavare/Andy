package com.example.andy.data.models

data class LocationPoint(
    val timestamp: String,
    val latitude: Double,
    val longitude: Double,
    val location: String
)

data class StoredLocation(
    val placeCity: String,
    val placeName: String,
    val placeCountry: String
)
