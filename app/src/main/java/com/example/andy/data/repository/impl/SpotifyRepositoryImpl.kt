package com.example.andy.data.repository.impl

import android.content.Context
import com.example.andy.data.repository.SpotifyRepository
import com.example.andy.data.models.Playlist
import com.example.andy.data.models.PlaylistData
import com.example.andy.data.models.SpotifyAccessTokenResponse
import com.example.andy.data.models.SpotifyPlaylists
import com.example.andy.data.models.Track
import com.example.andy.data.models.TrackDetails
import com.example.andy.data.models.responses.SpotifySearchResponse
import com.example.andy.data.repository.ApiRepository
import com.example.andy.util.SpotifyClientConfig
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import javax.inject.Inject

class SpotifyRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
    private val apiRepository: ApiRepository,
    private val spotify: SpotifyClientConfig
) : SpotifyRepository {

    // TypeToken to help Gson parse the top-level JSON object
    private val fileName = "spotify_playlists.json"
    private val destinationFile by lazy { File(context.filesDir, fileName) }
    private val spotifyAccessToken: String =  runBlocking { getSpotifyAccessToken() } ?: ""

    init {
        // Copy the file from raw resources if it doesn't exist in the app's files directory
        initializeFileFromRaw()
    }

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
                // If there's an error, create an empty file structure
                val emptyPlaylists = SpotifyPlaylists(mutableListOf())
                val emptyJson = gson.toJson(emptyPlaylists, SpotifyPlaylists::class.java)
                destinationFile.writeText(emptyJson)
            }
        }
    }

    override fun getPlayList(playListName: String): Playlist {
        val jsonString = destinationFile.readText()
        val spotifyPlaylists: SpotifyPlaylists = Json.decodeFromString(jsonString)
//        spotifyPlaylists.playlists.map { playlistData ->
//            val trackObjects = playlistData.tracks.map { trackString ->
//                val parts = trackString.split(" - ")
//                val artist = parts.getOrNull(0)?.trim().orEmpty()
//                val song = parts.getOrNull(1)?.trim().orEmpty()
//                Track(artist, song)
//            }
//            Playlist(playlistData.name, trackObjects)
//        }

        return spotifyPlaylists.playlists
            .first { it.name.equals(playListName, ignoreCase = true) }
            .let { playlistData ->
                val trackObjects = playlistData.tracks.map { trackString ->
                    val parts = trackString.split(" - ")
                    val artist = parts.getOrNull(0)?.trim().orEmpty()
                    val song = parts.getOrNull(1)?.trim().orEmpty()
                    Track(artist, song)
                }
                Playlist(playlistData.name, trackObjects)
            }
    }

    /**
     * Adds a new playlist or adds a track to an existing playlist.
     * If the file doesn't exist yet, it will be created.
     */
    override fun addPlayList(playlistName: String, artist: String, song: String) {
        // Ensure the file exists by initializing it if needed
        initializeFileFromRaw()

        val spotifyPlaylists: SpotifyPlaylists = if (destinationFile.exists()) {
            val jsonString = destinationFile.readText()
            gson.fromJson(jsonString, SpotifyPlaylists::class.java)
        } else {
            SpotifyPlaylists(mutableListOf())
        }

        // Find existing playlist by name (case-insensitive)
        val existingPlaylist = spotifyPlaylists.playlists.find {
            it.name.equals(playlistName, ignoreCase = true)
        }

        val newTrackString = "$artist - $song"

        // If playlist exists, add the track if it's not already there
        if (existingPlaylist != null) {
            if (!existingPlaylist.tracks.contains(newTrackString)) {
                existingPlaylist.tracks.add(newTrackString)
            }
        } else {
            // Otherwise, create a new playlist
            val newPlaylistData = PlaylistData(
                name = playlistName,
                tracks = mutableListOf(newTrackString)
            )
            spotifyPlaylists.playlists.add(newPlaylistData)
        }

        // Write updated data back to JSON
        val updatedJson = gson.toJson(spotifyPlaylists, SpotifyPlaylists::class.java)
        destinationFile.writeText(updatedJson)
    }

    override suspend fun getTrackDetails(track: String, artist: String): TrackDetails? {
        val query = "track:${track.trim()} artist:${artist.trim()}".replace(" ", "%20")
        val url = "https://api.spotify.com/v1/search?q=$query&type=track"

        try {
            val bodyString = apiRepository.getJson(url, spotifyAccessToken) ?: return null

            val searchResponse = gson.fromJson(bodyString, SpotifySearchResponse::class.java)
            val firstTrack = searchResponse.tracks.items.firstOrNull() ?: return null

            val trackUri = firstTrack.uri
            val albumArtUrl = firstTrack.album.images.firstOrNull()?.url ?: ""

            return TrackDetails(uri = trackUri, albumArtUrl = albumArtUrl, artist = artist, track = track)
        } catch (e: Exception) {
            val o = e
        }

        return null
    }

    suspend fun getSpotifyAccessToken(): String? {
        val url = "https://accounts.spotify.com/api/token"
        val postBody = "grant_type=client_credentials&client_id=${spotify.clientId}&client_secret=${spotify.clientSecret}"
        val headers = mapOf("Content-Type" to "application/x-www-form-urlencoded")

        // Assuming apiRepository.postJson makes a POST request and returns the response JSON as a String.
        val jsonString = apiRepository.postJson(url, postBody, headers, "application/x-www-form-urlencoded") ?: return null
        val tokenResponse = gson.fromJson(jsonString, SpotifyAccessTokenResponse::class.java)
        return tokenResponse.accessToken
    }
}