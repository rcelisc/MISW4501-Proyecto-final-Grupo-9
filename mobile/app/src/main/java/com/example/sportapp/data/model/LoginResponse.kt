package com.example.sportapp.data.model

data class LoginRequest(
        val id_number: String,
        val password: String
)

data class LoginResponse(
        val token: String,
)

data class LogoutRequest(
        val user_id: Int
)

data class LogoutResponse(
        val message: String
)
