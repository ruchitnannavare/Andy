package com.example.andy.data.models.args

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class AddPlaylistArgs(
    val playlistName: String,
    val artist: String,
    val song: String
)