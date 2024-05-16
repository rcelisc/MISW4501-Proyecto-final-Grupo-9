package com.example.sportapp.data.model

data class StravaActivity(
    val id: Long,
    val athlete_id: Long,
    val name: String,
    val distance: Float,
    val moving_time: Int,
    val elapsed_time: Int,
    val total_elevation_gain: Float,
    val type: String
)

data class AccessTokenResponse(
    val token_type: String,
    val expires_at: Long,
    val expires_in: Int,
    val refresh_token: String,
    val access_token: String,
    val athlete: Athlete
)

data class Athlete(
    val id: Long, // Cambiado de Int a Long
    val username: String?,
    val resource_state: Int,
    val firstname: String,
    val lastname: String,
    val bio: String?,
    val city: String,
    val state: String,
    val country: String,
    val sex: String,
    val premium: Boolean,
    val summit: Boolean,
    val created_at: String,
    val updated_at: String,
    val badge_type_id: Int,
    val weight: Float?,
    val profile_medium: String,
    val profile: String,
    val friend: String?,
    val follower: String?
)
