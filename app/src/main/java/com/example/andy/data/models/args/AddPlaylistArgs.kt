package com.example.andy.data.models.args

import kotlinx.serialization.Serializable

data class AddPlaylistArgs(
    val playlistName: String,
    val artist: String,
    val song: String
)