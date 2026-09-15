package com.evolixtechnologies.evofit.feature.activity

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(steps: Int) {
    var tab by remember { mutableIntStateOf(0) }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Activity", style = MaterialTheme.typography.titleLarge)
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            listOf("Day", "Week", "Month").forEachIndexed { index, label ->
                SegmentedButton(
                    selected = tab == index,
                    onClick = { tab = index },
                    shape = SegmentedButtonDefaults.itemShape(index, 3),
                    label = { Text(label, fontSize = 12.sp) }
                )
            }
        }
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(if (tab == 0) "Today" else if (tab == 1) "This week" else "This month", style = MaterialTheme.typography.titleSmall)
                Text("%,d".format(if (tab == 0) steps else if (tab == 1) steps * 5 else steps * 22), fontSize = 32.sp)
                Text("Steps", style = MaterialTheme.typography.bodyMedium)
                BarChart(modifier = Modifier.fillMaxWidth().height(180.dp), steps = steps)
                HorizontalDivider()
                Text("Distance   %.1f km".format(steps * 0.00078), style = MaterialTheme.typography.bodyMedium)
                Text("Calories   ${(steps * 0.04).toInt()} kcal", style = MaterialTheme.typography.bodyMedium)
                Text("Active time   ${steps / 110} min", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable private fun BarChart(modifier: Modifier, steps: Int) {
    val bars = remember(steps) { List(12) { i -> ((steps / 12f) * (0.35f + (i % 5) * 0.15f)).coerceAtLeast(40f) } }
    Canvas(modifier) {
        val max = bars.maxOrNull() ?: 1f
        val gap = size.width / bars.size
        bars.forEachIndexed { i, v ->
            val h = size.height * (v / max) * .9f
            drawLine(EvoGreen, Offset(gap * i + gap / 2, size.height), Offset(gap * i + gap / 2, size.height - h), strokeWidth = 12f)
        }
        drawLine(Color.LightGray, Offset(0f, size.height), Offset(size.width, size.height), strokeWidth = 1f)
    }
}
