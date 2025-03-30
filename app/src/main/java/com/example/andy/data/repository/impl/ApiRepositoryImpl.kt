package com.example.andy.data.repository.impl

import com.example.andy.data.repository.ApiRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class ApiRepositoryImpl @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) : ApiRepository {

    override suspend fun getJson(url: String, token: String?): String? {
        return withContext(Dispatchers.IO) {
            val requestBuilder = Request.Builder().url(url)
            if (!token.isNullOrBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            val request = requestBuilder.build()

            okHttpClient.newCall(request).execute().use { response ->
                response.body?.string()
            }
        }
    }

    override suspend fun postJson(url: String, jsonBody: String, headers: Map<String, String>, mediaTypeStr: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val mediaType = mediaTypeStr.toMediaType()
                val body = jsonBody.toRequestBody(mediaType)
                val requestBuilder = Request.Builder().url(url).post(body)
                headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }
                val request = requestBuilder.build()
                okHttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IllegalStateException("HTTP POST error: ${response.code}")
                    }
                    response.body?.string().orEmpty()
                }
            } catch (e: Exception) {
                val o = e
                TODO("Not yet implemented")
            }
        }
    }
}