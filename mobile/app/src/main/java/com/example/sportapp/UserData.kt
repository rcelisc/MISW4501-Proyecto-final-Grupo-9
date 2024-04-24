package com.example.sportapp

import android.app.Application

// En el archivo MyApp.kt
class SportApp : Application() {
    companion object {
        var userSesionId: String = ""
        var userCodeId: Int = 0
        var powerOutput: Int = 0
        var maxHeartRate: Int = 0
        var restingHeartRate: Int = 0
        var profile: String = ""
        var startDevice: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()
        userSesionId = ""
        userCodeId = 0
        powerOutput = 0
        maxHeartRate = 0
        restingHeartRate = 0
        profile = ""
        startDevice = false
    }
}