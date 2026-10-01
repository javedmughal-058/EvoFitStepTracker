package com.evolixtechnologies.evofit.core.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.LocalDateTime
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

    private val _steps = MutableStateFlow(if (prefs.getString("date", null) == LocalDate.now().toString()) prefs.getInt("last_steps", 0) else 0)
    val steps: StateFlow<Int> = _steps

    private val _paused = MutableStateFlow(prefs.getBoolean("paused", false))
    val paused: StateFlow<Boolean> = _paused
    private var listenerRegistered = false

    private val preferencesListener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { shared, key ->
        if (key == "last_steps") {
            _steps.value = if (shared.getString("date", null) == LocalDate.now().toString()) shared.getInt("last_steps", 0) else 0
        } else if (key == "paused") {
            _paused.value = shared.getBoolean("paused", false)
        }
    }

    fun start() {
        if (!listenerRegistered) {
            prefs.registerOnSharedPreferenceChangeListener(preferencesListener)
            listenerRegistered = true
        }
        stepSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        if (listenerRegistered) {
            prefs.unregisterOnSharedPreferenceChangeListener(preferencesListener)
            listenerRegistered = false
        }
    }

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
        val editor = prefs.edit()
            .putString("date", today)
            .putFloat("baseline", current)
            .putInt("base_steps", 0)
            .putInt("last_steps", 0)
            .putInt("steps_$today", 0)
        (0..23).forEach { editor.remove("hour_${today}_$it") }
        editor.apply()
        _steps.value = 0
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val cumulative = event?.values?.firstOrNull() ?: return
        val today = LocalDate.now().toString()
        val savedDate = prefs.getString("date", null)
        var baseline = prefs.getFloat("baseline", -1f)
        prefs.edit().putFloat("last_cumulative", cumulative).apply()

        val newDay = savedDate != today
        if (newDay || baseline < 0f || cumulative < baseline) {
            val carriedSteps = if (newDay) 0 else prefs.getInt("steps_$today", 0)
            baseline = cumulative
            prefs.edit()
                .putString("date", today)
                .putFloat("baseline", baseline)
                .putInt("base_steps", carriedSteps)
                .putInt("last_steps", carriedSteps)
                .putInt("steps_$today", carriedSteps)
                .putBoolean("paused", false)
                .remove("pause_cumulative")
                .apply()
            _paused.value = false
        }

        if (prefs.getBoolean("paused", false)) {
            return
        }

        val todaySteps = prefs.getInt("base_steps", 0) + max(0, (cumulative - baseline).toInt())
        val previousSteps = prefs.getInt("steps_$today", 0)
        val hour = LocalDateTime.now().hour
        val hourlyKey = "hour_${today}_$hour"
        val goalKey = "goal_$today"
        val configuredGoal = appContext.getSharedPreferences("evofit_app", Context.MODE_PRIVATE)
            .getInt("step_goal", 8000)
        _steps.value = todaySteps
        val editor = prefs.edit()
            .putInt("last_steps", todaySteps)
            .putInt("steps_$today", todaySteps)
            .putInt(hourlyKey, prefs.getInt(hourlyKey, 0) + (todaySteps - previousSteps).coerceAtLeast(0))
        if (!prefs.contains(goalKey)) editor.putInt(goalKey, configuredGoal)
        editor.apply()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun updateStepsForCurrentBaseline(cumulative: Float) {
        if (cumulative < 0f) return
        val today = LocalDate.now().toString()
        val baseline = prefs.getFloat("baseline", cumulative)
        val todaySteps = prefs.getInt("base_steps", 0) + max(0, (cumulative - baseline).toInt())
        _steps.value = todaySteps
        prefs.edit()
            .putInt("last_steps", todaySteps)
            .putInt("steps_$today", todaySteps)
            .apply()
    }
}
