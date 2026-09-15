package com.evolixtechnologies.evofit.data.repository

import com.evolixtechnologies.evofit.data.local.BloodPressureEntity
import com.evolixtechnologies.evofit.data.local.HealthDao
import com.evolixtechnologies.evofit.data.local.HeartRateEntity
import kotlinx.coroutines.flow.Flow

class HealthRepository(private val dao: HealthDao) {
    fun observeHeartRate(): Flow<List<HeartRateEntity>> = dao.observeHeartRate()
    fun observeBloodPressure(): Flow<List<BloodPressureEntity>> = dao.observeBloodPressure()

    suspend fun saveHeartRate(bpm: Int, context: String) {
        dao.insertHeartRate(HeartRateEntity(bpm = bpm, context = context, timestamp = System.currentTimeMillis()))
    }

    suspend fun saveBloodPressure(sys: Int, dia: Int, pulse: Int?) {
        dao.insertBloodPressure(BloodPressureEntity(systolic = sys, diastolic = dia, pulse = pulse, timestamp = System.currentTimeMillis()))
    }
}
