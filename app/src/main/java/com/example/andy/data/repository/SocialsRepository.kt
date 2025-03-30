package com.example.andy.data.repository

import com.example.andy.data.models.SocialMedia

interface SocialsRepository {
    fun getSocials(): SocialMedia
}