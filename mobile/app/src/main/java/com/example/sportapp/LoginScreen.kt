package com.example.sportapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.sportapp.ui.home.Home
import com.example.sportapp.UtilRedirect

class LoginScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // String appName = getString(R.string.app_name)

        val btnLoginE = findViewById<Button>(R.id.btnLogin)

        btnLoginE.setOnClickListener {
            UtilRedirect().redirectToHomeActivity(this@LoginScreen)
        }

        // Ponemos VALORES por defecto usuario en 1. mientras se hace HU Login.
        SportApp.userCodeId = 1
        SportApp.powerOutput = 250
        SportApp.maxHeartRate= 180
        SportApp.restingHeartRate = 60
        SportApp.profile = "Beginner"
        SportApp.userSesionId = ""

    }
}
