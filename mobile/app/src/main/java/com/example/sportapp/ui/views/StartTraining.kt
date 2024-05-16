package com.example.sportapp.ui.views

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Chronometer
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sportapp.R
import com.example.sportapp.SportApp
import com.example.sportapp.data.model.StartTrainingResponse
import com.example.sportapp.data.model.StopTrainingResponse
import com.example.sportapp.data.model.TrainingMetricsCalculatedResponse
import com.example.sportapp.data.repository.FTPVO2Repository
import com.example.sportapp.data.repository.StartTrainingRepository
import com.example.sportapp.data.repository.StopTrainingRepository
import com.example.sportapp.data.services.Calories
import com.example.sportapp.data.services.RetrofitClient
import com.example.sportapp.ui.home.Home
import com.example.sportapp.utils.BadgeUtils
import com.example.sportapp.utils.UtilRedirect
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StartTraining : AppCompatActivity() {
    private lateinit var chronometer: Chronometer
    private lateinit var spinnerTrainingType: Spinner
    private lateinit var tvwTimeTotal: TextView
    private lateinit var tvwDateTraining: TextView
    private lateinit var tvwCalTraining: TextView
    private lateinit var tvwFTP: TextView
    private lateinit var tvwVO2: TextView
    private lateinit var btnFTPVO2: View

    private var isChronometerRunning: Boolean = false
    private var typeTraining: String = ""
    private val startRepository = StartTrainingRepository(RetrofitClient.createTrainingSessionsService(this))
    private val stopRepository = StopTrainingRepository(RetrofitClient.createTrainingSessionsService(this))
    private val ftpRepository = FTPVO2Repository(RetrofitClient.createTrainingMetricsService(this))
    private val utilRedirect = UtilRedirect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_training)
        setUpNavigationButtons()

        // Initialize training type spinner
        val dataList = listOf("Natacion", "Ciclismo", "Running")
        spinnerTrainingType = findViewById(R.id.spinnerTrainingType)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dataList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTrainingType.adapter = adapter

        chronometer = findViewById(R.id.chronometer)
        tvwTimeTotal = findViewById(R.id.tvwTimeTotal)
        tvwDateTraining = findViewById(R.id.tvwDateTraining)
        tvwCalTraining = findViewById(R.id.tvwCalTraining)
        tvwFTP = findViewById(R.id.tvwFTP)
        tvwVO2 = findViewById(R.id.tvwVO2)
        btnFTPVO2 = findViewById(R.id.btnFTPVO2)

        findViewById<View>(R.id.btnStartTraining).setOnClickListener { toggleChronometer() }
        btnFTPVO2.setOnClickListener { calculateFTPVO2() }

        // Initially hide the finish training details and button
        hideFinishTrainingDetails()
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.top_navigation)
        BadgeUtils.updateNotificationBadge(this, bottomNavigationView)
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

    private fun toggleChronometer() {
        if (!isChronometerRunning) {
            typeTraining = spinnerTrainingType.selectedItem.toString()
            startTrainingSession()
        } else {
            stopChronometer()
        }
    }

    private fun startTrainingSession() {
        // Start the training session
        startRepository.startTrainingService(SportApp.userCodeId, typeTraining, object :
            Callback<StartTrainingResponse> {
            override fun onResponse(call: Call<StartTrainingResponse>, response: Response<StartTrainingResponse>) {
                if (response.isSuccessful) {
                    val startTrainingResponse = response.body()
                    SportApp.userSessionId = startTrainingResponse?.session_id ?: ""
                    if (SportApp.userSessionId.isNotEmpty()) {
                        startChronometer()
                    } else {
                        showToast(this@StartTraining, getString(R.string.error_starting_session))
                    }
                } else {
                    showToast(this@StartTraining, getString(R.string.error_starting_session))
                }
            }

            override fun onFailure(call: Call<StartTrainingResponse>, t: Throwable) {
                showToast(this@StartTraining, t.message ?: getString(R.string.error_generic))
            }
        })
    }

    private fun startChronometer() {
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
        isChronometerRunning = true
        findViewById<View>(R.id.btnStartTraining).apply {
            (this as TextView).text = getString(R.string.stop_training)
        }

        hideFinishTrainingDetails()
    }

    private fun stopChronometer() {
        chronometer.stop()
        val elapsedMillis = SystemClock.elapsedRealtime() - chronometer.base
        val elapsedMinutes = (elapsedMillis / 60000).toInt()
        isChronometerRunning = false
        findViewById<View>(R.id.btnStartTraining).apply {
            (this as TextView).text = getString(R.string.start_training)
        }

        finishTrainingSession(elapsedMinutes, typeTraining)
    }

    private fun finishTrainingSession(timeTraining: Int, typeTraining: String) {
        val caloriesBurned = Calories().calculateTotalCaloriesBurned(SportApp.age, SportApp.weight, SportApp.height, SportApp.isMale, typeTraining, timeTraining)
        stopRepository.stopTrainingService(SportApp.userSessionId, Date(), timeTraining, caloriesBurned.toInt(), "", object : Callback<StopTrainingResponse> {
            override fun onResponse(call: Call<StopTrainingResponse>, response: Response<StopTrainingResponse>) {
                if (response.isSuccessful) {
                    val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    tvwTimeTotal.text = getString(R.string.total_time, timeTraining)
                    tvwDateTraining.text = getString(R.string.prompt_date_training, date)
                    tvwCalTraining.text = getString(R.string.calories_burned, caloriesBurned.toInt())
                    tvwTimeTotal.visibility = View.VISIBLE
                    tvwDateTraining.visibility = View.VISIBLE
                    tvwCalTraining.visibility = View.VISIBLE
                    btnFTPVO2.visibility = View.VISIBLE

                    // Automatically calculate FTP and VO2 max
                    calculateFTPVO2()
                } else {
                    showToast(this@StartTraining, getString(R.string.error_finishing_session, response.code()))
                }
            }

            override fun onFailure(call: Call<StopTrainingResponse>, t: Throwable) {
                showToast(this@StartTraining, t.message ?: getString(R.string.error_generic))
            }
        })
    }

    private fun calculateFTPVO2() {
        ftpRepository.postCalculateFTPVo2(SportApp.userSessionId, object : Callback<TrainingMetricsCalculatedResponse> {
            override fun onResponse(call: Call<TrainingMetricsCalculatedResponse>, response: Response<TrainingMetricsCalculatedResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val ftpFormatted = String.format("%.2f", it.FTP)
                        val vo2Formatted = String.format("%.2f", it.VO2max)
                        tvwFTP.text = getString(R.string.prompt_ftp_training, ftpFormatted)
                        tvwVO2.text = getString(R.string.prompt_vo2_training, vo2Formatted)
                        tvwFTP.visibility = View.VISIBLE
                        tvwVO2.visibility = View.VISIBLE
                        showToast(this@StartTraining, getString(R.string.calculation_success))
                    }
                } else {
                    showToast(this@StartTraining, getString(R.string.error_calling_service, response.code()))
                }
            }

            override fun onFailure(call: Call<TrainingMetricsCalculatedResponse>, t: Throwable) {
                showToast(this@StartTraining, t.message ?: getString(R.string.error_generic))
            }
        })
    }

    private fun hideFinishTrainingDetails() {
        tvwTimeTotal.visibility = View.GONE
        tvwDateTraining.visibility = View.GONE
        tvwCalTraining.visibility = View.GONE
        tvwFTP.visibility = View.GONE
        tvwVO2.visibility = View.GONE
        btnFTPVO2.visibility = View.GONE
    }

    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}
