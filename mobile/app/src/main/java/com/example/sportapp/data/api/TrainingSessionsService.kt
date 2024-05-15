package com.example.sportapp.data.api

import com.example.sportapp.data.model.TrainingsSessionsResponse
import com.example.sportapp.data.model.ReceiveSessionDataResponse
import com.example.sportapp.data.model.StartTrainingResponse
import com.example.sportapp.data.model.StopTrainingResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.POST

interface TrainingSessionsService {

    @POST("/start-training")
    fun startTrainingService(@Body requestBody: RequestBody): Call<StartTrainingResponse>

    @POST("/stop-training")
    fun stopTrainingService(@Body requestBody: RequestBody): Call<StopTrainingResponse>

    @POST("/receive_session-data")
    fun receiveSessionDataServiceService(@Body requestBody: RequestBody): Call<ReceiveSessionDataResponse>

}
