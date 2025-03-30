package com.example.andy.data.repository


import com.example.andy.data.models.LocationPoint

interface LocationRepository {
    /**
     * Retrieves the latest location point from the CSV file.
     * Returns null if the file doesn't exist or if there is no valid data.
     */
    fun getLatestLocation(): LocationPoint?
}