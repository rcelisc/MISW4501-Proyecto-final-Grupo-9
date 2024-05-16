package com.example.sportapp.data.api

import com.example.sportapp.data.model.CalendarEventsAndServicesResponse
import com.example.sportapp.data.model.EventSuggestion
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface EventsService {
    @GET("/services/published")
    fun getCalendarEventsAndServices(): Call<CalendarEventsAndServicesResponse>

    @GET("/events/user/{userId}")
    fun getUserCalendar(@Path("userId") userId: Int): Call<List<EventSuggestion>>
}
