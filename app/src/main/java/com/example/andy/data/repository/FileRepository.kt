package com.example.andy.data.repository

import java.io.File
import java.io.InputStream

/**
 * Repository interface for file operations
 */
interface FileRepository {
    /**
     * Ensures a file exists in the app's file directory, creating it from raw resources if needed
     * @param fileName Name of the file to check/create
     * @return The file reference
     */
    fun ensureFileExists(fileName: String): File

    /**
     * Reads the content of a file as a string
     * @param fileName Name of the file to read
     * @return The file content as a string, or null if the file doesn't exist or can't be read
     */
    fun readFileAsString(fileName: String): String?

    /**
     * Writes a string to a file
     * @param fileName Name of the file to write to
     * @param content Content to write
     * @return True if the write was successful, false otherwise
     */
    fun writeStringToFile(fileName: String, content: String): Boolean
}