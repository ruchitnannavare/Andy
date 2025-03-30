package com.example.andy.data.models.args

data class AddEventArgs(
    val date: String,        // e.g., "2025-03-30"
    val time: String,        // e.g., "14:30"
    val eventName: String,   // e.g., "Team Meeting"
    val duration: Double     // e.g., 1.5 (hours)
)