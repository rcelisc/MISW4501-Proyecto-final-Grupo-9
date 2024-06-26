package com.example.sportapp

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import java.io.IOException
import java.util.Calendar
import java.util.concurrent.TimeUnit


class GetGoogleFitManager(private val context: Context, private val fitnessOptions: FitnessOptions) {

    fun fetchRestingHeartRate(callback: (Float) -> Unit) {
        val cuenta = GoogleSignIn.getAccountForExtension(context, fitnessOptions) // Cuenta
        if (cuenta != null)
        {
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
                        Log.e("ConnectGoogle", "Error al obtener datos >>>> : ", excepcion)
                        callback(0f) // O proporciona un mensaje más informativo o maneja el error en el código de llamada
                    }
            } catch (e: IOException) {
                // Elimina este bloque si no es relevante para el acceso a datos de Google Fit
                Log.e("ConnectGoogle", "Error al enviar datos por Bluetooth: ${e.message}")
            }
        }
        else{
            Log.e("ConnectGoogle", "El signin esta vacio o con error.")
        }
    }
}
