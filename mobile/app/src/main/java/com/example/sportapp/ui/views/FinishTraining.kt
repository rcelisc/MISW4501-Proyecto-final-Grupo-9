package com.example.sportapp.ui.views

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sportapp.R
import com.example.sportapp.SportApp
import com.example.sportapp.utils.UtilRedirect
import com.example.sportapp.data.model.StopTrainingResponse
import com.example.sportapp.data.model.TrainingMetricsCalculatedResponse
import com.example.sportapp.data.repository.FTPVO2Repository
import com.example.sportapp.data.repository.ReceiveSessionDataRepository
import com.example.sportapp.data.repository.StopTrainingRepository
import com.example.sportapp.data.services.Calories
import com.example.sportapp.data.services.FitnessSensor
import com.example.sportapp.data.services.RetrofitClient
import com.example.sportapp.ui.home.Home
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat

class FinishTraining : AppCompatActivity() {
    private val repositoryStop = StopTrainingRepository(RetrofitClient.createTrainingSessionsService(this))
    private val repositoryReceiveData = ReceiveSessionDataRepository(RetrofitClient.createTrainingSessionsService(this))
    private val repository = FTPVO2Repository(RetrofitClient.createTrainingMetricsService(this))
    private val utilRedirect = UtilRedirect()
    private val sensor = FitnessSensor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_training)
        setUpNavigationButtons()

        val btnFTPVO2 = findViewById<Button>(R.id.btnStart)
        val tvwTypeRun = findViewById<TextView>(R.id.tvwType)
        val tvwTimeTotal = findViewById<TextView>(R.id.tvwTimeTotal)
        val tvwDateTraining = findViewById<TextView>(R.id.tvwDateTraining)
        val tvwCalTraining = findViewById<TextView>(R.id.tvwCalTraining)
        val tvwFTP = findViewById<TextView>(R.id.tvwFTP)
        val tvwVO2 = findViewById<TextView>(R.id.tvwVO2)

        val timeTraining = intent.getIntExtra("timeTraining", 0)
        val typeTraining = intent.getStringExtra("typeTraining") ?: ""
        val caloriesBurned = Calories().calculateTotalCaloriesBurned(SportApp.age, SportApp.weight, SportApp.height, SportApp.isMale, typeTraining, timeTraining)

        btnFTPVO2.setOnClickListener {
            calculateFTPVO2(tvwFTP, tvwVO2)
        }

        finishTrainingSession(timeTraining, typeTraining, caloriesBurned, tvwTypeRun, tvwTimeTotal, tvwDateTraining, tvwCalTraining)
    }

    private fun calculateFTPVO2(tvwFTP: TextView, tvwVO2: TextView) {
        repository.postCalculateFTPVo2(SportApp.userSessionId, object : Callback<TrainingMetricsCalculatedResponse> {
            override fun onResponse(call: Call<TrainingMetricsCalculatedResponse>, response: Response<TrainingMetricsCalculatedResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val ftpFormatted = String.format("%.2f", it.FTP)
                        val vo2Formatted = String.format("%.2f", it.VO2max)
                        tvwFTP.text = getString(R.string.prompt_ftp_training, ftpFormatted)
                        tvwVO2.text = getString(R.string.prompt_vo2_training, vo2Formatted)
                        showToast(this@FinishTraining, getString(R.string.calculation_success))
                    }
                } else {
                    showToast(this@FinishTraining, getString(R.string.error_calling_service, response.code()))
                }
            }

            override fun onFailure(call: Call<TrainingMetricsCalculatedResponse>, t: Throwable) {
                showToast(this@FinishTraining, t.message ?: getString(R.string.error_generic))
            }
        })
    }

    private fun finishTrainingSession(timeTraining: Int, typeTraining: String, caloriesBurned: Double, tvwTypeRun: TextView, tvwTimeTotal: TextView, tvwDateTraining: TextView, tvwCalTraining: TextView) {
        repositoryStop.stopTrainingService(SportApp.userSessionId, Date(), timeTraining, caloriesBurned.toInt(), "", object : Callback<StopTrainingResponse> {
            override fun onResponse(call: Call<StopTrainingResponse>, response: Response<StopTrainingResponse>) {
                if (response.isSuccessful) {
                    tvwTypeRun.text = getString(R.string.type_run, typeTraining)
                    tvwTimeTotal.text = getString(R.string.total_time, timeTraining)
                    tvwDateTraining.text = getString(R.string.prompt_date_training, SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))
                    tvwCalTraining.text = getString(R.string.calories_burned, caloriesBurned.toInt())
                    showToast(this@FinishTraining, getString(R.string.training_session_finished))
                } else {
                    showToast(this@FinishTraining, getString(R.string.error_finishing_session, response.code()))
                }
            }

            override fun onFailure(call: Call<StopTrainingResponse>, t: Throwable) {
                showToast(this@FinishTraining, t.message ?: getString(R.string.error_generic))
            }
        })
    }

    private fun setUpNavigationButtons() {
        findViewById<MaterialButton>(R.id.ivRunExe).setOnClickListener { utilRedirect.redirectToActivity(this, StartTraining::class.java) }
        findViewById<MaterialButton>(R.id.ivHome).setOnClickListener { utilRedirect.redirectToActivity(this, Home::class.java) }
        findViewById<MaterialButton>(R.id.ivCalendar).setOnClickListener { utilRedirect.redirectToActivity(this, CalendarEvents::class.java) }
        findViewById<MaterialButton>(R.id.ivNotifications).setOnClickListener { utilRedirect.redirectToActivity(this, Notifications::class.java) }
        findViewById<MaterialButton>(R.id.ivClockW).setOnClickListener { utilRedirect.redirectToActivity(this, DashboardTraining::class.java) }
        findViewById<MaterialButton>(R.id.ivWatch).setOnClickListener { utilRedirect.redirectToActivity(this, ConnectDevice::class.java) }
        findViewById<MaterialButton>(R.id.ivRun).setOnClickListener { utilRedirect.redirectToActivity(this, SuggestRoutes::class.java) }
        findViewById<MaterialButton>(R.id.ivSugerencias).setOnClickListener { utilRedirect.redirectToActivity(this, Suggests::class.java) }
    }

    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}
