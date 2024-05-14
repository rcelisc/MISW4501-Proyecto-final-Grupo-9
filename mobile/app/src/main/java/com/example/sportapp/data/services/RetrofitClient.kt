package com.example.sportapp.data.services

import android.content.Context
import com.example.sportapp.Config
import com.example.sportapp.data.api.*
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private fun getRetrofit(context: Context, baseUrl: String): Retrofit {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
            val request: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${getToken(context)}")
                .build()
            chain.proceed(request)
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
        return getRetrofit(context, Config.BASE_URL_USERS).create(AuthService::class.java)
    }

    fun createUserService(context: Context): UserService {
        return getRetrofit(context, Config.BASE_URL_USERS_QUERIES).create(UserService::class.java)
    }

    fun getEventsService(context: Context): EventsService {
        return getRetrofit(context, Config.BASE_URL_SERVICE).create(EventsService::class.java)
    }
    fun createTrainingMetricsService(context: Context): TrainingMetricsService {
        return getRetrofit(context, Config.BASE_URL_FTPVo2).create(TrainingMetricsService::class.java)
    }

    fun createTrainingSessionsService(context: Context): TrainingSessionsService {
        return getRetrofit(context, Config.BASE_URL_Trainings).create(TrainingSessionsService::class.java)
    }

    fun createTrainingPlansService(context: Context): TrainingPlanService {
        return getRetrofit(context, Config.BASE_URL_TrainingPlans).create(TrainingPlanService::class.java)
    }
}
