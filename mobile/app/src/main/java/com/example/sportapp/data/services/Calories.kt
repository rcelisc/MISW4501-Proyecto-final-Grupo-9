package com.example.sportapp.data.services

class Calories {

    fun calculateExerciseCaloriesBurned(exerciseType: String, durationInMinutes: Int, weight: Float): Double {
        val metValue: Double = when (exerciseType.lowercase()) {
            "running" -> 10.0
            "cycling" -> 8.0
            else -> 5.0 // Swimming or default value.
        }

        return (durationInMinutes * metValue * weight) / 200
    }

    fun calculateTotalCaloriesBurned(exerciseType: String, durationInMinutes: Int, weight: Float): Double {
        // Return 0 calories burned if the duration is less than a minute
        if (durationInMinutes < 1) return 0.0

        return calculateExerciseCaloriesBurned(exerciseType, durationInMinutes, weight)
    }
}
