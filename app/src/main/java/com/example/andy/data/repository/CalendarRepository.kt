package com.example.andy.data.repository

import com.example.andy.data.models.CalendarDate
import com.example.andy.data.models.CalendarEvent

interface CalendarRepository {
    fun getEventsForDate(date: String): List<CalendarEvent>
    fun addEvent(date: String, time: String, eventName: String, duration: Double)

    fun getNextEvent(timeStamp: String): CalendarEvent

    fun getDateComponents(timeStamp: String): CalendarDate
}