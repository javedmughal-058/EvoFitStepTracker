package com.evolixtechnologies.evofit.feature.heartrate

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.evolixtechnologies.evofit.R
import com.evolixtechnologies.evofit.core.camera.HeartRateAnalyzer
import com.evolixtechnologies.evofit.core.designsystem.EvoCard
import com.evolixtechnologies.evofit.core.designsystem.EvoFilterChip
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen
import com.evolixtechnologies.evofit.core.designsystem.EvoMuted
import com.evolixtechnologies.evofit.core.designsystem.EvoPrimaryButton
import com.evolixtechnologies.evofit.core.designsystem.EvoRed
import com.evolixtechnologies.evofit.core.designsystem.EvoTopBar
import java.util.concurrent.Executors
import kotlin.math.sin

@Composable
fun HeartRateIntroScreen(onBack: () -> Unit, onStart: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EvoTopBar("Heart Rate", onBack = onBack)
        Image(
            painter = painterResource(R.drawable.heart_rate_camera_finger),
            contentDescription = "Finger on phone camera",
            modifier = Modifier.fillMaxWidth().height(190.dp)
        )
        Text("Camera pulse measurement", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(
            "Place your fingertip gently over the rear camera and flash.",
            style = MaterialTheme.typography.bodyMedium,
            color = EvoMuted
        )
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = EvoGreen.copy(alpha = .10f))
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text("For best results", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                listOf("Sit still", "Keep your hand relaxed", "Do not press too hard", "Ensure good lighting").forEach {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                        Text("-", style = MaterialTheme.typography.bodyMedium)
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Default.AccessTime, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(22.dp))
            Text("Approx. 30 seconds", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.weight(1f))
        EvoPrimaryButton("Start measurement", onStart)
        Text("For wellness use only. This is not a medical device or diagnosis.", style = MaterialTheme.typography.bodySmall, color = EvoMuted)
    }
}

@Composable
fun HeartRateMeasureScreen(onCancel: () -> Unit, onResult: (Int) -> Unit) {
    val context = LocalContext.current
    var permission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permission = it }
    LaunchedEffect(Unit) { if (!permission) launcher.launch(Manifest.permission.CAMERA) }

    if (!permission) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Camera permission is required", style = MaterialTheme.typography.titleMedium)
                Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) { Text("Allow camera") }
            }
        }
        return
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    var signal by remember { mutableStateOf(HeartRateAnalyzer.SignalState(false, null, 0f, 0)) }
    var measurementStartedAt by remember { mutableStateOf<Long?>(null) }
    var contactLostAt by remember { mutableLongStateOf(0L) }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    val startedAt = measurementStartedAt
    val elapsedSeconds = if (startedAt == null) 0 else ((now - startedAt) / 1000).toInt().coerceAtLeast(0)
    val remainingSeconds = (30 - elapsedSeconds).coerceAtLeast(0)
    val timeProgress = (elapsedSeconds / 30f).coerceIn(0f, 1f)
    val recoveringContact = startedAt != null && !signal.fingerDetected && contactLostAt > 0L && now - contactLostAt <= 2_000L
    val hasUsableSignal = signal.fingerDetected && startedAt != null && signal.quality >= .08f
    val ringProgress = if (hasUsableSignal) timeProgress.coerceAtLeast(.08f) else 0f
    val instruction = when {
        recoveringContact -> "Please align and keep your finger on sensor"
        !signal.fingerDetected -> "Finger not placed on sensor"
        signal.quality < .35f -> "Hold still while signal starts"
        else -> "Keep your finger still"
    }

    DisposableEffect(Unit) { onDispose { executor.shutdown() } }
    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            kotlinx.coroutines.delay(1000)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF071008))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCancel, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Measuring...", color = Color.White, style = MaterialTheme.typography.titleLarge)
            }
            TextButton(onClick = onCancel) { Text("Cancel", color = Color.White.copy(.72f)) }
        }
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).also { view ->
                        view.scaleType = PreviewView.ScaleType.FILL_CENTER
                        val providerFuture = ProcessCameraProvider.getInstance(ctx)
                        providerFuture.addListener({
                            val provider = providerFuture.get()
                            val preview = Preview.Builder().build().also { it.surfaceProvider = view.surfaceProvider }
                            val analyzer = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
                            analyzer.setAnalyzer(executor, HeartRateAnalyzer { s ->
                                ContextCompat.getMainExecutor(ctx).execute {
                                    signal = s
                                    val current = System.currentTimeMillis()
                                    if (s.fingerDetected) {
                                        contactLostAt = 0L
                                        if (measurementStartedAt == null) measurementStartedAt = current
                                    } else {
                                        if (measurementStartedAt != null) {
                                            if (contactLostAt == 0L) contactLostAt = current
                                            if (current - contactLostAt > 2_000L) {
                                                measurementStartedAt = null
                                                contactLostAt = 0L
                                            }
                                        }
                                    }
                                    val activeStartedAt = measurementStartedAt
                                    if (s.fingerDetected && activeStartedAt != null && current - activeStartedAt > 25_000 && s.bpm != null && s.quality > .95f) {
                                        onResult(s.bpm)
                                    }
                                }
                            })
                            try {
                                provider.unbindAll()
                                val camera = provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analyzer)
                                camera.cameraControl.enableTorch(true)
                            } catch (_: Exception) {
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                    }
                },
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            instruction,
            color = if (hasUsableSignal) Color.White.copy(.78f) else EvoRed,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(18.dp))
        Box(Modifier.size(204.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { ringProgress },
                modifier = Modifier.fillMaxSize(),
                color = EvoGreen,
                trackColor = Color.White.copy(alpha = .18f),
                strokeWidth = 9.dp,
                strokeCap = StrokeCap.Round
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Favorite, null, tint = EvoRed, modifier = Modifier.size(26.dp))
                Text(if (hasUsableSignal) signal.bpm?.toString() ?: "--" else "--", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Text("BPM", color = Color.White.copy(.68f), style = MaterialTheme.typography.bodyMedium)
            }
        }
        HeartWave(Modifier.fillMaxWidth().height(88.dp), animatedSeed = elapsedSeconds, active = hasUsableSignal)
        LinearProgressIndicator(
            progress = { timeProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(16.dp)),
            color = EvoGreen,
            trackColor = Color.White.copy(alpha = .12f)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            when {
                recoveringContact -> "Measurement paused. Re-align within 2 seconds"
                hasUsableSignal -> "$remainingSeconds seconds remaining"
                else -> "Waiting for finger placement"
            },
            color = Color.White.copy(.72f),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(12.dp))
        Text(instruction, color = Color.White.copy(.82f), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(18.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF142018))
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Signal quality", color = Color.White.copy(.80f), style = MaterialTheme.typography.bodyMedium)
                    Text(
                        when {
                            !signal.fingerDetected -> "No finger"
                            signal.quality < .35f -> "Adjust"
                            else -> "Good"
                        },
                        color = if (hasUsableSignal) EvoGreen else EvoRed,
                        fontWeight = FontWeight.Bold
                    )
                }
                SignalBars(if (signal.fingerDetected) signal.quality else 0f)
            }
        }
    }
}

@Composable
fun HeartRateResultScreen(bpm: Int, onSave: (String) -> Unit, onAgain: () -> Unit) {
    var context by remember { mutableStateOf("Resting") }
    val displayBpm = bpm.takeIf { it > 0 }?.toString() ?: "--"
    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        EvoTopBar("Heart Rate Result")
        Icon(Icons.Default.Favorite, null, tint = EvoRed, modifier = Modifier.size(32.dp))
        Text(displayBpm, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Text("BPM", style = MaterialTheme.typography.bodyMedium)
        AssistChip(
            onClick = {},
            label = { Text("Normal resting range") },
            colors = AssistChipDefaults.assistChipColors(containerColor = EvoGreen.copy(.12f), labelColor = Color(0xFF217600))
        )
        Text("15 Sep 2025 - 9:42 PM", style = MaterialTheme.typography.bodySmall, color = EvoMuted)
        Text("How were you measuring?", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth())
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Resting", "Sitting").forEach { value ->
                    EvoFilterChip(selected = context == value, text = value, onClick = { context = value }, modifier = Modifier.weight(1f))
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Standing", "After exercise").forEach { value ->
                    EvoFilterChip(selected = context == value, text = value, onClick = { context = value }, modifier = Modifier.weight(1f))
                }
            }
        }
        OutlinedTextField("", {}, label = { Text("Notes (optional)") }, modifier = Modifier.fillMaxWidth().height(72.dp), readOnly = true)
        Spacer(Modifier.weight(1f))
        EvoPrimaryButton("Save", { onSave(context) })
        TextButton(onClick = onAgain) { Text("Measure again") }
    }
}

@Composable
private fun SignalBars(quality: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom) {
        repeat(5) { index ->
            val active = quality >= (index + 1) * .18f
            Box(
                Modifier
                    .size(width = 5.dp, height = (12 + index * 5).dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(if (active) EvoGreen else Color.White.copy(alpha = .20f))
            )
        }
    }
}

@Composable
private fun HeartWave(modifier: Modifier, animatedSeed: Int, active: Boolean) {
    Canvas(modifier) {
        val mid = size.height * .55f
        var last = Offset(0f, mid)
        for (i in 1..80) {
            val x = size.width * i / 80f
            val y = if (active) mid + sin((i + animatedSeed) * .55f) * 18f + sin(i * 1.4f) * 7f else mid
            val next = Offset(x, y)
            drawLine(if (active) EvoGreen else Color.White.copy(alpha = .22f), last, next, strokeWidth = 3f)
            last = next
        }
    }
}
