package com.example.sportapp.data.api

import com.example.sportapp.data.model.CalendarEvent
import com.example.sportapp.data.model.EventsSuggestionsResponse
import com.example.sportapp.data.model.ReceiveSesionDataResponse
import com.example.sportapp.data.model.StartTrainingResponse
import com.example.sportapp.data.model.StopTrainingResponse
import com.example.sportapp.data.model.TrainingMetricsCalculatedResponse
import com.example.sportapp.data.model.TrainingPlansResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface EventsService {
    @GET("user/{userId}/calendar")
    fun getCalendarEvents(@Path("userId") userId: Int): Call<List<CalendarEvent>>
}

interface CalculateFTPVo2Service {
    @POST("/calculate-ftp-vo2max")
    fun postCalculateFTPVo2Service(@Body requestBody: RequestBody): Call<TrainingMetricsCalculatedResponse>
}

interface StartTrainingService {
    @POST("/start-training")
    fun startTrainingService(@Body requestBody: RequestBody): Call<StartTrainingResponse>
}

interface StopTrainingService {
    @POST("/stop-training")
    fun stopTrainingService(@Body requestBody: RequestBody): Call<StopTrainingResponse>
}

interface ReceiveSesionDataService {
    @POST("/receive_session-data")
    fun receiveSesionDataServiceService(@Body requestBody: RequestBody): Call<ReceiveSesionDataResponse>
}

interface TrainingPlansService {
    @GET("training-plans/{profile}")
    fun getTrainingPlans(@Path("profile") profile: String): Call<List<TrainingPlansResponse>>
}

interface EventsSuggestionsService {
    @GET("/events/get")
    fun getEventsSuggestions(): Call<List<EventsSuggestionsResponse>>
}

//interface ApiService {
//    @GET("user/{userId}/calendar")
//    //@GET("/user/1/calendar")
//    fun getCalendarEvents(@Path("userId") userId: Int): Call<List<CalendarEvent>>
//}
