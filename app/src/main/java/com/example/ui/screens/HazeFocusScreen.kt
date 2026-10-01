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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Masks
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.example.data.local.HazardType
import com.example.data.model.DataSourceSettings
import com.example.data.model.HazardThresholds
import com.example.data.model.VoiceSettings
import com.example.data.repository.LiveHazardSnapshot
import com.example.ui.components.AqiSeverityBadge
import com.example.ui.components.MetricChip
import com.example.ui.theme.HazardAmber
import com.example.ui.theme.HazardGreen
import com.example.ui.theme.HazardOrange
import com.example.ui.theme.HazardPurple
import com.example.ui.theme.HazardRed
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark700
import com.example.ui.theme.SlateDark800
import java.util.Locale

@Composable
fun HazeFocusScreen(
    snapshot: LiveHazardSnapshot,
    thresholds: HazardThresholds,
    voiceSettings: VoiceSettings = VoiceSettings(),
    dataSourceSettings: DataSourceSettings = DataSourceSettings(),
    onUpdateThresholds: (HazardThresholds) -> Unit,
    onTestHazeVoice: () -> Unit,
    onSimulateCriticalHaze: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Hero Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDark800),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, HazardOrange.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(HazardOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Air,
                                contentDescription = "Haze Monitoring",
                                tint = HazardOrange,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "CRITICAL HAZE PROTOCOL",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "IQAir & Atmospheric Particulate Telemetry",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    AqiSeverityBadge(aqi = snapshot.aqi)
                }

                Spacer(modifier = Modifier.height(14.dp))
                // IQAir Live Feed Info
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (snapshot.iqAirStationActive) Color(0xFF14532D) else SlateDark700.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (snapshot.iqAirStationActive) "🍃 IQAir Verified Station: ${snapshot.iqAirCity ?: "Connected"}"
                            else "📡 Source: ${snapshot.airQualitySource}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (snapshot.iqAirStationActive) Color(0xFF86EFAC) else Color(0xFF93C5FD)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "${snapshot.aqi}",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = when {
                                snapshot.aqi >= thresholds.hazeCriticalAqi -> HazardRed
                                snapshot.aqi > 100 -> HazardOrange
                                else -> HazardGreen
                            }
                        )
                        Text(
                            text = "Current US AQI (GPS: ${snapshot.location.placeName})",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }

                    Button(
                        onClick = onTestHazeVoice,
                        colors = ButtonDefaults.buttonColors(containerColor = HazardOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("hear_critical_haze_voice_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Hear AI Voice",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${voiceSettings.persona.displayName} Voice", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // IQAir Station Telemetry Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDark800),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "IQAIR AIRVISUAL STATION TELEMETRY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricChip(
                        label = "Main Pollutant",
                        value = snapshot.iqAirMainPollutant ?: "PM2.5 (Fine Haze)",
                        isWarning = true,
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        label = "PM2.5 Density",
                        value = "${String.format(Locale.US, "%.1f", snapshot.pm25)} µg/m³",
                        isWarning = snapshot.pm25 >= 55.0,
                        modifier = Modifier.weight(1f)
                    )
                    MetricChip(
                        label = "Sensor Source",
                        value = if (snapshot.iqAirStationActive) "IQAir Station" else "Global Feed",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Haze Safety Threshold Tuning
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDark800),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CRITICAL HAZE ALERT THRESHOLD",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Hourly monitoring triggers immediate alert if AQI exceeds this level:",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Threshold: AQI ${thresholds.hazeCriticalAqi}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = HazardOrange
                    )
                    Text(
                        text = when {
                            thresholds.hazeCriticalAqi <= 100 -> "Sensitive (100)"
                            thresholds.hazeCriticalAqi <= 150 -> "Unhealthy for Sensitive (150 - Default)"
                            thresholds.hazeCriticalAqi <= 200 -> "Unhealthy (200)"
                            else -> "Hazardous (250+)"
                        },
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }

                Slider(
                    value = thresholds.hazeCriticalAqi.toFloat(),
                    onValueChange = { newValue ->
                        onUpdateThresholds(thresholds.copy(hazeCriticalAqi = newValue.toInt()))
                    },
                    valueRange = 80f..250f,
                    steps = 16,
                    colors = SliderDefaults.colors(
                        thumbColor = HazardOrange,
                        activeTrackColor = HazardOrange,
                        inactiveTrackColor = SlateDark700
                    ),
                    modifier = Modifier.testTag("haze_threshold_slider")
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // AQI Tier Spectrum Guide
        Text(
            text = "OFFICIAL SAFETY SCALE & HEALTH THRESHOLDS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF94A3B8),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AqiTierCard(
                range = "0 - 50",
                name = "Good",
                color = HazardGreen,
                desc = "Air quality is satisfactory, poses little or no risk to health."
            )
            AqiTierCard(
                range = "51 - 100",
                name = "Moderate",
                color = HazardAmber,
                desc = "Acceptable air quality; unusually sensitive people should consider reducing prolonged outdoor exertion."
            )
            AqiTierCard(
                range = "101 - 150",
                name = "Unhealthy for Sensitive Groups",
                color = HazardOrange,
                desc = "Members of sensitive groups may experience health effects. General public not likely affected."
            )
            AqiTierCard(
                range = "151 - 200",
                name = "Unhealthy (CRITICAL ALERT TIER)",
                color = HazardRed,
                desc = "Everyone may begin to experience health effects; members of sensitive groups may experience more serious health effects."
            )
            AqiTierCard(
                range = "201 - 300",
                name = "Very Unhealthy (SEVERE HAZARD)",
                color = HazardPurple,
                desc = "Health alert: The risk of health effects is increased for everyone."
            )
            AqiTierCard(
                range = "301+",
                name = "Hazardous (EMERGENCY CRISIS)",
                color = Color(0xFF881337),
                desc = "Health warning of emergency conditions: everyone is more likely to be affected."
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Direct Simulation Button
        Button(
            onClick = onSimulateCriticalHaze,
            colors = ButtonDefaults.buttonColors(containerColor = HazardRed),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("simulate_critical_haze_button")
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Test Haze Alert",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simulate IQAir Critical Haze Event (AQI 318)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AqiTierCard(
    range: String,
    name: String,
    color: Color,
    desc: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SlateDark800,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$range AQI: $name",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 15.sp
                )
            }
        }
    }
}
