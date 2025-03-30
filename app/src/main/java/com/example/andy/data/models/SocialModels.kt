package com.example.andy.data.models

data class SocialMedia(
    val twitter: TwitterData
)

data class TwitterData(
    val handle: String,
    val recent_posts: List<String>,
    val followers_count: Int,
    val following_count: Int
)