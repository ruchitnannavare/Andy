package com.example.andy.data.models

import com.google.gson.annotations.SerializedName
import kotlinx.io.bytestring.unsafe.UnsafeByteStringOperations
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

// Mirrors the JSON structure exactly.
@OptIn(InternalSerializationApi::class)
@Serializable
data class SpotifyPlaylists(
    val playlists: MutableList<PlaylistData>
)

data class SpotifyAccessTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int
)

data class TrackDetails(
    val uri: String,
    val albumArtUrl: String,
    val artist: String,
    val track: String
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class PlaylistData(
    val name: String,
    val tracks: MutableList<String>
)

// Higher-level models for the UI (artist and song are split out).
data class Playlist(
    val name: String,
    val tracks: List<Track>
)

data class Track(
    val artist: String,
    val song: String
)
