package com.example.sportapp.data.model

data class Event(
    val additional_info: String?,
    val category: String,
    val description: String,
    val duration: Int,
    val event_date: String,
    val fee: Double,
    val id: Int,
    val location: String,
    val name: String
)

data class Service(
    val available: Boolean,
    val description: String,
    val id: Int,
    val name: String,
    val rate: Double,
    val status: String
)

data class CalendarEventsAndServicesResponse(
    val events: List<Event>,
    val services: List<Service>
)

