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
import com.example.sportapp.data.model.StartTrainingResponse
import com.example.sportapp.data.repository.StartTrainingRepository
import com.example.sportapp.data.services.RetrofitStartTrainingService
import com.example.sportapp.ui.home.Home
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RunTraining : AppCompatActivity() {

    private lateinit var chronometer1: Chronometer
    private lateinit var startButton: Button
    private var isChronometerRunning: Boolean = false
    private var valorTraining: Int = 0
    private var typeTraining: String = ""
    private val repository = StartTrainingRepository(RetrofitStartTrainingService.createApiService())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run_training)
        typeTraining = intent.getStringExtra("training").toString()

        val tvwTypeRun = findViewById<TextView>(R.id.tvwTypeRun)
        val btnHome = findViewById<ImageView>(R.id.ivHome)
        val btnDevice = findViewById<ImageView>(R.id.ivWatch)

        //Redirige a la Actividad Device
        btnDevice.setOnClickListener{
            val device = Intent(this, ConnectDevice::class.java)
            startActivity(device)
        }

        btnHome.setOnClickListener{
            val home = Intent(this, Home::class.java)
            startActivity(home)
        }

        /**/
        /*Conectar con servicio iniciar entrenamiento*/
        repository.startTrainingService(SportApp.userCodeId, typeTraining, object :
            Callback<StartTrainingResponse> {
            override fun onResponse(call: Call<StartTrainingResponse>, response: Response<StartTrainingResponse>) {
                if (response.isSuccessful) {
                    val startTrainingResponse = response.body()
                    SportApp.userSesionId = startTrainingResponse?.session_id.toString()
                    showToast(this@RunTraining, getString(R.string.promt_start_training))
                    Log.d("DEBUG", "Sesion Id : " + SportApp.userSesionId)
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
