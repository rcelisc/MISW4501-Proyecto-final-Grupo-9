package com.example.sportapp.data.api


import com.example.sportapp.data.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
interface UserService {
    @GET("users/{userId}")
    fun getUserById(@Path("userId") userId: Int): Call<User>
}