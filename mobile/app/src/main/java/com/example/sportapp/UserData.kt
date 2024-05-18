package com.example.sportapp

import android.app.Application

// En el archivo MyApp.kt
class SportApp : Application() {
    companion object {
        var userSessionId: String = ""
        var userRole: String? = null
        var userCodeId: Int = -1
        var powerOutput: Int = 0
        var maxHeartRate: Int = 0
        var restingHeartRate: Int = 0
        var profile: String? = ""
        var plan_type: String? = ""
        var startDevice: Boolean = false
        var isMale: Boolean = true
        var age: Int = 0
        var weight: Float = 0.0F // kg
        var height: Float = 0.0F // cm
        var calories: Int = 0 // cm
        var steps: Int = 0 // cm
    }

    override fun onCreate() {
        super.onCreate()
        resetUserData()
    }

    private fun resetUserData() {
        userSessionId = ""
        userCodeId = 0
        powerOutput = 0
        maxHeartRate = 0
        restingHeartRate = 0
        profile = ""
        startDevice = false
        isMale = true
        age = 0
        weight = 0.0F
        height = 0.0F
        calories = 0
        steps = 0
    }
}