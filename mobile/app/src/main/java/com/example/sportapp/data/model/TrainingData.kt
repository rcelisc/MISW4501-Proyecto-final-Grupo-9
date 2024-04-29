package com.example.sportapp.data.model

import java.util.Date

data class StartTrainingRequest(
    val user_id: Int,
    val training_type: String
)

data class CalculateFtpVo2maxRequest(
    val session_id: String
)

data class StartTrainingResponse (
    val session_id: String
)

data class StopTrainingRequest(
    val session_id: String,
    val end_time: Date,
    val duration: Int,
    val calories_burned: Int,
    val notes: String
)

data class StopTrainingResponse (
    val message: String,
    val session_id: String
)


data class ReceiveSesionDataRequest(
    val session_id: String,
    val power_output: Int,
    val max_heart_rate: Int,
    val resting_heart_rate: Int
)

data class ReceiveSesionDataResponse (
    val message: String
)

data class TrainingPlansResponse(
    val assigned_users: String,
    val description: String,
    val duration: String,
    val exercises: String,
    val frequency: String,
    val id: Int,
    val objectives: String,
    val profile_type: String
)

data class TrainingsSesionsResponse(
    val duration: String,
    val id: String,
    val notes: String,
    val training_type: String,
    val user_id: Int
)

