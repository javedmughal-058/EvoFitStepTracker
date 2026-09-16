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

    private val _paused = MutableStateFlow(prefs.getBoolean("paused", false))
    val paused: StateFlow<Boolean> = _paused

    fun start() {
        stepSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    fun stop() = sensorManager.unregisterListener(this)

    fun isSupported(): Boolean = stepSensor != null

    fun pause() {
        _paused.value = true
        prefs.edit()
            .putBoolean("paused", true)
            .putFloat("pause_cumulative", prefs.getFloat("last_cumulative", prefs.getFloat("baseline", -1f)))
            .apply()
    }

    fun resume() {
        val current = prefs.getFloat("last_cumulative", -1f)
        val pausedAt = prefs.getFloat("pause_cumulative", current)
        val baseline = prefs.getFloat("baseline", -1f)
        val editor = prefs.edit().putBoolean("paused", false).remove("pause_cumulative")
        if (baseline >= 0f && current >= pausedAt) {
            editor.putFloat("baseline", baseline + (current - pausedAt))
        }
        editor.apply()
        _paused.value = false
        updateStepsForCurrentBaseline(current)
    }

    fun resetToday() {
        val today = LocalDate.now().toString()
        val current = prefs.getFloat("last_cumulative", prefs.getFloat("baseline", 0f))
        prefs.edit()
            .putString("date", today)
            .putFloat("baseline", current)
            .putInt("last_steps", 0)
            .putInt("steps_$today", 0)
            .apply()
        _steps.value = 0
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val cumulative = event?.values?.firstOrNull() ?: return
        val today = LocalDate.now().toString()
        val savedDate = prefs.getString("date", null)
        var baseline = prefs.getFloat("baseline", -1f)
        prefs.edit().putFloat("last_cumulative", cumulative).apply()

        if (savedDate != today || baseline < 0f || cumulative < baseline) {
            baseline = cumulative
            prefs.edit()
                .putString("date", today)
                .putFloat("baseline", baseline)
                .putInt("last_steps", 0)
                .putInt("steps_$today", 0)
                .putBoolean("paused", false)
                .remove("pause_cumulative")
                .apply()
            _paused.value = false
        }

        if (_paused.value) {
            return
        }

        val todaySteps = max(0, (cumulative - baseline).toInt())
        _steps.value = todaySteps
        prefs.edit()
            .putInt("last_steps", todaySteps)
            .putInt("steps_$today", todaySteps)
            .apply()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun updateStepsForCurrentBaseline(cumulative: Float) {
        if (cumulative < 0f) return
        val today = LocalDate.now().toString()
        val baseline = prefs.getFloat("baseline", cumulative)
        val todaySteps = max(0, (cumulative - baseline).toInt())
        _steps.value = todaySteps
        prefs.edit()
            .putInt("last_steps", todaySteps)
            .putInt("steps_$today", todaySteps)
            .apply()
    }
}
