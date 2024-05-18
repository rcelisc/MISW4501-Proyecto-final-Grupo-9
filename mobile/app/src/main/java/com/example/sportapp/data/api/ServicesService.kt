package com.example.sportapp.data.api
import com.example.sportapp.data.model.ServicesResponse
import retrofit2.Call
import retrofit2.http.GET
interface ServicesService {
    @GET("/services/published")
    fun getServicesPublished(): Call<ServicesResponse>
}