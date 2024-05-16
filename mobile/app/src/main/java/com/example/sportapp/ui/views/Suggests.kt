package com.example.sportapp.ui.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sportapp.R
import com.example.sportapp.ui.home.Home
import com.example.sportapp.utils.BadgeUtils
import com.example.sportapp.utils.UtilRedirect
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class Suggests : AppCompatActivity() {
    private val utilRedirect = UtilRedirect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggests)
        setUpNavigationButtons()
    }

    override fun onResume() {
        super.onResume()
        val topNavigationView = findViewById<BottomNavigationView>(R.id.top_navigation)
        BadgeUtils.updateNotificationBadge(this, topNavigationView)
    }

    private fun setUpNavigationButtons() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_run -> {
                    utilRedirect.redirectToActivity(this, DashboardTraining::class.java)
                    true
                }
                R.id.nav_clock -> {
                    utilRedirect.redirectToActivity(this, DashboardTraining::class.java)
                    true
                }
                R.id.nav_start -> {
                    utilRedirect.redirectToActivity(this, StartTraining::class.java)
                    true
                }
                R.id.nav_watch -> {
                    utilRedirect.redirectToActivity(this, ConnectDevice::class.java)
                    true
                }
                else -> false
            }
        }

        val topNavigationView = findViewById<BottomNavigationView>(R.id.top_navigation)
        topNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_suggestions -> {
                    utilRedirect.redirectToActivity(this, Suggests::class.java)
                    true
                }
                R.id.nav_home -> {
                    utilRedirect.redirectToActivity(this, Home::class.java)
                    true
                }
                R.id.nav_calendar -> {
                    utilRedirect.redirectToActivity(this, CalendarEvents::class.java)
                    true
                }
                R.id.nav_notifications -> {
                    utilRedirect.redirectToActivity(this, Notifications::class.java)
                    true
                }
                else -> false
            }
        }
    }
}
