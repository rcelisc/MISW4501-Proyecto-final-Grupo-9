package com.example.sportapp.data.model

data class User(
    val id: Int,
    val name: String,
    val surname: String,
    val id_type: String,
    val id_number: String,
    val city_of_living: String?,
    val country_of_living: String?,
    val type: String,
    val age: Int,
    val gender: String,
    val weight: Float,
    val height: Float,
    val city_of_birth: String,
    val country_of_birth: String,
    val sports: String?, // Comma-separated list of sports
    val profile_type: String?,
    val heart_rate: Int?,
    val vo2_max: Float?,
    val blood_pressure: String?,
    val respiratory_rate: Int?,
    val training_frequency: String?, // Changed to String to match Python
    val sports_practiced: String?,
    val average_session_duration: Int?, // Changed to Int to match Python
    val recovery_time: Int?,
    val training_pace: String?, // Changed to String to match Python
    val ethnicity: String?,
    val plan_type: String? = "basic", // Default value
    val daily_calories: Int?,
    val daily_protein: Int?,
    val daily_liquid: Int?,
    val daily_carbs: Int?,
    val meal_frequency: String? // Changed to String to match Python
)
