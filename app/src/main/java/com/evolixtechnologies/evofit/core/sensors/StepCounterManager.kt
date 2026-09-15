package com.evolixtechnologies.evofit.core.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import kotlin.math.max

/**
 * Uses Android's hardware step counter (cumulative since boot) and persists a daily baseline.
 * This keeps today's count stable across app restarts and resets on the next day's first event.
 */
class StepCounterManager(context: Context) : SensorEventListener {
    private val appContext = context.applicationContext
    private val sensorManager = appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val prefs = appContext.getSharedPreferences("evofit_steps", Context.MODE_PRIVATE)

    private val _steps = MutableStateFlow(prefs.getInt("last_steps", 0))
    val steps: StateFlow<Int> = _steps

    fun start() {
        stepSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    fun stop() = sensorManager.unregisterListener(this)

    fun isSupported(): Boolean = stepSensor != null

    override fun onSensorChanged(event: SensorEvent?) {
        val cumulative = event?.values?.firstOrNull() ?: return
        val today = LocalDate.now().toString()
        val savedDate = prefs.getString("date", null)
        var baseline = prefs.getFloat("baseline", -1f)

        if (savedDate != today || baseline < 0f || cumulative < baseline) {
            baseline = cumulative
            prefs.edit().putString("date", today).putFloat("baseline", baseline).putInt("last_steps", 0).apply()
        }

        val todaySteps = max(0, (cumulative - baseline).toInt())
        _steps.value = todaySteps
        prefs.edit().putInt("last_steps", todaySteps).apply()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
