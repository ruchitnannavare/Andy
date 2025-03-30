package com.example.andy.data.repository.impl

import android.content.Context
import com.example.andy.data.models.SocialMedia
import com.example.andy.data.repository.SocialsRepository
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import javax.inject.Inject

class SocialsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : SocialsRepository {

    private val fileName = "social_media.json"
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
    override fun getSocials(): SocialMedia {
        val file = destinationFile
        if (!file.exists()) {
            throw FileNotFoundException("social_media.json not found in filesDir")
        }
        val jsonString = file.readText()
        return gson.fromJson(jsonString, SocialMedia::class.java)
    }
}
