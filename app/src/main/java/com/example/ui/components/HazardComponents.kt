package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.HazardAlertEntity
import com.example.data.local.HazardType
import com.example.ui.theme.HazardAmber
import com.example.ui.theme.HazardBlue
import com.example.ui.theme.HazardGreen
import com.example.ui.theme.HazardOrange
import com.example.ui.theme.HazardPurple
import com.example.ui.theme.HazardRed
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark700
import com.example.ui.theme.SlateDark800

@Composable
fun CriticalAlertBanner(
    alert: HazardAlertEntity,
    onPlayVoice: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hazardType = try {
        HazardType.valueOf(alert.type)
    } catch (e: Exception) {
        HazardType.HAZE
    }

    val bannerBrush = when (hazardType) {
        HazardType.HAZE -> Brush.horizontalGradient(listOf(Color(0xFF7C2D12), Color(0xFFC2410C)))
        HazardType.RAIN -> Brush.horizontalGradient(listOf(Color(0xFF1E3A8A), Color(0xFF2563EB)))
        HazardType.EARTHQUAKE -> Brush.horizontalGradient(listOf(Color(0xFF7F1D1D), Color(0xFFDC2626)))
        HazardType.TSUNAMI -> Brush.horizontalGradient(listOf(Color(0xFF0F766E), Color(0xFF0284C7)))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("critical_alert_banner_${alert.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(bannerBrush)
                .border(1.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (hazardType) {
                                    HazardType.HAZE -> Icons.Default.Air
                                    HazardType.RAIN -> Icons.Default.Thunderstorm
                                    HazardType.EARTHQUAKE -> Icons.Default.Public
                                    HazardType.TSUNAMI -> Icons.Default.WaterDrop
                                },
                                contentDescription = "Hazard Icon",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = alert.title.uppercase(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                if (alert.wasDndSilenced) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color.Black.copy(alpha = 0.4f)
                                    ) {
                                        Text(
                                            text = "🌙 DND SILENCED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFDE68A),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = alert.primaryMetric,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("dismiss_alert_button_${alert.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss Alert",
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = alert.description,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📍 ${alert.locationText}",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Button(
                        onClick = onPlayVoice,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("play_voice_alert_${alert.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Replay Alert Voice",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Replay Voice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AqiSeverityBadge(aqi: Int, modifier: Modifier = Modifier) {
    val (label, bg, fg) = when {
        aqi <= 50 -> Triple("Good (Safe)", HazardGreen, Color.White)
        aqi <= 100 -> Triple("Moderate", HazardAmber, Color.Black)
        aqi <= 150 -> Triple("Sensitive Warning", HazardOrange, Color.White)
        aqi <= 200 -> Triple("Unhealthy Critical", HazardRed, Color.White)
        aqi <= 300 -> Triple("Very Unhealthy", HazardPurple, Color.White)
        else -> Triple("Hazardous Emergency", Color(0xFF881337), Color.White)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bg,
        modifier = modifier
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
fun MetricChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isWarning: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isWarning) HazardRed.copy(alpha = 0.15f) else SlateDark700,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isWarning) HazardRed.copy(alpha = 0.5f) else SlateBorder
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = if (isWarning) HazardRed else Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
