package com.example.sportapp.data.api

import com.example.sportapp.data.model.TrainingPlansResponse
import com.example.sportapp.data.model.TrainingsSessionsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TrainingPlanService {
    @GET("training-plans/{profile}")
    fun getTrainingPlans(@Path("profile") profile: String?): Call<List<TrainingPlansResponse>>

    @GET("training-sessions/user/{userId}")
    fun getTrainingSessionsByUser(@Path("userId") userId: Int): Call<List<TrainingsSessionsResponse>>
}
