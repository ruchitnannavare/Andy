package com.example.andy.data.models

// UserProfile.kt
data class UserProfile(
    val name: String,
    val age: Int,
    val gender: String,
    val profession: String,
    val email: String,
    val location: Location,
    val previous_notifications: List<Notification>,
    val app_usage: AppUsage,
    val contacts: List<Contact>,
    val purchases: List<Purchase>,
    val fitness_data: FitnessData
)

data class Location(
    val home: Place,
    val work: Place
)

data class Place(
    val city: String,
    val country: String,
    val coordinates: Coordinates
)

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)

data class Notification(
    val date: String,
    val time: String,
    val message: String
)

data class AppUsage(
    val most_used_apps: List<App>,
    val last_opened_app: String,
    val screen_time: String
)

data class App(
    val name: String,
    val usage_time: String
)

data class Contact(
    val name: String,
    val phone: String,
    val email: String
)

data class Purchase(
    val item: String,
    val date: String,
    val price: String,
    val store: String
)

data class FitnessData(
    val steps_today: Int,
    val average_daily_steps: Int,
    val last_workout: LastWorkout,
    val sleep: Sleep,
    val weekly_distance_km: Int,
    val calories_burned_today: Int
)

data class LastWorkout(
    val type: String,
    val distance_km: Int,
    val duration_minutes: Int
)

data class Sleep(
    val last_night: String,
    val average: String
)
