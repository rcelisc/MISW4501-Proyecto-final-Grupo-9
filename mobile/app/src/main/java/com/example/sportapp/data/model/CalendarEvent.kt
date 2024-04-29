package com.example.sportapp.data.model
import java.util.*
data class CalendarEvent (
    val descripción: String,
    val fecha: String,
    val nombre: String,
    val ubicación : String
)

data class EventsSuggestionsResponse(
    val id: Int,
    val name: String,
    val category: String,
    val description: String,
    val eventDate: Date,
    val duration: Int,
    val fee: Double,
    val location: String,
    val additionalInfo: AdditionalInfo
)

data class AdditionalInfo(
    val maxParticipants: Int,
    val minAge: Int,
    val registrationDeadline: Date
)
