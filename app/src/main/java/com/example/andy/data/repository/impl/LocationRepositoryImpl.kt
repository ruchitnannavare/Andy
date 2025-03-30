package com.example.andy.data.repository.impl

import android.content.Context
import com.example.andy.data.models.LocationPoint
import com.example.andy.data.repository.LocationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.InputStream
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationRepository {

    private val fileName = "location.csv"
    private val destinationFile by lazy { File(context.filesDir, fileName) }

    init {
        // Copy the CSV from raw resources if not already in the app's files directory
        initializeFileFromRaw()
    }

    /**
     * Initializes the CSV file by copying it from raw resources if it doesn't exist.
     */
    private fun initializeFileFromRaw() {
        if (!destinationFile.exists()) {
            try {
                val resourceId = context.resources.getIdentifier(
                    fileName.substringBeforeLast("."), // remove extension for resource lookup
                    "raw",
                    context.packageName
                )
                val inputStream: InputStream = context.resources.openRawResource(resourceId)
                inputStream.use { input ->
                    destinationFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                // TODO: Consider proper error handling or logging
            }
        }
    }

    /**
     * Reads the CSV file and returns the latest location point.
     * Assumes the CSV is sorted in ascending order by timestamp.
     */
    override fun getLatestLocation(): LocationPoint? {
        if (!destinationFile.exists()) return null

        val lines = destinationFile.readLines()
        if (lines.size <= 1) return null  // file is empty or contains only the header

        // Drop the header row and get the last non-empty line
        val dataLines = lines.drop(1).filter { it.isNotBlank() }
        val lastLine = dataLines.lastOrNull() ?: return null

        val parts = lastLine.split(",")
        if (parts.size < 4) return null

        val timestamp = parts[0].trim()
        val latitude = parts[1].trim().toDoubleOrNull() ?: 0.0
        val longitude = parts[2].trim().toDoubleOrNull() ?: 0.0
        val location = parts[3].trim()

        return LocationPoint(timestamp, latitude, longitude, location)
    }
}
