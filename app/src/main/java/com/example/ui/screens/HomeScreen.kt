package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.HazardAlertEntity
import com.example.data.local.HazardType
import com.example.data.model.DndSettings
import com.example.data.model.HazardThresholds
import com.example.data.model.VoiceSettings
import com.example.data.repository.LiveHazardSnapshot
import com.example.ui.components.AqiSeverityBadge
import com.example.ui.components.CriticalAlertBanner
import com.example.ui.components.MetricChip
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
fun HomeScreen(
    snapshot: LiveHazardSnapshot,
    alerts: List<HazardAlertEntity>,
    dndSettings: DndSettings,
    thresholds: HazardThresholds,
    voiceSettings: VoiceSettings = VoiceSettings(),
    isDndActiveNow: Boolean,
    onRefresh: () -> Unit,
    onPlayVoice: (HazardType) -> Unit,
    onDismissAlert: (Long) -> Unit,
    onNavigateToSimulate: () -> Unit,
    onNavigateToHaze: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Top Location Bar & DND Indicator
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
                                .background(HazardOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "GPS Location",
                                tint = HazardOrange,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CURRENT GPS LOCATION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = snapshot.location.placeName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    IconButton(
                        onClick = onRefresh,
                        enabled = !snapshot.isFetching,
                        modifier = Modifier.testTag("refresh_gps_button")
                    ) {
                        if (snapshot.isFetching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = HazardOrange,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh GPS",
                                tint = Color(0xFF38BDF8)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Lat: ${String.format(Locale.US, "%.3f", snapshot.location.latitude)}, Lon: ${String.format(Locale.US, "%.3f", snapshot.location.longitude)}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )

                    // DND Night status chip
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isDndActiveNow) Color(0xFF312E81) else Color(0xFF0F766E).copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDndActiveNow) Color(0xFF6366F1) else Color(0xFF14B8A6).copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isDndActiveNow) Icons.Default.DarkMode else Icons.Default.NotificationsActive,
                                contentDescription = "DND Status",
                                tint = if (isDndActiveNow) Color(0xFFA5B4FC) else Color(0xFF2DD4BF),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isDndActiveNow) "DND NIGHT SILENCE ACTIVE" else "HOURLY RADAR ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDndActiveNow) Color(0xFFA5B4FC) else Color(0xFF2DD4BF)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                // IQAir Telemetry and AI Persona Badges
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (snapshot.iqAirStationActive) Color(0xFF14532D) else SlateDark700
                    ) {
                        Text(
                            text = if (snapshot.iqAirStationActive) "🍃 IQAIR VERIFIED STATION" else "📡 IQAIR / GLOBAL AQI",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (snapshot.iqAirStationActive) Color(0xFF86EFAC) else Color(0xFF94A3B8),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF0F172A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "🤖 ${voiceSettings.persona.displayName} ACTIVE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Active Critical Alerts (Banners)
        val activeAlerts = alerts.filter { !it.isAcknowledged }
        if (activeAlerts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "CRITICAL OUTSIDE HAZARDS DETECTED (${activeAlerts.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = HazardRed,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                activeAlerts.take(3).forEach { alert ->
                    val hazardType = try {
                        HazardType.valueOf(alert.type)
                    } catch (e: Exception) {
                        HazardType.HAZE
                    }
                    CriticalAlertBanner(
                        alert = alert,
                        onPlayVoice = { onPlayVoice(hazardType) },
                        onDismiss = { onDismissAlert(alert.id) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 1. PRIMARY FOCUS: HAZE & AIR QUALITY CARD (Crucial User Requirement)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("haze_air_quality_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDark800),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (snapshot.aqi >= thresholds.hazeCriticalAqi) HazardRed else SlateBorder
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(HazardOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Air,
                                contentDescription = "Air Quality & Haze",
                                tint = HazardOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "OUTSIDE HAZE & AIR QUALITY",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Critical safety threshold: AQI ${thresholds.hazeCriticalAqi}",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    AqiSeverityBadge(aqi = snapshot.aqi)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SlateDark700.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📡 Data Feed: ${snapshot.airQualitySource}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF7DD3FC),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // AQI Meter Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${snapshot.aqi}",
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Black,
                                color = when {
                                    snapshot.aqi >= thresholds.hazeCriticalAqi -> HazardRed
                                    snapshot.aqi > 100 -> HazardOrange
                                    snapshot.aqi > 50 -> HazardAmber
                                    else -> HazardGreen
                                }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "US AQI",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                        Text(
                            text = if (snapshot.aqi >= thresholds.hazeCriticalAqi)
                                "⚠️ CRITICAL SAFETY THRESHOLD EXCEEDED"
                            else "Within Normal Environmental Safety Range",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (snapshot.aqi >= thresholds.hazeCriticalAqi) HazardRed else HazardGreen
                        )
                    }

                    Button(
                        onClick = { onPlayVoice(HazardType.HAZE) },
                        colors = ButtonDefaults.buttonColors(containerColor = SlateDark700),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("test_haze_sound_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Test Haze Alert Sound",
                            tint = HazardOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(voiceSettings.persona.displayName, fontSize = 11.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar toward critical threshold
                val progressFraction = (snapshot.aqi / 300f).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = when {
                        snapshot.aqi >= thresholds.hazeCriticalAqi -> HazardRed
                        snapshot.aqi > 100 -> HazardOrange
                        else -> HazardGreen
                    },
                    trackColor = SlateDark700
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Fine particulate matter breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricChip(
                        label = "PM2.5 (Fine Haze)",
                        value = "${String.format(Locale.US, "%.1f", snapshot.pm25)} µg/m³",
                        isWarning = snapshot.pm25 >= 55.0,
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        label = "PM10 (Dust)",
                        value = "${String.format(Locale.US, "%.1f", snapshot.pm10)} µg/m³",
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        label = "Ozone (O₃)",
                        value = "${String.format(Locale.US, "%.1f", snapshot.ozone)} µg/m³",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                // Safety Action guidance
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
                            contentDescription = "Haze Safety Guide",
                            tint = HazardOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (snapshot.aqi >= thresholds.hazeCriticalAqi)
                                "Stay indoors! Close windows, activate HEPA air cleaners, wear N95 masks."
                            else "Outdoor conditions acceptable. Sensitive respiratory groups should monitor hourly.",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. REAL-TIME RAIN & WEATHER CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("rain_weather_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDark800),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (snapshot.rainMm >= thresholds.torrentialRainMm) HazardBlue else SlateBorder
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(HazardBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Thunderstorm,
                                contentDescription = "Torrential Rain & Storm",
                                tint = HazardBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TORRENTIAL RAIN & STORM",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Threshold: ${thresholds.torrentialRainMm} mm/h precipitation",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Button(
                        onClick = { onPlayVoice(HazardType.RAIN) },
                        colors = ButtonDefaults.buttonColors(containerColor = SlateDark700),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Test Rain Alert Sound",
                            tint = HazardBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Voice", fontSize = 11.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricChip(
                        label = "Precipitation",
                        value = "${String.format(Locale.US, "%.1f", snapshot.rainMm)} mm/h",
                        isWarning = snapshot.rainMm >= thresholds.torrentialRainMm,
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        label = "Temperature",
                        value = "${String.format(Locale.US, "%.1f", snapshot.temperatureC)} °C",
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        label = "Wind Gusts",
                        value = "${String.format(Locale.US, "%.1f", snapshot.windGustKmH)} km/h",
                        isWarning = snapshot.windGustKmH >= 50.0,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. EARTHQUAKE & TSUNAMI RADAR CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("earthquake_tsunami_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDark800),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (snapshot.hasTsunamiThreat || snapshot.highestNearbyMag >= thresholds.earthquakeMinMag) HazardRed else SlateBorder
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(HazardPurple.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = "Earthquake & Tsunami",
                                tint = HazardPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SEISMIC & TSUNAMI RADAR",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "USGS Real-time 600km perimeter",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = { onPlayVoice(HazardType.EARTHQUAKE) }) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Test Quake Voice",
                                tint = HazardPurple
                            )
                        }
                        IconButton(onClick = { onPlayVoice(HazardType.TSUNAMI) }) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Test Tsunami Voice",
                                tint = HazardBlue
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricChip(
                        label = "Max Seismic Mag",
                        value = if (snapshot.highestNearbyMag > 0) "M ${String.format(Locale.US, "%.1f", snapshot.highestNearbyMag)}" else "None >3.0",
                        isWarning = snapshot.highestNearbyMag >= thresholds.earthquakeMinMag,
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        label = "Tsunami Threat",
                        value = if (snapshot.hasTsunamiThreat) "ACTIVE WARNING" else "Clear / None",
                        isWarning = snapshot.hasTsunamiThreat,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (snapshot.recentQuakes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Recent Seismicity (Last 24h):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    snapshot.recentQuakes.take(2).forEach { q ->
                        Text(
                            text = "• M ${q.properties?.mag ?: 0.0} - ${q.properties?.place ?: "Unknown"}",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onNavigateToSimulate,
                colors = ButtonDefaults.buttonColors(containerColor = HazardOrange),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("simulate_test_station_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Simulate Alerts",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Test Critical Alerts", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onNavigateToHaze,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Air,
                    contentDescription = "Haze Deep Dive",
                    modifier = Modifier.size(16.dp),
                    tint = HazardOrange
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Haze Guide", fontSize = 12.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
