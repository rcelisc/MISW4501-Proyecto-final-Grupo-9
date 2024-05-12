package com.example.sportapp

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import java.io.IOException
import java.util.Calendar
import java.util.concurrent.TimeUnit

class GoogleFitManager(private val context: Context, private val fitnessOptions: GoogleSignInOptionsExtension) {

    fun fetchRestingHeartRate(callback: (Float) -> Unit) {
        val account = GoogleSignIn.getAccountForExtension(context, fitnessOptions)
        val endTime = Calendar.getInstance().timeInMillis
        val startTime = endTime - TimeUnit.DAYS.toMillis(7) // Obtener datos de los últimos 7 días


        try {
            val restingHeartRateRequest = DataReadRequest.Builder()
                .read(DataType.TYPE_HEART_RATE_BPM)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()

            Fitness.getHistoryClient(context, account)
                .readData(restingHeartRateRequest)
                .addOnSuccessListener { response ->
                    val dataPoints = response.getDataSet(DataType.TYPE_HEART_RATE_BPM).dataPoints
                    val lastDataPoint = dataPoints.lastOrNull()
                    val restingHeartRate = lastDataPoint?.getValue(Field.FIELD_BPM)?.asFloat() ?: 0f
                    callback(restingHeartRate)
                }
                .addOnFailureListener { exception ->
                    // Manejar errores aquí
                    Log.e("ConnectGoogle", "Error al Obtener los datos de Google: ${exception.message}")
                    callback(0f)
                }
        }
        catch (e: IOException) {
            Log.e("ConnectGoogle", "Error al enviar datos por Bluetooth: ${e.message}")
        }
    }


//    fun fetchMaxHeartRate(callback: (Float) -> Unit) {
//        val account = GoogleSignIn.getAccountForExtension(context, fitnessOptions)
//        val endTime = Calendar.getInstance().timeInMillis
//        val startTime = endTime - TimeUnit.DAYS.toMillis(7) // Obtener datos de los últimos 7 días
//
//        val maxHeartRateRequest = DataReadRequest.Builder()
//            .read(DataType.TYPE_HEART_RATE_MAX)
//            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
//            .build()
//
//        Fitness.getHistoryClient(context, account)
//            .readData(maxHeartRateRequest)
//            .addOnSuccessListener { response ->
//                val dataPoints = response.getDataSet(DataType.TYPE_HEART_RATE_MAX).dataPoints
//                val lastDataPoint = dataPoints.lastOrNull()
//                val maxHeartRate = lastDataPoint?.getValue(Field.FIELD_MAX)?.asFloat() ?: 0f
//                callback(maxHeartRate)
//            }
//            .addOnFailureListener { exception ->
//                // Manejar errores aquí
//                callback(0f)
//            }
//    }
}



class GetGoogleFitManager(private val context: Context, private val fitnessOptions: FitnessOptions) {

    fun fetchRestingHeartRate(callback: (Float) -> Unit) {
        val cuenta = GoogleSignIn.getAccountForExtension(context, fitnessOptions) // Cuenta
        val tiempoFinal = Calendar.getInstance().timeInMillis
        val tiempoInicial = tiempoFinal - TimeUnit.DAYS.toMillis(7) // Obtener datos de los últimos 7 días

        try {
            val solicitudFrecuenciaCardiacaReposo = DataReadRequest.Builder()
                .read(DataType.TYPE_HEART_RATE_BPM) // Tipo de dato
                .setTimeRange(tiempoInicial, tiempoFinal, TimeUnit.MILLISECONDS)
                .build()

            Fitness.getHistoryClient(context, cuenta)
                .readData(solicitudFrecuenciaCardiacaReposo)
                .addOnSuccessListener { respuesta ->
                    val puntosDatos = respuesta.getDataSet(DataType.TYPE_HEART_RATE_BPM).dataPoints
                    val ultimoPunto = puntosDatos.lastOrNull()
                    val frecuenciaCardiacaReposo = ultimoPunto?.getValue(Field.FIELD_BPM)?.asFloat() ?: 0f
                    callback(frecuenciaCardiacaReposo)
                }
                .addOnFailureListener { excepcion ->
                    Log.e("GoogleFitManager", "Error al obtener datos: ", excepcion)
                    callback(0f) // O proporciona un mensaje más informativo o maneja el error en el código de llamada
                }
        } catch (e: IOException) {
            // Elimina este bloque si no es relevante para el acceso a datos de Google Fit
            Log.e("ConnectGoogle", "Error al enviar datos por Bluetooth: ${e.message}")
        }
    }
}
