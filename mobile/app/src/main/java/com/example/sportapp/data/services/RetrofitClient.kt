package com.example.sportapp.data.services

import android.content.Context
import com.example.sportapp.Config
import com.example.sportapp.data.api.AuthService
import com.example.sportapp.data.api.EventsService
import com.example.sportapp.data.api.ServicesService
import com.example.sportapp.data.api.StravaService
import com.example.sportapp.data.api.TrainingMetricsService
import com.example.sportapp.data.api.TrainingPlanService
import com.example.sportapp.data.api.TrainingSessionsService
import com.example.sportapp.data.api.UserService
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private fun getRetrofit(baseUrl: String, includeAuthInterceptor: Boolean, context: Context): Retrofit {
        val httpClient = OkHttpClient.Builder()

        if (includeAuthInterceptor) {
            httpClient.addInterceptor { chain ->
                val request: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${getToken(context)}")
                    .build()
                chain.proceed(request)
            }
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
    }

    private fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("SportAppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }

    fun createAuthService(context: Context): AuthService {
        return getRetrofit(Config.BASE_URL_USERS, true, context).create(AuthService::class.java)
    }

    fun createUserService(context: Context): UserService {
        return getRetrofit(Config.BASE_URL_USERS_QUERIES, true, context).create(UserService::class.java)
    }

    fun getEventsService(context: Context): EventsService {
        return getRetrofit(Config.BASE_URL_EVENTS_QUERIES, true, context).create(EventsService::class.java)
    }

    fun getEventsAndServicesService(context: Context): EventsService {
        return getRetrofit(Config.BASE_URL_SERVICE, true, context).create(EventsService::class.java)
    }

    fun getServicesPublished(context: Context): ServicesService {
        return getRetrofit(Config.BASE_URL_SERVICE, true, context).create(ServicesService::class.java)
    }

    fun createTrainingMetricsService(context: Context): TrainingMetricsService {
        return getRetrofit(Config.BASE_URL_FTPVo2, true, context).create(TrainingMetricsService::class.java)
    }

    fun createTrainingSessionsService(context: Context): TrainingSessionsService {
        return getRetrofit(Config.BASE_URL_Trainings, true, context).create(TrainingSessionsService::class.java)
    }

    fun createTrainingPlansService(context: Context): TrainingPlanService {
        return getRetrofit(Config.BASE_URL_TrainingPlans, true, context).create(TrainingPlanService::class.java)
    }



    // Nueva configuración de Retrofit para Strava
    fun createStravaService(context: Context): StravaService {
        return getRetrofit(Config.BASE_URL_Trainings, false, context).create(StravaService::class.java)
    }
}
