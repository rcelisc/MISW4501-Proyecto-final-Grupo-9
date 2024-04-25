package com.example.sportapp.data.services

class Calories {

    fun calculateBMR(age: Int, weight: Int, height: Int, isMale: Boolean): Double {
        val bmr: Double
        if (isMale) {
            bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
        } else {
            bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
        }
        return bmr
    }


    fun calculateExerciseCaloriesBurned(exerciseType: String, durationInMinutes: Int, weight: Int): Double {
        // Aquí podrías tener una tabla de calorías por ejercicio
        // o usar un método específico para calcular las calorías
        // dependiendo del tipo de ejercicio y su intensidad.
        // Por simplicidad, asumimos que recibimos un valor fijo
        // de calorías quemadas por minuto para el ejercicio dado.
        val caloriesPerMinute: Double = when (exerciseType) {
            "running" -> 10.0 // Suponiendo 10 calorías por minuto para correr
            "cycling" -> 8.0  // Suponiendo 8 calorías por minuto para ciclismo
            else -> 5.0       // Por defecto, 5 calorías por minuto
        }
        return caloriesPerMinute * durationInMinutes * weight / 60
    }


    fun calculateTotalCaloriesBurned(age: Int, weight: Int, height: Int, isMale: Boolean,
                                      exerciseType: String, durationInMinutes: Int): Double {
        val bmr = calculateBMR(age, weight, height, isMale)
        val exerciseCaloriesBurned = calculateExerciseCaloriesBurned(exerciseType, durationInMinutes, weight)
        return bmr + exerciseCaloriesBurned
    }
}