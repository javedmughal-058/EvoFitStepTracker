package com.evolixtechnologies.evofit.core.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.util.ArrayDeque
import kotlin.math.roundToInt

/**
 * Experimental phone-camera PPG estimator for wellness use only.
 * It analyzes luminance changes from a fingertip covering camera + flash.
 */
class HeartRateAnalyzer(
    private val onSignal: (SignalState) -> Unit
) : ImageAnalysis.Analyzer {

    data class SignalState(
        val fingerDetected: Boolean,
        val bpm: Int?,
        val quality: Float,
        val samples: Int
    )

    private data class Point(val t: Long, val v: Double)
    private val points = ArrayDeque<Point>()

    override fun analyze(image: ImageProxy) {
        try {
            val now = System.currentTimeMillis()
            val yPlane = image.planes[0].buffer
            val remaining = yPlane.remaining()
            if (remaining <= 0) return

            val bytes = ByteArray(remaining)
            yPlane.get(bytes)
            val stride = (remaining / 2500).coerceAtLeast(1)
            var sum = 0L
            var count = 0
            var i = 0
            while (i < bytes.size) {
                sum += (bytes[i].toInt() and 0xFF)
                count++
                i += stride
            }
            val avg = if (count == 0) 0.0 else sum.toDouble() / count
            val finger = avg > 55.0

            if (!finger) {
                points.clear()
                onSignal(SignalState(false, null, 0f, 0))
                return
            }

            points.addLast(Point(now, avg))
            while (points.isNotEmpty() && now - points.first().t > 30_000) points.removeFirst()

            val bpm = estimateBpm(points.toList())
            val duration = if (points.size > 1) (points.last().t - points.first().t) else 0L
            val quality = (duration / 25_000f).coerceIn(0f, 1f)
            onSignal(SignalState(true, bpm, quality, points.size))
        } finally {
            image.close()
        }
    }

    private fun estimateBpm(values: List<Point>): Int? {
        if (values.size < 30) return null
        val mean = values.map { it.v }.average()
        val centered = values.map { Point(it.t, it.v - mean) }

        val peaks = mutableListOf<Long>()
        var lastPeak = 0L
        for (i in 1 until centered.lastIndex) {
            val a = centered[i - 1]
            val b = centered[i]
            val c = centered[i + 1]
            if (b.v > a.v && b.v >= c.v && b.v > 1.2 && b.t - lastPeak > 320) {
                peaks += b.t
                lastPeak = b.t
            }
        }
        if (peaks.size < 3) return null
        val rr = peaks.zipWithNext { a, b -> b - a }.filter { it in 330..1500 }
        if (rr.size < 2) return null
        val avgRr = rr.average()
        val bpm = (60_000.0 / avgRr).roundToInt()
        return bpm.takeIf { it in 40..180 }
    }
}
