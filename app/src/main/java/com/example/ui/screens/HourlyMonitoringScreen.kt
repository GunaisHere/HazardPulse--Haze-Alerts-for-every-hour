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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.local.HourlyReadingEntity
import com.example.ui.theme.HazardAmber
import com.example.ui.theme.HazardBlue
import com.example.ui.theme.HazardGreen
import com.example.ui.theme.HazardOrange
import com.example.ui.theme.HazardRed
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark700
import com.example.ui.theme.SlateDark800
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HourlyMonitoringScreen(
    isHourlyEnabled: Boolean,
    hourlyReadings: List<HourlyReadingEntity>,
    onToggleHourly: (Boolean) -> Unit,
    onRunManualCheck: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Toggle Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDark800),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isHourlyEnabled) HazardGreen.copy(alpha = 0.2f) else SlateDark700),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Hourly Watchdog",
                                tint = if (isHourlyEnabled) HazardGreen else Color(0xFF94A3B8),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "HOURLY GPS MONITORING",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = if (isHourlyEnabled) "Active background alarm every 60 min" else "Hourly background polling paused",
                                fontSize = 12.sp,
                                color = if (isHourlyEnabled) HazardGreen else Color(0xFF94A3B8)
                            )
                        }
                    }

                    Switch(
                        checked = isHourlyEnabled,
                        onCheckedChange = onToggleHourly,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = HazardGreen,
                            uncheckedThumbColor = Color(0xFF94A3B8),
                            uncheckedTrackColor = SlateDark700
                        ),
                        modifier = Modifier.testTag("hourly_monitoring_switch")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                // Critical Alert Policy Explanation
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SlateDark700.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Policy",
                            tint = HazardOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Filtered Alert Policy: Checks happen hourly in the background, but alerts and spoken voice are ONLY triggered if conditions are truly critical (AQI > 150, torrential rain, or nearby earthquakes).",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onRunManualCheck,
                    colors = ButtonDefaults.buttonColors(containerColor = SlateDark700),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("run_hourly_check_now_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Check Now",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Trigger Immediate Hourly Check", fontSize = 12.sp, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // History Log Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "HOURLY READINGS DATABASE (${hourlyReadings.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (hourlyReadings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "No Readings",
                        tint = SlateDark700,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No hourly records logged yet.",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "Tap 'Trigger Immediate Hourly Check' to log first reading.",
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(hourlyReadings) { reading ->
                    HourlyReadingItemCard(reading = reading, timeFormatter = timeFormatter)
                }
            }
        }
    }
}

@Composable
private fun HourlyReadingItemCard(
    reading: HourlyReadingEntity,
    timeFormatter: SimpleDateFormat
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SlateDark800,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (reading.triggeredAlert) HazardRed.copy(alpha = 0.5f) else SlateBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (reading.triggeredAlert) Icons.Default.Warning else Icons.Default.CheckCircle,
                        contentDescription = "Status",
                        tint = if (reading.triggeredAlert) HazardRed else HazardGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = timeFormatter.format(Date(reading.timestamp)),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (reading.triggeredAlert) HazardRed.copy(alpha = 0.2f) else HazardGreen.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (reading.triggeredAlert) "CRITICAL EVENT" else "SAFE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (reading.triggeredAlert) HazardRed else HazardGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = reading.statusSummary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (reading.triggeredAlert) Color(0xFFFCA5A5) else Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "AQI: ${reading.aqi} (PM2.5: ${String.format(Locale.US, "%.1f", reading.pm25)})",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )
                Text(text = "•", fontSize = 11.sp, color = Color(0xFF64748B))
                Text(
                    text = "Rain: ${String.format(Locale.US, "%.1f", reading.rainMm)} mm/h",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )
                if (reading.maxEarthquakeMag > 0) {
                    Text(text = "•", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text(
                        text = "Quake: M${String.format(Locale.US, "%.1f", reading.maxEarthquakeMag)}",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }
    }
}
