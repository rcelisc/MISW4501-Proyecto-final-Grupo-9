package com.example.sportapp.ui.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sportapp.R
import com.example.sportapp.SportApp
import com.example.sportapp.utils.UtilRedirect
import com.example.sportapp.data.model.StartTrainingResponse
import com.example.sportapp.data.repository.StartTrainingRepository
import com.example.sportapp.data.services.RetrofitClient
import com.example.sportapp.ui.home.Home
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RunTraining : AppCompatActivity() {

    private lateinit var chronometer1: Chronometer
    private lateinit var startButton: Button
    private var isChronometerRunning: Boolean = false
    private var valorTraining: Int = 0
    private var typeTraining: String = ""
    private val repository = StartTrainingRepository(RetrofitClient.createTrainingSessionsService(this))
    private val utilRedirect = UtilRedirect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run_training)
        setUpNavigationButtons()
        typeTraining = intent.getStringExtra("training").toString()

        val tvwTypeRun = findViewById<TextView>(R.id.tvwTypeRun)

        /*Conectar con servicio iniciar entrenamiento*/
        repository.startTrainingService(SportApp.userCodeId, typeTraining, object :
            Callback<StartTrainingResponse> {
            override fun onResponse(call: Call<StartTrainingResponse>, response: Response<StartTrainingResponse>) {
                if (response.isSuccessful) {
                    val startTrainingResponse = response.body()
                    SportApp.userSessionId = startTrainingResponse?.session_id.toString()
                    showToast(this@RunTraining, getString(R.string.promt_start_training))
                    Log.d("DEBUG", "Session Id : " + SportApp.userSessionId)
                } else {
                    val errorMessage = "La llamada al servicio no fue exitosa. Código de error: ${response.code()}"
                    showToast(this@RunTraining, errorMessage)
                    Log.d("DEBUG", errorMessage)
                }
            }

            override fun onFailure(call: Call<StartTrainingResponse>, t: Throwable) {
                // Manejar errores de red o de llamada al servicio
                Log.d("DEBUG", "Error en la llamada al servicio: ${t.message}")
                t.printStackTrace()
            }
        })
        /**/
        tvwTypeRun.text = getString(R.string.type_training)  + " " + typeTraining
        chronometer1 = findViewById(R.id.chronometer1)
        startButton = findViewById(R.id.btnStart)

        startButton.setOnClickListener { startChronometer() }

        startChronometer()
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


    fun startChronometer() {
        if (!isChronometerRunning) {
            // Hacer visible el cronómetro y comenzar a contar
            chronometer1.visibility = View.VISIBLE
            //chronometer.format = "HH:mm:ss"
            chronometer1.base = SystemClock.elapsedRealtime()
            chronometer1.start()

            // Cambiar el texto del botón a "Detener"
            startButton.text = getString(R.string.stop_training)
            isChronometerRunning = true
        } else {
            // Detener el cronómetro
            chronometer1.stop()
            val tiempoDetenido = chronometer1.text.toString()
            val partesTiempo = tiempoDetenido.split(":")
            val minutos = partesTiempo[0].toInt()
            startButton.text = getString(R.string.start_training)
            isChronometerRunning = false

            //LLamar la nueva vista y pasar los parametros.
            finishTrainingActivity(minutos)

        }
    }

    private fun finishTrainingActivity(tiempoEjercicio: Int) {
        val finishTra = Intent(this, FinishTraining::class.java)
        var min = tiempoEjercicio
        if (min <= 0){
            min = 1
        }
        finishTra.putExtra("timeTraining", min)
        finishTra.putExtra("typeTraining", typeTraining)
        startActivity(finishTra)
    }

    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}
