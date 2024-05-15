package com.example.sportapp.data.api

import com.example.sportapp.data.model.TrainingMetricsCalculatedResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
interface TrainingMetricsService {
    @POST("/calculate-ftp-vo2max")
    fun postCalculateFTPVo2Service(@Body requestBody: RequestBody): Call<TrainingMetricsCalculatedResponse>
}