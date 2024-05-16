package com.example.sportapp.data.api

import com.example.sportapp.data.model.AccessTokenResponse
import com.example.sportapp.data.model.StravaActivity
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface StravaService {
    @GET("/strava_callback")
    fun exchangeCodeForToken(@Query("code") code: String): Call<AccessTokenResponse>

    @GET("/fetch_strava_activities")
    fun fetchStravaActivities(@Header("Authorization") token: String): Call<List<StravaActivity>>


    @GET("/activities")
    fun getStoredActivities(): Call<List<StravaActivity>>
}
