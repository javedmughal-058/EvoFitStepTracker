package com.evolixtechnologies.evofit.data.repository

import com.evolixtechnologies.evofit.data.local.BloodPressureEntity
import com.evolixtechnologies.evofit.data.local.HealthDao
import com.evolixtechnologies.evofit.data.local.HeartRateEntity
import com.evolixtechnologies.evofit.data.local.ActivityLogEntity
import com.evolixtechnologies.evofit.data.local.SleepLogEntity
import kotlinx.coroutines.flow.Flow

class HealthRepository(private val dao: HealthDao) {
    fun observeHeartRate(): Flow<List<HeartRateEntity>> = dao.observeHeartRate()
    fun observeBloodPressure(): Flow<List<BloodPressureEntity>> = dao.observeBloodPressure()
    fun observeActivity(): Flow<List<ActivityLogEntity>> = dao.observeActivity()
    fun observeSleep(): Flow<List<SleepLogEntity>> = dao.observeSleep()

    suspend fun saveHeartRate(bpm: Int, context: String) {
        dao.insertHeartRate(HeartRateEntity(bpm = bpm, context = context, timestamp = System.currentTimeMillis()))
    }

    suspend fun saveBloodPressure(sys: Int, dia: Int, pulse: Int?) {
        dao.insertBloodPressure(BloodPressureEntity(systolic = sys, diastolic = dia, pulse = pulse, timestamp = System.currentTimeMillis()))
    }

    suspend fun saveActivity(title: String, minutes: Int, calories: Int) {
        dao.insertActivity(ActivityLogEntity(title = title, minutes = minutes, calories = calories, timestamp = System.currentTimeMillis()))
    }

    suspend fun saveSleep(bedtime: String, wakeTime: String, durationMinutes: Int, quality: String, notes: String) {
        dao.insertSleep(
            SleepLogEntity(
                bedtime = bedtime,
                wakeTime = wakeTime,
                durationMinutes = durationMinutes,
                quality = quality,
                notes = notes,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}
