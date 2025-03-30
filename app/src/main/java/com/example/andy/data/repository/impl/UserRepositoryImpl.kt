package com.example.andy.data.repository.impl

import android.content.Context
import com.example.andy.data.models.UserProfile
import com.example.andy.data.repository.UserRepository
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : UserRepository {

    private val fileName = "user_profile.json"
    private val destinationFile by lazy { File(context.filesDir, fileName) }

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

    override fun getUserRepository(): UserProfile {
        // The file should have been copied to context.filesDir (e.g., during first launch)
        val file = destinationFile
        if (!file.exists()) {
            throw FileNotFoundException("user_profile.json not found in filesDir")
        }
        val jsonString = file.readText()
        return gson.fromJson(jsonString, UserProfile::class.java)
    }
}