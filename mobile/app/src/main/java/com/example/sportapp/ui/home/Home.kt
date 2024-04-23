package com.example.sportapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sportapp.LoginScreen
import com.example.sportapp.R
import com.example.sportapp.ui.views.CalendarEvents
import com.example.sportapp.ui.views.DashboardTraining
import com.example.sportapp.ui.views.Notifications
import com.example.sportapp.ui.views.StartTraining
import com.example.sportapp.ui.views.StravaViewConnect

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        // String appName = getString(R.string.app_name)
        val btnStrava = findViewById<ImageView>(R.id.imgStrava)
        val btnRunExe = findViewById<ImageView>(R.id.ivRunExe)
        val btnExit = findViewById<ImageView>(R.id.ivHome)
        val btnExit1 = findViewById<TextView>(R.id.tvwCerrarSesion)
        val btnCalendar = findViewById<ImageView>(R.id.ivCalendar)
        val btnNotifications = findViewById<ImageView>(R.id.ivNotifications)
        val btnDashboard = findViewById<ImageView>(R.id.ivClockW)



        //Redirige a la Actividad Strava.
        btnStrava.setOnClickListener{
            val strava = Intent(this, StravaViewConnect::class.java)
            startActivity(strava)
        }

        //Redirige a la Actividad Iniciar Entrenamiento.
        btnRunExe.setOnClickListener{
            val startTraining = Intent(this, StartTraining::class.java)
            startActivity(startTraining)
        }

        //Cerrar Sesion.
        btnExit.setOnClickListener{
            val exitApp = Intent(this, LoginScreen::class.java)
            startActivity(exitApp)
        }

        btnExit1.setOnClickListener{
            val exitApp = Intent(this, LoginScreen::class.java)
            startActivity(exitApp)
        }

        //Redirige a la Actividad Calendario de Eventos.
        btnCalendar.setOnClickListener{
            val calendar = Intent(this, CalendarEvents::class.java)
            startActivity(calendar)
        }

        btnNotifications.setOnClickListener{
            val notif = Intent(this, Notifications::class.java)
            startActivity(notif)
        }


        btnDashboard.setOnClickListener{
            val dash = Intent(this, DashboardTraining::class.java)
            startActivity(dash)
        }

    }


}