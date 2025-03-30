package com.example.andy.data.models.responses

data class SpotifySearchResponse(
    val tracks: Tracks
)

data class Tracks(
    val items: List<TrackItem>
)

data class TrackItem(
    val album: Album,
    val uri: String
)

data class Album(
    val images: List<AlbumImage>
)

data class AlbumImage(
    val url: String,
    val height: Int?,
    val width: Int?
)
