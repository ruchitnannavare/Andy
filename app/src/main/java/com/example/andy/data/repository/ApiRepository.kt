package com.example.andy.data.repository

interface ApiRepository {
    /**
     * Performs a GET request to the given [url].
     * Optionally include a Bearer [token] for authorization.
     * Returns the response body as a [String] or null if no body.
     */
    suspend fun getJson(url: String, token: String? = null): String?

    /**
     * Performs a POST request to the given [url].
     * Optionally include a Bearer [token] for authorization.
     * Returns the response body as a [String] or null if no body.
     */
    suspend fun postJson(url: String, jsonBody: String, headers: Map<String, String> = emptyMap(), mediaTypeStr: String = "application/json; charset=utf-8"): String
}