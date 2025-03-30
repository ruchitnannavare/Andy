package com.example.andy.data.repository

import com.example.andy.data.models.Playlist
import com.example.andy.data.models.TrackDetails

interface SpotifyRepository {
    fun getPlayList(playListName: String): Playlist
    fun addPlayList(playlistName: String, artist: String, song: String)

    suspend fun getTrackDetails(track: String, artist: String): TrackDetails?
}