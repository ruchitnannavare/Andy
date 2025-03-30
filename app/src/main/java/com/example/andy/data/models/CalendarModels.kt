package com.example.andy.data.models

data class CalendarEvent(
    val date: String,
    val time: String,
    val event: String,
    val duration: Double
)

data class CalendarDate(
    val day: Int,
    val month: Int,
    val year: Int
)