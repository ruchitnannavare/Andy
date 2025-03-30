package com.example.andy.data.repository.impl

import android.content.Context
import com.example.andy.data.repository.FileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FileRepository {

    /**
     * Ensures a file exists in the app's file directory
     * If the file doesn't exist, it attempts to create it from raw resources
     */
    override fun ensureFileExists(fileName: String): File {
        val destinationFile = File(context.filesDir, fileName)

        if (!destinationFile.exists()) {
            updateFileDir(fileName)
        }

        return destinationFile
    }

    /**
     * Creates a file in the app's file directory from raw resources if it doesn't exist
     * @param fileName Name of the file to create
     */
    fun updateFileDir(fileName: String) {
        val destinationFile = File(context.filesDir, fileName)

        try {
            // Get the resource ID for the raw file
            val resourceName = fileName.substringBeforeLast(".")
            val resourceId = context.resources.getIdentifier(
                resourceName,
                "raw",
                context.packageName
            )

            if (resourceId != 0) {
                // Resource exists, copy it to files directory
                context.resources.openRawResource(resourceId).use { inputStream ->
                    destinationFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            } else {
                // Resource doesn't exist, create an empty file
                destinationFile.createNewFile()
            }
        } catch (e: IOException) {
            // Create an empty file if there's an error
            try {
                destinationFile.createNewFile()
            } catch (e: IOException) {
                // Handle error appropriately for your app
                e.printStackTrace()
            }
        }
    }

    /**
     * Reads a file's content as a string
     */
    override fun readFileAsString(fileName: String): String? {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            try {
                file.readText()
            } catch (e: IOException) {
                null
            }
        } else {
            null
        }
    }

    /**
     * Writes a string to a file
     */
    override fun writeStringToFile(fileName: String, content: String): Boolean {
        val file = File(context.filesDir, fileName)
        return try {
            file.writeText(content)
            true
        } catch (e: IOException) {
            false
        }
    }
}