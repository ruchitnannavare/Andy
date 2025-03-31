package com.example.andy.data.models

data class CalendarEvent(
    val date: String,
    val time: String,
    val event: String,
    val duration: Double
)

data class CalendarDate(
    val date: Int,     // day of month, e.g., 13
    val month: String, // month name, e.g., "march"
    val year: Int,     // year, e.g., 2025
    val day: String    // day of week, e.g., "monday"
)