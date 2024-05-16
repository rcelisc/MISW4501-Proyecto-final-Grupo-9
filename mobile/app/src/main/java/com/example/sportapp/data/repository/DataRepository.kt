package com.example.sportapp.data.repository

import android.util.Log
import com.example.sportapp.data.api.*
import com.example.sportapp.data.model.*
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Callback
import java.util.Date

class DataRepository(private val eventsService: EventsService) {
    fun getCalendarEventsAndServices() = eventsService.getCalendarEventsAndServices()
}

class EventsRepository(private val eventsService: EventsService) {
    fun getCalendarEvents(userId: Int) = eventsService.getUserCalendar(userId)
}

class FTPVO2Repository(private val trainingMetricsService: TrainingMetricsService) {
    fun postCalculateFTPVo2(sessionId: String, callback: Callback<TrainingMetricsCalculatedResponse>) {
        val calculateFtpVo2maxRequest = CalculateFtpVo2maxRequest(sessionId)
        val jsonRequest = Gson().toJson(calculateFtpVo2maxRequest)
        Log.d("DEBUG",  "Data in Repository Body > $jsonRequest Session -> $sessionId")
        val requestBody = jsonRequest.toRequestBody("application/json".toMediaTypeOrNull())
        trainingMetricsService.postCalculateFTPVo2Service(requestBody).enqueue(callback)
    }
}

class StartTrainingRepository(private val trainingSessionsService: TrainingSessionsService) {
    fun startTrainingService(userId: Int, trainingType: String, callback: Callback<StartTrainingResponse>) {
        val startTrainingRequest = StartTrainingRequest(user_id = userId, training_type = trainingType)
        val jsonRequest = Gson().toJson(startTrainingRequest)
        val requestBody = jsonRequest.toRequestBody("application/json".toMediaTypeOrNull())
        trainingSessionsService.startTrainingService(requestBody).enqueue(callback)
    }
}

class StopTrainingRepository(private val trainingSessionsService: TrainingSessionsService) {
    fun stopTrainingService(sessionId: String, trainingDate: Date, duration: Int, caloriesBurned: Int, notes: String, callback: Callback<StopTrainingResponse>) {
        val stopTrainingRequest = StopTrainingRequest(session_id = sessionId, end_time = trainingDate, duration = duration, calories_burned = caloriesBurned, notes = notes)
        val jsonRequest = Gson().toJson(stopTrainingRequest)
        val requestBody = jsonRequest.toRequestBody("application/json".toMediaTypeOrNull())
        trainingSessionsService.stopTrainingService(requestBody).enqueue(callback)
    }
}

class ReceiveSessionDataRepository(private val trainingSessionsService: TrainingSessionsService) {
    fun receiveSessionDataService(sessionId: String, powerOutput: Int, maxHeartRate: Int, restingHeartRate: Int, callback: Callback<ReceiveSessionDataResponse>) {
        val receiveSessionDataRequest = ReceiveSessionDataRequest(session_id = sessionId, power_output = powerOutput, max_heart_rate = maxHeartRate, resting_heart_rate = restingHeartRate)
        val jsonRequest = Gson().toJson(receiveSessionDataRequest)
        val requestBody = jsonRequest.toRequestBody("application/json".toMediaTypeOrNull())
        trainingSessionsService.receiveSessionDataServiceService(requestBody).enqueue(callback)
    }
}

class TrainingPlansRepository(private val trainingPlanService: TrainingPlanService) {
    fun getTrainingPlans(profile: String?) = trainingPlanService.getTrainingPlans(profile)
}

class TrainingSessionsRepository(private val trainingPlanService: TrainingPlanService) {
    fun getTrainingSessions(userId: Int) = trainingPlanService.getTrainingSessionsByUser(userId)
}

class StravaRepository(private val stravaService: StravaService) {

    fun exchangeCodeForToken(code: String, callback: Callback<AccessTokenResponse>) {
        stravaService.exchangeCodeForToken(code).enqueue(callback)
    }

    fun fetchStravaActivities(token: String, callback: Callback<List<StravaActivity>>) {
        stravaService.fetchStravaActivities(token).enqueue(callback)
    }

    fun getStoredActivities(callback: Callback<List<StravaActivity>>) {
        stravaService.getStoredActivities().enqueue(callback)
    }
}