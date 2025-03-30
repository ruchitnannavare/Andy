package com.example.andy.data.repository

import com.example.andy.data.models.UserProfile

interface UserRepository {
    fun getUserRepository(): UserProfile
}