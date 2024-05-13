package com.example.sportapp.ui.views

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.sportapp.R
import com.example.sportapp.UtilRedirect
import com.example.sportapp.ui.home.Home

class SuggestRoutes : AppCompatActivity() {
    private val utilRedirect = UtilRedirect()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggest_routes)
        setUpNavigationButtons()
    }

    private fun setUpNavigationButtons() {
        findViewById<ImageView>(R.id.ivRunExe).setOnClickListener { utilRedirect.redirectToActivity(this, StartTraining::class.java) }
        findViewById<ImageView>(R.id.ivHome).setOnClickListener { utilRedirect.redirectToActivity(this, Home::class.java) }
        findViewById<ImageView>(R.id.ivCalendar).setOnClickListener { utilRedirect.redirectToActivity(this, CalendarEvents::class.java) }
        findViewById<ImageView>(R.id.ivNotifications).setOnClickListener { utilRedirect.redirectToActivity(this, Notifications::class.java) }
        findViewById<ImageView>(R.id.ivClockW).setOnClickListener { utilRedirect.redirectToActivity(this, DashboardTraining::class.java) }
        findViewById<ImageView>(R.id.ivWatch).setOnClickListener { utilRedirect.redirectToActivity(this, ConnectDevice::class.java) }
        findViewById<ImageView>(R.id.ivRun).setOnClickListener { utilRedirect.redirectToActivity(this, SuggestRoutes::class.java) }
        findViewById<ImageView>(R.id.ivSugerencias).setOnClickListener { utilRedirect.redirectToActivity(this, Suggests::class.java) }

    }
}