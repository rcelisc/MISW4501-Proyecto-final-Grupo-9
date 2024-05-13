package com.example.sportapp.ui.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sportapp.SportApp
import com.example.sportapp.databinding.ActivityLoginBinding
import com.example.sportapp.ui.home.Home
import com.example.sportapp.UtilRedirect

class LoginScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val utilRedirect = UtilRedirect()

        binding.btnLogin.setOnClickListener {
            utilRedirect.redirectToActivity(this, Home::class.java)
            initializeUserSession()
        }
    }

    private fun initializeUserSession() {
        SportApp.userCodeId = 1  // This should be set after a successful login attempt
        SportApp.powerOutput = 250
        SportApp.maxHeartRate = 180
        SportApp.restingHeartRate = 60
        SportApp.profile = "Beginner"
        SportApp.userSessionId = ""
    }
}
