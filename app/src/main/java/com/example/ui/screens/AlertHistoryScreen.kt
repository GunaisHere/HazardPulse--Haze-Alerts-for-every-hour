package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AlertHistoryScreen(
    alerts: List<HazardAlertEntity>,
    onPlayVoice: (HazardType) -> Unit,
    onDismissAlert: (Long) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormatter = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "CRITICAL ALERT HISTORY",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "${alerts.size} logged events in local database",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            if (alerts.isNotEmpty()) {
                IconButton(
                    onClick = onClearAll,
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear History",
                        tint = HazardRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (alerts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.NotificationsOff,
                        contentDescription = "No Alerts",
                        tint = SlateDark700,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No critical alerts recorded.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "Real-time conditions are safe or hourly checks found no hazards.",
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(alerts, key = { it.id }) { alert ->
                    AlertHistoryCard(
                        alert = alert,
                        timeFormatter = timeFormatter,
                        onPlayVoice = onPlayVoice,
                        onDismiss = { onDismissAlert(alert.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AlertHistoryCard(
    alert: HazardAlertEntity,
    timeFormatter: SimpleDateFormat,
    onPlayVoice: (HazardType) -> Unit,
    onDismiss: () -> Unit
) {
    val hazardType = try {
        HazardType.valueOf(alert.type)
    } catch (e: Exception) {
        HazardType.HAZE
    }

    val typeColor = when (hazardType) {
        HazardType.HAZE -> HazardOrange
        HazardType.RAIN -> HazardBlue
        HazardType.EARTHQUAKE -> HazardPurple
        HazardType.TSUNAMI -> Color(0xFF0284C7)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateDark800),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(typeColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (hazardType) {
                                HazardType.HAZE -> Icons.Default.Air
                                HazardType.RAIN -> Icons.Default.Thunderstorm
                                HazardType.EARTHQUAKE -> Icons.Default.Public
                                HazardType.TSUNAMI -> Icons.Default.WaterDrop
                            },
                            contentDescription = alert.type,
                            tint = typeColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = alert.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (alert.wasDndSilenced) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF312E81)
                                ) {
                                    Text(
                                        text = "🌙 DND SILENCED",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFA5B4FC),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = timeFormatter.format(Date(alert.timestamp)),
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Dismiss",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = alert.description,
                fontSize = 12.sp,
                color = Color(0xFFCBD5E1),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊 ${alert.primaryMetric}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = typeColor
                )

                Button(
                    onClick = { onPlayVoice(hazardType) },
                    colors = ButtonDefaults.buttonColors(containerColor = SlateDark700),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Replay Alert",
                        tint = typeColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Replay Voice", fontSize = 10.sp, color = Color.White)
                }
            }
        }
    }
}
