package com.example.andy.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

object SchemaConfig {

    private val jsonParser = Json { ignoreUnknownKeys = true }

    val addPlaylistArgsSchema: JsonElement = jsonParser.parseToJsonElement("""
        {
          "type": "object",
          "properties": {
            "playlistName": {
              "type": "string",
              "description": "The name of the playlist."
            },
            "artist": {
              "type": "string",
              "description": "The artist of the track to be added."
            },
            "song": {
              "type": "string",
              "description": "The name of the song."
            }
          },
          "required": ["playlistName", "artist", "song"]
        }
    """.trimIndent())

    val getWeatherArgsSchema: JsonElement = jsonParser.parseToJsonElement("""
        {
          "type": "object",
          "properties": {
            "lat": {
              "type": "number",
              "description": "The latitude of the location in degrees."
            },
            "lon": {
              "type": "number",
              "description": "The longitude of the location in degrees."
            }
          },
          "required": ["lat", "lon"]
        }
    """.trimIndent())

    val launchAppArgsSchema: JsonElement = jsonParser.parseToJsonElement("""
        {
          "type": "object",
          "properties": {
            "packageName": {
              "type": "string",
              "description": "The package name of the app to launch."
            }
          },
          "required": ["packageName"]
        }
    """.trimIndent())

    val addEventArgsSchema: JsonElement = jsonParser.parseToJsonElement("""
        {
          "type": "object",
          "properties": {
            "date": {
              "type": "string",
              "description": "The date of the event (e.g., 2025-03-30)."
            },
            "time": {
              "type": "string",
              "description": "The time of the event (e.g., 14:30)."
            },
            "eventName": {
              "type": "string",
              "description": "A name or title for the event."
            },
            "duration": {
              "type": "number",
              "description": "The duration of the event, in hours."
            }
          },
          "required": ["date", "time", "eventName", "duration"]
        }
    """.trimIndent())
}
