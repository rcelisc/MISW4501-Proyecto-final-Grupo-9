package com.example.sportapp.data.api

import com.example.sportapp.data.model.LoginRequest
import com.example.sportapp.data.model.LoginResponse
import com.example.sportapp.data.model.LogoutRequest
import com.example.sportapp.data.model.LogoutResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/logout")
    fun logoutUser(@Body request: LogoutRequest): Call<LogoutResponse>
}