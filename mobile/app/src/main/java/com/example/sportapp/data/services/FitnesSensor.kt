package com.example.sportapp.data.services

import kotlin.random.Random

interface FitnessSensorListener {
    fun onMeasurementsChanged(powerOutput: Int, maxHeartRate: Int, restingHeartRate: Int)
}

class FitnessSensor {
    private var isRunning: Boolean = false

    private var listener: FitnessSensorListener? = null

    fun setListener(listener: FitnessSensorListener) {
        this.listener = listener
    }

    fun start() {
        if (!isRunning) {
            isRunning = true
            println("Fitness sensor started.")
            //simulateMeasurements()
        } else {
            println("Fitness sensor is already running.")
        }
    }

    fun stop() {
        if (isRunning) {
            isRunning = false
            println("Fitness sensor stopped.")
        } else {
            println("Fitness sensor is not running.")
        }
    }

    private fun simulateMeasurements() {
        Thread {
            while (isRunning) {
                val powerOutput = Random.nextInt(100, 500)
                val maxHeartRate = Random.nextInt(140, 200)
                val restingHeartRate = Random.nextInt(50, 90)

                println("Power Output: $powerOutput watts")
                println("Max Heart Rate: $maxHeartRate bpm")
                println("Resting Heart Rate: $restingHeartRate bpm")
                Thread.sleep(1000) // Simulate measurements every second
            }
        }.start()
    }


    fun generateManualMeasurements(): Triple<Int, Int, Int>? {
        return if (isRunning) {
            val powerOutput = Random.nextInt(100, 500)
            val maxHeartRate = Random.nextInt(140, 200)
            val restingHeartRate = Random.nextInt(50, 90)

            // No necesitamos llamar al listener en este caso
            Triple(powerOutput, maxHeartRate, restingHeartRate) // Devolver un Triple con los datos generados
        } else {
            null // En caso de que el sensor no esté en ejecución, devolver null
        }
    }
}

    fun main() {
        val sensor = FitnessSensor()
        sensor.start()

        // Simular la ejecución del sensor durante 10 segundos
        Thread.sleep(10000)
        sensor.stop()
    }


