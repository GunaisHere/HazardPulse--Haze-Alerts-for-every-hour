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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.HazardType
import com.example.ui.theme.HazardAmber
import com.example.ui.theme.HazardBlue
import com.example.ui.theme.HazardOrange
import com.example.ui.theme.HazardPurple
import com.example.ui.theme.HazardRed
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDark700
import com.example.ui.theme.SlateDark800

@Composable
fun SimulationScreen(
    isDndActiveNow: Boolean,
    onSimulate: (HazardType, Boolean) -> Unit,
    onClearAllAlerts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var bypassDndForSimulation by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDark800),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, HazardAmber.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(HazardAmber.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Simulation Lab",
                            tint = HazardAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CRITICAL ALERT SIMULATION LAB",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Test voice, notifications & DND behavior instantly",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Trigger real critical alerts for your GPS coordinates to verify that the custom emergency voice alert sounds, high-priority notifications are dispatched, and records are persisted to Room database.",
                    fontSize = 12.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))
                // DND Bypass Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Force Sound (Bypass Night DND)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isDndActiveNow) "DND is active now. Enable this to test voice sound immediately."
                            else "DND is currently inactive.",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Switch(
                        checked = bypassDndForSimulation,
                        onCheckedChange = { bypassDndForSimulation = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = HazardOrange,
                            uncheckedThumbColor = Color(0xFF94A3B8),
                            uncheckedTrackColor = SlateDark700
                        ),
                        modifier = Modifier.testTag("simulation_bypass_dnd_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "TRIGGER CRITICAL EVENT SCENARIOS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF94A3B8),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SimulationScenarioCard(
                title = "Critical Haze Alert",
                subtitle = "AQI 318 (Hazardous PM2.5: 184 µg/m³)",
                desc = "Severe forest fire / pollution inversion. Injects hazardous AQI, dispatches 'Critical Haze Alert' voice announcement.",
                icon = Icons.Default.Air,
                buttonColor = HazardOrange,
                buttonText = "Trigger Critical Haze",
                onClick = { onSimulate(HazardType.HAZE, bypassDndForSimulation) },
                testTag = "simulate_scenario_haze"
            )

            SimulationScenarioCard(
                title = "Critical Torrential Rain Alert",
                subtitle = "38.5 mm/h Precipitation & Cloudburst",
                desc = "Extreme storm squall. Dispatches 'Critical Rain Alert' voice and flash flood guidance.",
                icon = Icons.Default.Thunderstorm,
                buttonColor = HazardBlue,
                buttonText = "Trigger Critical Rain",
                onClick = { onSimulate(HazardType.RAIN, bypassDndForSimulation) },
                testTag = "simulate_scenario_rain"
            )

            SimulationScenarioCard(
                title = "Critical Earthquake Alert",
                subtitle = "Magnitude 6.7 (65 km distance)",
                desc = "Severe seismic event near user GPS. Dispatches 'Critical Earthquake Alert' voice and drop-cover-hold guidance.",
                icon = Icons.Default.Public,
                buttonColor = HazardPurple,
                buttonText = "Trigger Critical Quake",
                onClick = { onSimulate(HazardType.EARTHQUAKE, bypassDndForSimulation) },
                testTag = "simulate_scenario_quake"
            )

            SimulationScenarioCard(
                title = "Critical Tsunami Warning",
                subtitle = "Ocean Hazard Wave Prediction +3.8m",
                desc = "Coastal tsunami threat detected. Dispatches 'Critical Tsunami Warning' voice and evacuation advisory.",
                icon = Icons.Default.WaterDrop,
                buttonColor = Color(0xFF0284C7),
                buttonText = "Trigger Tsunami Warning",
                onClick = { onSimulate(HazardType.TSUNAMI, bypassDndForSimulation) },
                testTag = "simulate_scenario_tsunami"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = onClearAllAlerts,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("clear_all_alerts_button")
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Clear All",
                modifier = Modifier.size(16.dp),
                tint = HazardRed
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Clear All Alert Records from Database", fontSize = 12.sp, color = HazardRed)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SimulationScenarioCard(
    title: String,
    subtitle: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    buttonColor: Color,
    buttonText: String,
    onClick: () -> Unit,
    testTag: String
) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(buttonColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = buttonColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = buttonColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = desc,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(testTag)
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = "Trigger",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(buttonText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
