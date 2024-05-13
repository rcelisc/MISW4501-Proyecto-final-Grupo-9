package com.example.sportapp.ui.home

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sportapp.R
import com.example.sportapp.UtilRedirect


class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpNavigationButtons()
        initNumeration()
    }

    private fun setUpNavigationButtons() {
        // String appName = getString(R.string.app_name)
        val btnStrava = findViewById<ImageView>(R.id.imgStrava)
        val btnRunExe = findViewById<ImageView>(R.id.ivRunExe)
        val btnExit = findViewById<ImageView>(R.id.ivHome)
        val btnExit1 = findViewById<TextView>(R.id.tvwCerrarSesion)
        val btnCalendar = findViewById<ImageView>(R.id.ivCalendar)
        val btnNotifications = findViewById<ImageView>(R.id.ivNotifications)
        val btnDashboard = findViewById<ImageView>(R.id.ivClockW)
        val btnDevice = findViewById<ImageView>(R.id.ivWatch)
        val btnSuggestRoutes = findViewById<ImageView>(R.id.ivRun)
        val btnSuggest = findViewById<ImageView>(R.id.ivSugerencias)

        btnDevice.setOnClickListener{UtilRedirect().redirectToDeviceActivity(this@Home)}
        btnStrava.setOnClickListener{UtilRedirect().redirectToStravaActivity(this@Home)}
        btnRunExe.setOnClickListener{UtilRedirect().redirectToStartTrainingActivity(this@Home)}
        btnExit.setOnClickListener{UtilRedirect().redirectToLoginScreenActivity(this@Home)}
        btnExit1.setOnClickListener{UtilRedirect().redirectToLoginScreenActivity(this@Home)}
        btnCalendar.setOnClickListener{UtilRedirect().redirectToCalendarEventsActivity(this@Home)}
        btnNotifications.setOnClickListener{UtilRedirect().redirectToNotificationsActivity(this@Home)}
        btnDashboard.setOnClickListener{UtilRedirect().redirectToDashboardTrainingActivity(this@Home)}
        btnSuggestRoutes.setOnClickListener{UtilRedirect().redirectToSuggestRoutesActivity(this@Home)}
        btnSuggest.setOnClickListener{UtilRedirect().redirectToSuggestsActivity(this@Home)}
    }

    private fun  initNumeration() {
        val tvVersion = findViewById<TextView>(R.id.tvwVersionName)

        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        tvVersion.text = "v_" + packageInfo.versionName.toString()

    }
}