package com.evolixtechnologies.evofit.feature.about

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evolixtechnologies.evofit.R
import com.evolixtechnologies.evofit.core.designsystem.EvoBlue
import com.evolixtechnologies.evofit.core.designsystem.EvoCard
import com.evolixtechnologies.evofit.core.designsystem.EvoGreen
import com.evolixtechnologies.evofit.core.designsystem.EvoMuted

@Composable
fun AboutScreen() {
    val context = LocalContext.current
    val darkTheme = MaterialTheme.colorScheme.background.luminance() < .5f
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("About", style = MaterialTheme.typography.titleLarge)
            Text("App version and company information.", style = MaterialTheme.typography.bodyLarge, color = EvoMuted)
        }

        EvoCard {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
//                Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp), color = EvoGreen.copy(alpha = .10f)) {
//                    Image(
//                        painterResource(R.drawable.evofit_app_logo),
//                        contentDescription = "EvoFit",
//                        modifier = Modifier.padding(16.dp).size(118.dp)
//                    )
//                }
                BrandTitle()

                Text("Step Tracker", style = MaterialTheme.typography.titleSmall, color = EvoMuted)
                Surface(shape = CircleShape, color = EvoGreen.copy(alpha = .13f)) {
                    Text("Version 1.0.0", modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp), style = MaterialTheme.typography.bodySmall, color = Color(0xFF087816))
                }
                Text("Small Steps. Big Progress.", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge, color = EvoMuted)
            }
        }

        EvoCard {
            Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                ContactRow(Icons.Default.Email, "info@evolixtechnologies.com", EvoGreen) {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:info@evolixtechnologies.com")
                        putExtra(Intent.EXTRA_SUBJECT, "EvoFit support")
                    }
                    runCatching {
                        context.startActivity(Intent.createChooser(intent, "Contact Evolix"))
                    }.onFailure {
                        Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .55f))
                ContactRow(Icons.Default.Language, "evolixtechnologies.com", EvoBlue) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://evolixtechnologies.com/"))
                    runCatching {
                        context.startActivity(intent)
                    }.onFailure {
                        Toast.makeText(context, "No browser app found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        EvoCard {
            Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                AboutLink("Privacy Policy")
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .55f))
                AboutLink("Terms of Use")
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .55f))
                AboutLink("Open Source Licenses")
            }
        }

        EvoCard {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Developed by", style = MaterialTheme.typography.titleSmall)
                Image(
                    painterResource(if (darkTheme) R.drawable.evolix_logo else R.drawable.evolix_logo_dark),
                    contentDescription = "Evolix Technologies",
                    modifier = Modifier.fillMaxWidth(.86f).height(78.dp)
                )
//                Text("Evolix Technologies", style = MaterialTheme.typography.bodyLarge, color = EvoMuted)
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            "(c) 2026 Evolix Technologies\nAll rights reserved.",
            color = EvoMuted,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun BrandTitle() {
    Row(verticalAlignment = Alignment.Bottom) {
        Text("Evo", style = MaterialTheme.typography.titleLarge, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold)
        Text("Fit", style = MaterialTheme.typography.titleLarge, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = EvoGreen)
    }
}

@Composable
private fun ContactRow(icon: ImageVector, text: String, tint: Color, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(42.dp).background(tint.copy(alpha = .12f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = tint)
        }
        Text(text, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
        Icon(Icons.Default.ChevronRight, null, tint = EvoMuted)
    }
}

@Composable
private fun AboutLink(text: String) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Icon(Icons.Default.ChevronRight, null, tint = EvoMuted)
    }
}
