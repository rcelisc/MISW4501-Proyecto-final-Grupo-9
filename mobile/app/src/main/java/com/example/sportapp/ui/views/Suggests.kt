package com.example.sportapp.ui.views

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.sportapp.R
import com.example.sportapp.UtilRedirect

class Suggests : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggests)
        setUpNavigationButtons()
    }

    private fun setUpNavigationButtons() {
        val btnRunExe = findViewById<ImageView>(R.id.ivRunExe)
        val btnExit = findViewById<ImageView>(R.id.ivHome)
        val btnCalendar = findViewById<ImageView>(R.id.ivCalendar)
        val btnNotifications = findViewById<ImageView>(R.id.ivNotifications)
        val btnDashboard = findViewById<ImageView>(R.id.ivClockW)
        val btnDevice = findViewById<ImageView>(R.id.ivWatch)
        val btnSuggestRoutes = findViewById<ImageView>(R.id.ivRun)
        val btnSuggest = findViewById<ImageView>(R.id.ivSugerencias)

        btnDevice.setOnClickListener{ UtilRedirect().redirectToDeviceActivity(this)}
        btnRunExe.setOnClickListener{ UtilRedirect().redirectToStartTrainingActivity(this)}
        btnExit.setOnClickListener{ UtilRedirect().redirectToHomeActivity(this)}
        btnCalendar.setOnClickListener{ UtilRedirect().redirectToCalendarEventsActivity(this)}
        btnNotifications.setOnClickListener{ UtilRedirect().redirectToNotificationsActivity(this)}
        btnDashboard.setOnClickListener{ UtilRedirect().redirectToDashboardTrainingActivity(this)}
        btnSuggestRoutes.setOnClickListener{ UtilRedirect().redirectToSuggestRoutesActivity(this)}
        btnSuggest.setOnClickListener{ UtilRedirect().redirectToSuggestsActivity(this)}
    }
}