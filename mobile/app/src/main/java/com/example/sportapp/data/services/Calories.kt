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
        // Por simplicidad, asumimos que recibimos un valor fijo
        // de calorías quemadas por minuto para el ejercicio dado.
        val caloriesPerMinute: Double = when (exerciseType.lowercase()) {
            "running" -> 10.0
            "cycling" -> 8.0
            else -> 5.0 // Swiming.
        }

        return caloriesPerMinute * durationInMinutes.coerceAtLeast(1) * weight / 60
    }

    fun calculateTotalCaloriesBurned(age: Int, weight: Int, height: Int, isMale: Boolean,
                                      exerciseType: String, durationInMinutes: Int): Double {
        val bmr = calculateBMR(age, weight, height, isMale)
        val exerciseCaloriesBurned = calculateExerciseCaloriesBurned(exerciseType, durationInMinutes, weight)
        return bmr + exerciseCaloriesBurned
    }
}