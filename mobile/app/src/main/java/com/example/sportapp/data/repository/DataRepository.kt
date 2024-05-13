package com.example.sportapp.data.repository

import android.util.Log
import com.example.sportapp.SportApp
import com.example.sportapp.data.api.CalculateFTPVo2Service
import com.example.sportapp.data.api.EventsService
import com.example.sportapp.data.api.EventsSuggestionsService
import com.example.sportapp.data.api.ReceiveSessionDataService
import com.example.sportapp.data.api.StartTrainingService
import com.example.sportapp.data.api.StopTrainingService
import com.example.sportapp.data.api.TrainingPlansService
import com.example.sportapp.data.api.TrainingSessionsService
import com.example.sportapp.data.model.CalculateFtpVo2maxRequest
import com.example.sportapp.data.model.ReceiveSessionDataRequest
import com.example.sportapp.data.model.ReceiveSessionDataResponse
import com.example.sportapp.data.model.StartTrainingRequest
import com.example.sportapp.data.model.StartTrainingResponse
import com.example.sportapp.data.model.StopTrainingRequest
import com.example.sportapp.data.model.StopTrainingResponse
import com.example.sportapp.data.model.TrainingMetricsCalculatedResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Callback
import java.util.Date


class DataRepository(private val apiService: EventsService) {
    fun getCalendarEvents(userId: Int) = apiService.getCalendarEvents(userId)
}

class FTPVO2Repository(private val ftpVo2Service: CalculateFTPVo2Service) {
    fun postCalculateFTPVo2(sessionId: String, callback: Callback<TrainingMetricsCalculatedResponse>) {
        val calculateFtpVo2maxRequest = CalculateFtpVo2maxRequest(sessionId)
        val jsonRequest = Gson().toJson(calculateFtpVo2maxRequest)
        Log.d("DEBUG",  "Data en Respository Body > " + jsonRequest.toString() + " Session -> " + sessionId)
        val requestBody1 = jsonRequest.toString().toRequestBody("application/json".toMediaTypeOrNull())
        Log.d("DEBUG",  "Data en Respository Body -- 1 > " + requestBody1.toString() + " Session -> " + sessionId)
        ftpVo2Service.postCalculateFTPVo2Service(requestBody1).enqueue(callback)
    }
}

class StartTrainingRepository(private val startTrainingService: StartTrainingService) {
    fun startTrainingService(userId: Int, trainingType: String, callback: Callback<StartTrainingResponse>) {
        val startTrainingRequest = StartTrainingRequest(user_id = userId, training_type = trainingType)
        val jsonRequest = Gson().toJson(startTrainingRequest)
        val requestBody = jsonRequest.toRequestBody("application/json".toMediaTypeOrNull())
        startTrainingService.startTrainingService(requestBody).enqueue(callback)
    }
}

class StopTrainingRepository(private val stopTrainingService: StopTrainingService) {
    fun stopTrainingService(sessionId: String, trainingDate: Date, duration: Int, caloriesBurned: Int, notes: String, callback: Callback<StopTrainingResponse>) {
        val stopTrainingRequest = StopTrainingRequest(session_id = sessionId, end_time = trainingDate, duration = duration, calories_burned = caloriesBurned, notes = notes)
        val jsonRequest = Gson().toJson(stopTrainingRequest)
        val requestBody = jsonRequest.toRequestBody("application/json".toMediaTypeOrNull())
        stopTrainingService.stopTrainingService(requestBody).enqueue(callback)
    }
}

class ReceiveSessionDataRepository(private val receiveSessionDataService: ReceiveSessionDataService) {
    fun receiveSessionDataService(sessionId: String, powerOutput: Int, maxHeartRate: Int, restingHeartRate: Int, callback: Callback<ReceiveSessionDataResponse>) {
        val stopTrainingRequest = ReceiveSessionDataRequest(session_id = sessionId, power_output = powerOutput, max_heart_rate = maxHeartRate, resting_heart_rate = restingHeartRate)
        val jsonRequest = Gson().toJson(stopTrainingRequest)
        val requestBody = jsonRequest.toRequestBody("application/json".toMediaTypeOrNull())
        receiveSessionDataService.receiveSessionDataServiceService(requestBody).enqueue(callback)
    }
}

class TrainingPlansRepository(private val trainingPlansService: TrainingPlansService) {
    fun getTrainingPlans(profile: String) = trainingPlansService.getTrainingPlans(profile)
}

class EventsSuggestionsRepository(private val eventsSuggestionsService: EventsSuggestionsService) {
    fun getEventsSuggestions() = eventsSuggestionsService.getEventsSuggestions()
}

class TrainingSessionsRepository(private val trainingSessionService: TrainingSessionsService) {
    fun getTrainingSessions(userId: Int) = trainingSessionService.getTrainingUserService(userId)
}