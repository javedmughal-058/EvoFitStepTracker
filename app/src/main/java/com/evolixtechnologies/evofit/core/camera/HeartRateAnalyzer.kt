package com.evolixtechnologies.evofit.core.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.util.ArrayDeque
import kotlin.math.max
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
    private data class FrameStats(val luminance: Double, val red: Double, val green: Double, val blue: Double)

    private val points = ArrayDeque<Point>()
    private var consecutiveFingerFrames = 0

    override fun analyze(image: ImageProxy) {
        try {
            val now = System.currentTimeMillis()
            val stats = readFrameStats(image) ?: return
            val warmDominance = stats.red - max(stats.green, stats.blue)
            val fingerCandidate = stats.luminance > 70.0 &&
                stats.red > stats.green * 1.08 &&
                stats.red > stats.blue * 1.12 &&
                warmDominance > 10.0

            consecutiveFingerFrames = if (fingerCandidate) {
                (consecutiveFingerFrames + 1).coerceAtMost(8)
            } else {
                0
            }
            val finger = consecutiveFingerFrames >= 3

            if (!finger) {
                points.clear()
                onSignal(SignalState(false, null, 0f, 0))
                return
            }

            points.addLast(Point(now, stats.luminance))
            while (points.isNotEmpty() && now - points.first().t > 30_000) points.removeFirst()

            val bpm = estimateBpm(points.toList())
            val duration = if (points.size > 1) (points.last().t - points.first().t) else 0L
            val quality = (duration / 25_000f).coerceIn(0f, 1f)
            onSignal(SignalState(true, bpm, quality, points.size))
        } finally {
            image.close()
        }
    }

    private fun readFrameStats(image: ImageProxy): FrameStats? {
        if (image.planes.size < 3) return readLuminanceStats(image)

        val yPlane = image.planes[0]
        val uPlane = image.planes[1]
        val vPlane = image.planes[2]
        val yBuffer = yPlane.buffer
        val uBuffer = uPlane.buffer
        val vBuffer = vPlane.buffer
        val stepX = (image.width / 28).coerceAtLeast(1)
        val stepY = (image.height / 28).coerceAtLeast(1)
        var ySum = 0.0
        var rSum = 0.0
        var gSum = 0.0
        var bSum = 0.0
        var count = 0

        var y = 0
        while (y < image.height) {
            var x = 0
            while (x < image.width) {
                val yIndex = y * yPlane.rowStride + x * yPlane.pixelStride
                val chromaX = x / 2
                val chromaY = y / 2
                val uIndex = chromaY * uPlane.rowStride + chromaX * uPlane.pixelStride
                val vIndex = chromaY * vPlane.rowStride + chromaX * vPlane.pixelStride

                if (yIndex < yBuffer.limit() && uIndex < uBuffer.limit() && vIndex < vBuffer.limit()) {
                    val yf = (yBuffer.get(yIndex).toInt() and 0xFF).toFloat()
                    val uf = (uBuffer.get(uIndex).toInt() and 0xFF) - 128f
                    val vf = (vBuffer.get(vIndex).toInt() and 0xFF) - 128f
                    val red = (yf + 1.402f * vf).coerceIn(0f, 255f)
                    val green = (yf - 0.344136f * uf - 0.714136f * vf).coerceIn(0f, 255f)
                    val blue = (yf + 1.772f * uf).coerceIn(0f, 255f)
                    ySum += yf
                    rSum += red
                    gSum += green
                    bSum += blue
                    count++
                }
                x += stepX
            }
            y += stepY
        }

        return if (count == 0) null else FrameStats(
            luminance = ySum / count,
            red = rSum / count,
            green = gSum / count,
            blue = bSum / count
        )
    }

    private fun readLuminanceStats(image: ImageProxy): FrameStats? {
        val yPlane = image.planes.firstOrNull() ?: return null
        val yBuffer = yPlane.buffer
        val stepX = (image.width / 28).coerceAtLeast(1)
        val stepY = (image.height / 28).coerceAtLeast(1)
        var sum = 0.0
        var count = 0
        var y = 0
        while (y < image.height) {
            var x = 0
            while (x < image.width) {
                val index = y * yPlane.rowStride + x * yPlane.pixelStride
                if (index < yBuffer.limit()) {
                    sum += (yBuffer.get(index).toInt() and 0xFF)
                    count++
                }
                x += stepX
            }
            y += stepY
        }
        if (count == 0) return null
        val luminance = sum / count
        return FrameStats(luminance = luminance, red = luminance, green = luminance, blue = luminance)
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
