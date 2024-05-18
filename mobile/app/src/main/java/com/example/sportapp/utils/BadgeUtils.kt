package com.example.sportapp.utils

import android.content.Context
import android.util.Log
import com.example.sportapp.R
import com.example.sportapp.SportApp
import com.example.sportapp.data.model.EventSuggestion
import com.example.sportapp.data.model.FoodBeverageSuggestion
import com.example.sportapp.data.model.ServicesResponse
import com.example.sportapp.data.model.TrainingPlansResponse
import com.example.sportapp.data.repository.EventsRepository
import com.example.sportapp.data.repository.ServicesRepository
import com.example.sportapp.data.repository.TrainingPlansRepository
import com.example.sportapp.data.services.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object BadgeUtils {

    fun updateNotificationBadge(context: Context, bottomNavigationView: BottomNavigationView) {
        val sharedPreferences = context.getSharedPreferences("SportAppPrefs", Context.MODE_PRIVATE)
        val dismissedSet = sharedPreferences.getStringSet("dismissedSuggestions", emptySet()) ?: emptySet()
        Log.d("BadgeUtils", "Dismissed suggestions: $dismissedSet")

        val trainingPlansRepository = TrainingPlansRepository(RetrofitClient.createTrainingPlansService(context))
        val eventsRepository = EventsRepository(RetrofitClient.getEventsService(context))
        val servicesRepository = ServicesRepository(RetrofitClient.getServicesPublished(context))

        var notificationCount = 0

        trainingPlansRepository.getTrainingPlans(SportApp.profile).enqueue(object : Callback<List<TrainingPlansResponse>> {
            override fun onResponse(call: Call<List<TrainingPlansResponse>>, response: Response<List<TrainingPlansResponse>>) {
                if (response.isSuccessful) {
                    response.body()?.let { plans ->
                        val count = plans.count { !dismissedSet.contains("plan_${it.id}") }
                        Log.d("BadgeUtils", "Training plans count: $count")
                        notificationCount += count
                        updateBadge(bottomNavigationView, notificationCount)
                    }
                }
            }

            override fun onFailure(call: Call<List<TrainingPlansResponse>>, t: Throwable) {
                Log.e("BadgeUtils", "Error fetching training plans: ${t.message}")
            }
        })

        eventsRepository.getCalendarEvents(SportApp.userCodeId).enqueue(object : Callback<List<EventSuggestion>> {
            override fun onResponse(call: Call<List<EventSuggestion>>, response: Response<List<EventSuggestion>>) {
                if (response.isSuccessful) {
                    response.body()?.let { events ->
                        val count = events.count { !dismissedSet.contains("event_${it.id}") }
                        Log.d("BadgeUtils", "Events count: $count")
                        notificationCount += count
                        updateBadge(bottomNavigationView, notificationCount)
                    }
                }
            }

            override fun onFailure(call: Call<List<EventSuggestion>>, t: Throwable) {
                Log.e("BadgeUtils", "Error fetching events: ${t.message}")
            }
        })

        // Count for Services
        if (SportApp.plan_type == "intermediate" || SportApp.profile == "premium") {
            servicesRepository.getServicesPublished().enqueue(object : Callback<ServicesResponse> {
                override fun onResponse(
                    call: Call<ServicesResponse>,
                    response: Response<ServicesResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { servicesResponse ->
                            val count =
                                servicesResponse.services.count { !dismissedSet.contains("service_${it.id}") }
                            Log.d("BadgeUtils", "Services count: $count")
                            notificationCount += count
                            updateBadge(bottomNavigationView, notificationCount)
                        }
                    }
                }

                override fun onFailure(call: Call<ServicesResponse>, t: Throwable) {
                    Log.e("BadgeUtils", "Error fetching services: ${t.message}")
                }
            })
        }


        // Check for Route Suggestion
        val suggestedRoute = sharedPreferences.getString("suggestedRoute", null)
        if (suggestedRoute != null && !dismissedSet.contains("route_0")) {
            notificationCount += 1
            updateBadge(bottomNavigationView, notificationCount)
        }

        // Add count for food and beverage suggestions
        val foodBeverageSuggestions = getRandomFoodBeverageSuggestions().filter { !dismissedSet.contains("foodBeverage_${it.id}") }
        notificationCount += foodBeverageSuggestions.size
        updateBadge(bottomNavigationView, notificationCount)


    }

    private fun updateBadge(bottomNavigationView: BottomNavigationView, count: Int) {
        val badge = bottomNavigationView.getOrCreateBadge(R.id.nav_notifications)
        badge.number = count
        badge.isVisible = count > 0
        Log.d("BadgeUtils", "Updating badge count to: $count")
    }

    private fun getRandomFoodBeverageSuggestions(limit: Int = 1): List<FoodBeverageSuggestion> {
        val suggestions = listOf(
            FoodBeverageSuggestion(1, "Banana", "High in potassium and easy to digest.", "Before"),
            FoodBeverageSuggestion(2, "Energy Bar", "Provides quick energy.", "During"),
            FoodBeverageSuggestion(3, "Chocolate Milk", "Great for recovery with carbs and protein.", "After"),
            FoodBeverageSuggestion(4, "Water", "Stay hydrated throughout the activity.", "During"),
            FoodBeverageSuggestion(5, "Protein Shake", "Helps in muscle recovery.", "After")
        )
        return suggestions.shuffled().take(limit)
    }
}
