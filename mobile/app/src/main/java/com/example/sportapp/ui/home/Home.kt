package com.example.sportapp.ui.home

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sportapp.ui.views.LoginScreen
import com.example.sportapp.R
import com.example.sportapp.UtilRedirect
import com.example.sportapp.ui.views.*


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
        val btnExit1 = findViewById<TextView>(R.id.tvwCerrarSession)
        val btnCalendar = findViewById<ImageView>(R.id.ivCalendar)
        val btnNotifications = findViewById<ImageView>(R.id.ivNotifications)
        val btnDashboard = findViewById<ImageView>(R.id.ivClockW)
        val btnDevice = findViewById<ImageView>(R.id.ivWatch)
        val btnSuggestRoutes = findViewById<ImageView>(R.id.ivRun)
        val btnSuggest = findViewById<ImageView>(R.id.ivSugerencias)

        val utilRedirect = UtilRedirect()

        //Redirige a la Actividad Device
        btnDevice.setOnClickListener { utilRedirect.redirectToActivity(this, ConnectDevice::class.java) }
        //Redirige a la Actividad Strava.
        btnStrava.setOnClickListener { utilRedirect.redirectToActivity(this, StravaViewConnect::class.java) }
        //Redirige a la Actividad Iniciar Entrenamiento.
        btnRunExe.setOnClickListener { utilRedirect.redirectToActivity(this, StartTraining::class.java) }
        //Cerrar Session.
        btnExit.setOnClickListener { utilRedirect.redirectToActivity(this, LoginScreen::class.java) }
        btnExit1.setOnClickListener { utilRedirect.redirectToActivity(this, LoginScreen::class.java) }
        //Redirige a la Actividad Calendario de Eventos.
        btnCalendar.setOnClickListener { utilRedirect.redirectToActivity(this, CalendarEvents::class.java) }
        btnNotifications.setOnClickListener { utilRedirect.redirectToActivity(this, Notifications::class.java) }
        btnDashboard.setOnClickListener { utilRedirect.redirectToActivity(this, DashboardTraining::class.java) }
        btnSuggestRoutes.setOnClickListener { utilRedirect.redirectToActivity(this, SuggestRoutes::class.java) }
        btnSuggest.setOnClickListener { utilRedirect.redirectToActivity(this, Suggests::class.java) }
    }

    private fun  initNumeration() {
        val tvVersion = findViewById<TextView>(R.id.tvwVersionName)

        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        tvVersion.text = "v_" + packageInfo.versionName.toString()

    }
}