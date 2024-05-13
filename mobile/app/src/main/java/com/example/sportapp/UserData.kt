package com.example.sportapp

import android.app.Application

// En el archivo MyApp.kt
class SportApp : Application() {
    companion object {
        var userSessionId: String = ""
        var userCodeId: Int = 0
        var powerOutput: Int = 0
        var maxHeartRate: Int = 0
        var restingHeartRate: Int = 0
        var profile: String = ""
        var startDevice: Boolean = false
        var isMale: Boolean = true
        var age: Int = 30
        var weight: Int = 80 // kg
        var height: Int = 174 // cm
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
        age = 40
        weight = 80
        height = 174
    }
}