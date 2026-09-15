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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.evolixtechnologies.evofit.core.camera.HeartRateAnalyzer
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen
import com.evolixtechnologies.evofit.core.designsystem.EvoRed
import java.util.concurrent.Executors

@Composable
fun HeartRateIntroScreen(onStart: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Heart rate", style = MaterialTheme.typography.titleLarge)
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Camera pulse measurement", style = MaterialTheme.typography.titleMedium)
                Text("Place your fingertip gently over the rear camera and flash.", style = MaterialTheme.typography.bodyMedium)
                Text("For best results", style = MaterialTheme.typography.titleSmall)
                Text("• Sit still\n• Keep your hand relaxed\n• Don't press too hard\n• Keep the lens fully covered", style = MaterialTheme.typography.bodyMedium)
                Text("Approx. 30 seconds", style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = EvoGreen, contentColor = Color.Black)) {
            Text("Start measurement")
        }
        Text("For wellness use only. This is not a medical device or diagnosis.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun HeartRateMeasureScreen(onResult: (Int) -> Unit) {
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
    var startedAt by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) { onDispose { executor.shutdown() } }

    Column(Modifier.fillMaxSize().background(Color(0xFF0C0F0C)).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Measuring...", color = Color.White, style = MaterialTheme.typography.titleLarge)
        Text(if (signal.fingerDetected) "Signal detected" else "Cover camera + flash", color = if (signal.fingerDetected) EvoGreen else Color.LightGray, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(12.dp))
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).also { view ->
                    val providerFuture = ProcessCameraProvider.getInstance(ctx)
                    providerFuture.addListener({
                        val provider = providerFuture.get()
                        val preview = Preview.Builder().build().also { it.surfaceProvider = view.surfaceProvider }
                        val analyzer = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
                        analyzer.setAnalyzer(executor, HeartRateAnalyzer { s ->
                            ContextCompat.getMainExecutor(ctx).execute {
                                signal = s
                                if (s.fingerDetected && System.currentTimeMillis() - startedAt > 25_000 && s.bpm != null && s.quality > .95f) {
                                    onResult(s.bpm)
                                }
                            }
                        })
                        try {
                            provider.unbindAll()
                            val camera = provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analyzer)
                            camera.cameraControl.enableTorch(true)
                        } catch (_: Exception) { }
                    }, ContextCompat.getMainExecutor(ctx))
                }
            },
            modifier = Modifier.fillMaxWidth().height(180.dp)
        )
        Spacer(Modifier.height(22.dp))
        Box(Modifier.size(180.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { if (signal.fingerDetected) signal.quality.coerceAtMost(1f) else 0f },
                modifier = Modifier.fillMaxSize(),
                color = EvoGreen,
                trackColor = Color.DarkGray,
                strokeWidth = 10.dp
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(signal.bpm?.toString() ?: "--", color = Color.White, fontSize = 44.sp)
                Text("BPM", color = Color.LightGray, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(if (signal.fingerDetected) "Keep your finger still" else "No finger detected", color = Color.White, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(progress = { signal.quality.coerceAtMost(1f) }, modifier = Modifier.fillMaxWidth(), color = EvoGreen, trackColor = Color.DarkGray)
        Spacer(Modifier.height(8.dp))
        Text("Experimental camera PPG • wellness only", color = EvoRed.copy(alpha = .85f), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun HeartRateResultScreen(bpm: Int, onSave: (String) -> Unit, onAgain: () -> Unit) {
    var context by remember { mutableStateOf("Resting") }
    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Heart Rate result", style = MaterialTheme.typography.titleLarge)
        Text("♥", color = EvoRed, fontSize = 28.sp)
        Text("$bpm", fontSize = 46.sp)
        Text("BPM", style = MaterialTheme.typography.bodyMedium)
        AssistChip(onClick = {}, label = { Text("Wellness estimate") })
        Text("How were you measuring?", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth())
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Resting", "Sitting", "Standing").forEach { value ->
                FilterChip(selected = context == value, onClick = { context = value }, label = { Text(value, fontSize = 10.sp) })
            }
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = { onSave(context) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = EvoGreen, contentColor = Color.Black)) { Text("Save") }
        TextButton(onClick = onAgain) { Text("Measure again") }
    }
}
