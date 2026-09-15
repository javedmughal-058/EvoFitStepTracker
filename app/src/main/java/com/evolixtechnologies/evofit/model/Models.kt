package com.evolixtechnologies.evofit.domain.model

data class DailyActivity(
    val steps: Int,
    val goal: Int = 8000,
    val distanceKm: Double,
    val calories: Int,
    val activeMinutes: Int
)

data class HeartRateSample(
    val bpm: Int,
    val timestamp: Long,
    val context: String = "Resting"
)

data class BloodPressureSample(
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int?,
    val timestamp: Long
)
