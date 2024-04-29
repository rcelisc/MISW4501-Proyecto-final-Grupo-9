package com.example.sportapp.ui.views

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sportapp.R
import com.example.sportapp.SportApp
import com.example.sportapp.data.model.ReceiveSesionDataResponse
import com.example.sportapp.data.model.StopTrainingResponse
import com.example.sportapp.data.model.TrainingMetricsCalculatedResponse
import com.example.sportapp.data.repository.FTPVO2Repository
import com.example.sportapp.data.repository.ReceiveSesionDataRepository
import com.example.sportapp.data.repository.StopTrainingRepository
import com.example.sportapp.data.services.RetrofitCalculateFTPVO2max
import com.example.sportapp.data.services.RetrofitReceiveSesionDataService
import com.example.sportapp.data.services.RetrofitStopTrainingService
import  com.example.sportapp.data.services.Calories
import com.example.sportapp.data.services.FitnessSensor
import com.example.sportapp.ui.home.Home
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.Locale

class FinishTraining : AppCompatActivity() {


    private val repositoryStop = StopTrainingRepository(RetrofitStopTrainingService.createApiService())
    private val repositoryReceiveData = ReceiveSesionDataRepository(RetrofitReceiveSesionDataService.createApiService())
    private val repository = FTPVO2Repository(RetrofitCalculateFTPVO2max.createApiService())
    private val sensor = FitnessSensor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_training)

        val ivHome = findViewById<ImageView>(R.id.ivHome)
        val ivRunExe = findViewById<ImageView>(R.id.ivRunExe)
        val btnFTPVO2 = findViewById<Button>(R.id.btnStart)
        val btnDevice = findViewById<ImageView>(R.id.ivWatch)




        //Redirige a la Actividad Device
        btnDevice.setOnClickListener{
            val device = Intent(this, ConnectDevice::class.java)
            startActivity(device)
        }

        //Variables de la vista
        val tvwTypeRun = findViewById<TextView>(R.id.tvwType)
        val tvwTimeTotal = findViewById<TextView>(R.id.tvwTimeTotal)
        val tvwDateTraining = findViewById<TextView>(R.id.tvwDateTraining)
        val tvwCalTraining = findViewById<TextView>(R.id.tvwCalTraining)
        val tvwFTP = findViewById<TextView>(R.id.tvwFTP)
        val tvwVO2 = findViewById<TextView>(R.id.tvwVO2)
        sensor.start()

        ivHome.setOnClickListener{
            val home = Intent(this, Home::class.java)
            startActivity(home)
        }

        ivRunExe.setOnClickListener{
            val home = Intent(this, StartTraining::class.java)
            startActivity(home)
        }

        btnFTPVO2.setOnClickListener{
            tvwVO2.text = getString(R.string.prompt_vo2_training) //"FTP :"
            tvwFTP.text = getString(R.string.prompt_ftp_training) //"VO2Max : "
            repository.postCalculateFTPVo2(SportApp.userSesionId, object : Callback<TrainingMetricsCalculatedResponse> {
                override fun onResponse(call: Call<TrainingMetricsCalculatedResponse>, response: Response<TrainingMetricsCalculatedResponse>) {
                    if (response.isSuccessful) {
                        //val metricsResponse = response.body()
                        val metricsResponse = response.body()

                        Log.d("DEBUG",  "Body > " + response.body().toString() + " Sesion -> " + SportApp.userSesionId + "Data ->> " + metricsResponse.toString())

                        val ftpFormatted = String.format("%.2f", metricsResponse?.FTP)
                        val vo2Formatted = String.format("%.2f", metricsResponse?.VO2max)
                        tvwFTP.text = tvwFTP.text.toString() + " " + ftpFormatted + " W"
                        tvwVO2.text = tvwVO2.text.toString() + " " + vo2Formatted + " ml/kg/min"

                        showToast(this@FinishTraining, "Calculo FTp y VO2Max Exitoso!!")
                        val Message = "Valores generados FTP : > " +  metricsResponse?.FTP.toString() + " Vo2 > " + metricsResponse?.VO2max.toString()
                        Log.d("DEBUG", Message)
                        Log.d("DEBUG", metricsResponse.toString())

                    } else {
                        val errorMessage = "Error en la llamada al servicio FTPVo2Max . Código de error: ${response.code()}"
                        showToast(this@FinishTraining, errorMessage)
                        Log.d("DEBUG", errorMessage)
                    }
                }

                override fun onFailure(call: Call<TrainingMetricsCalculatedResponse>, t: Throwable) {
                    // Manejar errores de red o de llamada al servicio
                    Log.d("DEBUG", "Error en la llamada al servicio: ${t.message}")
                    t.printStackTrace()
                }
            })

        }

        var timeTraining = intent.getIntExtra("timeTraining",0)
        val typeTraining = intent.getStringExtra("typeTraining")
        val caloriesBurned = Calories().calculateTotalCaloriesBurned(SportApp.age, SportApp.weight, SportApp.height, SportApp.isMale, typeTraining.toString(), timeTraining)

        repositoryStop.stopTrainingService(SportApp.userSesionId, Date(), timeTraining.toInt(), caloriesBurned.toInt(), "", object : Callback<StopTrainingResponse> {
            override fun onResponse(call: Call<StopTrainingResponse>, response: Response<StopTrainingResponse>) {
                if (response.isSuccessful) {
                    //val stopTrainingResponse = response.body()
                    //SportApp.userSesionId = stopTrainingResponse?.session_id.toString()

                    tvwTypeRun.text = tvwTypeRun.text.toString() + " " +  typeTraining
                    tvwTimeTotal.text = tvwTimeTotal.text.toString() + " " + timeTraining.toString() + " " + getString(R.string.units_minutes)
                    tvwDateTraining.text = tvwDateTraining.text.toString() + " " + SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    tvwCalTraining.text = tvwCalTraining.text.toString() + " " + caloriesBurned.toInt().toString()

                    showToast(this@FinishTraining, getString(R.string.promt_finish_training))
                    Log.d("DEBUG", "Sesion Finalizada Correctamente : " + SportApp.userSesionId)

                } else {
                    val errorMessage = "Error al finalizar sesion. Código de error: ${response.code()}"
                    showToast(this@FinishTraining, errorMessage)
                    Log.d("DEBUG", errorMessage)
                }

                //Receive
                val measurements = sensor.generateManualMeasurements()
                val (powerOutput, maxHeartRate, restingHeartRate) = measurements ?: Triple(0, 0, 0)
                repositoryReceiveData.receiveSesionDataService(SportApp.userSesionId, powerOutput, maxHeartRate, restingHeartRate, object : Callback<ReceiveSesionDataResponse> {
                    override fun onResponse(call: Call<ReceiveSesionDataResponse>, response: Response<ReceiveSesionDataResponse>) {
                        if (response.isSuccessful) {
                            val receiveDataSesionResponse = response.body()

                            showToast(this@FinishTraining, getString(R.string.promt_data_sended))
                            Log.d("DEBUG", "Receive data sesion exitosa : PO " +  powerOutput.toString() + " MH "  + maxHeartRate.toString() + " RH " + restingHeartRate.toString() + " Resp. Serv." + receiveDataSesionResponse?.message.toString())

                        } else {
                            val errorMessage = "Error al llamar servicio Receive Sesion Data . Código de error: ${response.code()}"
                            showToast(this@FinishTraining, errorMessage)
                            Log.d("DEBUG", errorMessage)
                        }

                    }

                    override fun onFailure(call: Call<ReceiveSesionDataResponse>, t: Throwable) {
                        // Manejar errores de red o de llamada al servicio
                        Log.d("DEBUG", "Error en la llamada al servicio: ${t.message}")
                        t.printStackTrace()
                    }
                })
            }

            override fun onFailure(call: Call<StopTrainingResponse>, t: Throwable) {
                // Manejar errores de red o de llamada al servicio
                Log.d("DEBUG", "Error al finalizar sesion service.: ${t.message}")
                t.printStackTrace()
            }
        })

    }

    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}