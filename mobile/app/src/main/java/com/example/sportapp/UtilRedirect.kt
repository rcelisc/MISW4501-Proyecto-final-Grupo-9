package com.example.sportapp
import android.content.Context
import android.content.Intent
import com.example.sportapp.ui.home.Home
import com.example.sportapp.ui.views.CalendarEvents
import com.example.sportapp.ui.views.ConnectDevice
import com.example.sportapp.ui.views.DashboardTraining
import com.example.sportapp.ui.views.Notifications
import com.example.sportapp.ui.views.StartTraining
import com.example.sportapp.ui.views.StravaViewConnect
import com.example.sportapp.ui.views.SuggestRoutes
import com.example.sportapp.ui.views.Suggests

class UtilRedirect {

    fun redirectToDeviceActivity(context: Context) {
        val intent = Intent(context, ConnectDevice::class.java)
        context.startActivity(intent)
    }

    fun redirectToHomeActivity(context: Context) {
        val intent = Intent(context, Home::class.java)
        context.startActivity(intent)
    }

    fun redirectToStravaActivity(context: Context) {
        val intent = Intent(context, StravaViewConnect::class.java)
        context.startActivity(intent)
    }

    fun redirectToStartTrainingActivity(context: Context) {
        val intent = Intent(context, StartTraining::class.java)
        context.startActivity(intent)
    }

    fun redirectToLoginScreenActivity(context: Context) {
        val intent = Intent(context, LoginScreen::class.java)
        context.startActivity(intent)
    }
    fun redirectToCalendarEventsActivity(context: Context) {
        val intent = Intent(context, CalendarEvents::class.java)
        context.startActivity(intent)
    }

    fun redirectToNotificationsActivity(context: Context) {
        val intent = Intent(context, Notifications::class.java)
        context.startActivity(intent)
    }

    fun redirectToDashboardTrainingActivity(context: Context) {
        val intent = Intent(context, DashboardTraining::class.java)
        context.startActivity(intent)
    }

    fun redirectToSuggestRoutesActivity(context: Context) {
        val intent = Intent(context, SuggestRoutes::class.java)
        context.startActivity(intent)
    }

    fun redirectToSuggestsActivity(context: Context) {
        val intent = Intent(context, Suggests::class.java)
        context.startActivity(intent)
    }
}