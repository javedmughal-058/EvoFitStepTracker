package com.evolixtechnologies.evofit.core.sensors

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.evolixtechnologies.evofit.MainActivity
import com.evolixtechnologies.evofit.R

class StepTrackingService : Service() {
    private lateinit var stepCounter: StepCounterManager

    override fun onCreate() {
        super.onCreate()
        stepCounter = StepCounterManager(this)
        ensureChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        stepCounter.start()
        return START_STICKY
    }

    override fun onDestroy() {
        stepCounter.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification() =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.evofit_app_logo)
            .setContentTitle("EvoFit is tracking steps")
            .setContentText("Step counting continues while the app is in the background.")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Step tracking",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Keeps EvoFit step tracking active in the background."
        }
        manager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "evofit_step_tracking"
        private const val NOTIFICATION_ID = 1001
    }
}
