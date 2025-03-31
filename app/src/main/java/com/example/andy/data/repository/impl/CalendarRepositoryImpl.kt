package com.example.andy.data.repository.impl

import android.content.Context
import com.example.andy.data.models.CalendarDate
import com.example.andy.data.models.CalendarEvent
import com.example.andy.data.models.SpotifyPlaylists
import com.example.andy.data.repository.CalendarRepository
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CalendarRepository {

    private val fileName = "calendar.csv"
    private val destinationFile by lazy { File(context.filesDir, fileName) }

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    init {
        // Copy the file from raw resources if it doesn't exist in the app's files directory
        initializeFileFromRaw()
    }

    /**
     * Initialize the file by copying from raw resources if it doesn't exist
     */
    private fun initializeFileFromRaw() {
        if (!destinationFile.exists()) {
            try {
                // Get the resource ID for the raw file
                val resourceId = context.resources.getIdentifier(
                    fileName.substringBeforeLast("."), // Get name without extension
                    "raw",
                    context.packageName
                )

                // Open the raw resource as an input stream
                val inputStream: InputStream = context.resources.openRawResource(resourceId)

                // Copy to the destination file
                inputStream.use { input ->
                    destinationFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                //TODO: Add file empty exception
            }
        }
    }

    override fun getEventsForDate(date: String): List<CalendarEvent> {
        val file = destinationFile
        if (!file.exists()) {
            // No CSV file yet
            return emptyList()
        }

        val lines = file.readLines()
        if (lines.size <= 1) {
            // Either empty or just the header
            return emptyList()
        }

        // Drop the header row: date,time,event,duration
        return lines.drop(1).mapNotNull { line ->
            val parts = line.split(",")
            if (parts.size < 4) null else {
                val eventDate = parts[0].trim()
                val time = parts[1].trim()
                val eventName = parts[2].trim()
                val duration = parts[3].trim().toDoubleOrNull() ?: 0.0

                if (eventDate == date) {
                    CalendarEvent(eventDate, time, eventName, duration)
                } else {
                    null
                }
            }
        }
    }

    /**
     * Appends a new event line to the CSV file.
     * If the file doesn't exist, creates it with a header row.
     */
    override fun addEvent(date: String, time: String, eventName: String, duration: Double) {
        val file = destinationFile

        // If file doesn't exist, create it and write a header
        if (!file.exists()) {
            file.writeText("date,time,event,duration\n")
        }

        // Append the new event line
        val newLine = "$date,$time,$eventName,$duration\n"
        file.appendText(newLine)
    }

    override fun getNextEvent(timestamp: String): CalendarEvent {
        val inputDateTime = LocalDateTime.parse(timestamp, formatter)

        val file = destinationFile
        if (!file.exists()) return CalendarEvent("", "", "nothing", 0.0)

        val lines = file.readLines()
        if (lines.size <= 1) return CalendarEvent("", "", "nothing", 0.0)

        // Read each line after header, combine date and time, and parse as LocalDateTime
        val upcomingEvents = lines.drop(1).mapNotNull { line ->
            val parts = line.split(",")
            if (parts.size < 4) null else {
                try {
                    val eventDate = parts[0].trim()
                    val eventTime = parts[1].trim()
                    val eventDateTime = LocalDateTime.parse("$eventDate $eventTime", formatter)
                    Triple(eventDateTime, parts, CalendarEvent(eventDate, eventTime, parts[2].trim(), parts[3].trim().toDoubleOrNull() ?: 0.0))
                } catch (e: Exception) {
                    null
                }
            }
        }.filter { (eventDateTime, _, _) ->
            eventDateTime.isAfter(inputDateTime)
        }

        return if (upcomingEvents.isEmpty()) {
            CalendarEvent("", "", "nothing", 0.0)
        } else {
            // Get the event with the earliest timestamp after the given time
            upcomingEvents.minByOrNull { it.first }!!.third
        }
    }

    /**
     * Extracts day, month, and year from the full timestamp "yyyy-MM-dd HH:mm:ss".
     */
    override fun getDateComponents(timestamp: String): CalendarDate {
        return try {
            // Parse the timestamp into a LocalDateTime object.
            val dateTime = LocalDateTime.parse(timestamp, formatter)

            // Extract the day of the month.
            val dayOfMonth = dateTime.dayOfMonth

            // Get the full month name in lowercase.
            val monthName = dateTime.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).lowercase()

            // Extract the year.
            val year = dateTime.year

            // Get the full name of the day of the week in lowercase.
            val dayName = dateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH).lowercase()

            CalendarDate(date = dayOfMonth, month = monthName, year = year, day = dayName)
        } catch (e: Exception) {
            // In case of parsing error, return a default value.
            CalendarDate(date = 0, month = "", year = 0, day = "")
        }
    }
}