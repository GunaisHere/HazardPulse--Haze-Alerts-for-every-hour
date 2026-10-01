package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.HazardType
import com.example.data.model.DataSourceSettings
import com.example.data.model.DndSettings
import com.example.data.model.HazardThresholds
import com.example.data.model.VoicePersona
import com.example.data.model.VoiceSettings
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
fun SettingsDndScreen(
    dndSettings: DndSettings,
    thresholds: HazardThresholds,
    voiceSettings: VoiceSettings,
    dataSourceSettings: DataSourceSettings,
    isDndActiveNow: Boolean,
    onUpdateDnd: (DndSettings) -> Unit,
    onUpdateThresholds: (HazardThresholds) -> Unit,
    onUpdateVoiceSettings: (VoiceSettings) -> Unit,
    onUpdateDataSourceSettings: (DataSourceSettings) -> Unit,
    onTestVoiceAlert: (HazardType) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var apiKeyInput by remember(dataSourceSettings.iqAirApiKey) {
        mutableStateOf(dataSourceSettings.iqAirApiKey)
    }
    var callsignInput by remember(voiceSettings.userCallsign) {
        mutableStateOf(voiceSettings.userCallsign)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // 1. IRON MAN TACTICAL AI VOICE (J.A.R.V.I.S. & F.R.I.D.A.Y.)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_voice_persona_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDark800),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.6f))
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
                                .background(Color(0xFF0284C7).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = "AI Voice Engine",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "STARK AI VOICE ENGINE",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Cinematic J.A.R.V.I.S. & F.R.I.D.A.Y. acoustics",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0F172A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "${voiceSettings.persona.displayName} ACTIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Persona Selector Cards
                Text(
                    text = "SELECT TACTICAL AI PERSONA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFCBD5E1)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // J.A.R.V.I.S. Card
                    val isJarvis = voiceSettings.persona == VoicePersona.JARVIS
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isJarvis) Color(0xFF1E3A8A).copy(alpha = 0.6f) else SlateDark700,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isJarvis) Color(0xFF38BDF8) else SlateBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onUpdateVoiceSettings(
                                    voiceSettings.copy(
                                        persona = VoicePersona.JARVIS,
                                        pitch = VoicePersona.JARVIS.defaultPitch,
                                        speed = VoicePersona.JARVIS.defaultSpeed,
                                        userCallsign = "Sir"
                                    )
                                )
                                callsignInput = "Sir"
                            }
                            .testTag("select_persona_jarvis")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "J.A.R.V.I.S.",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                if (isJarvis) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "British Tactical AI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF93C5FD)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Calm baritone, speaks to 'Sir' with measured cadence.",
                                fontSize = 10.sp,
                                color = Color(0xFFCBD5E1),
                                lineHeight = 13.sp
                            )
                        }
                    }

                    // F.R.I.D.A.Y. Card
                    val isFriday = voiceSettings.persona == VoicePersona.FRIDAY
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isFriday) Color(0xFF065F46).copy(alpha = 0.6f) else SlateDark700,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isFriday) Color(0xFF34D399) else SlateBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onUpdateVoiceSettings(
                                    voiceSettings.copy(
                                        persona = VoicePersona.FRIDAY,
                                        pitch = VoicePersona.FRIDAY.defaultPitch,
                                        speed = VoicePersona.FRIDAY.defaultSpeed,
                                        userCallsign = "Boss"
                                    )
                                )
                                callsignInput = "Boss"
                            }
                            .testTag("select_persona_friday")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "F.R.I.D.A.Y.",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                if (isFriday) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color(0xFF34D399),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Irish Combat & Hazard AI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA7F3D0)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Crisp, alert feminine voice, addresses user as 'Boss'.",
                                fontSize = 10.sp,
                                color = Color(0xFFCBD5E1),
                                lineHeight = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // User Callsign (Sir / Boss / Tony / Commander)
                Text(
                    text = "AI ADDRESSES YOU AS (CALLSIGN):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFCBD5E1)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = callsignInput,
                        onValueChange = {
                            callsignInput = it
                            onUpdateVoiceSettings(voiceSettings.copy(userCallsign = it))
                        },
                        placeholder = { Text("e.g. Sir, Boss, Tony") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = SlateBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_callsign_input")
                    )

                    Button(
                        onClick = {
                            onTestVoiceAlert(HazardType.HAZE)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("audition_voice_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Audition",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Audition", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Holographic HUD Chime Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Stark Holographic HUD Chime",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Plays acoustic two-harmonic pulse before speaking",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Switch(
                        checked = voiceSettings.playHudChime,
                        onCheckedChange = { chime ->
                            onUpdateVoiceSettings(voiceSettings.copy(playHudChime = chime))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF38BDF8),
                            uncheckedThumbColor = Color(0xFF94A3B8),
                            uncheckedTrackColor = SlateDark700
                        ),
                        modifier = Modifier.testTag("hud_chime_switch")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Voice Pitch Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Voice Tone / Baritone Pitch",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1)
                    )
                    Text(
                        text = "${String.format("%.2f", voiceSettings.pitch)}x",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                }
                Slider(
                    value = voiceSettings.pitch,
                    onValueChange = { newPitch ->
                        onUpdateVoiceSettings(voiceSettings.copy(pitch = newPitch))
                    },
                    valueRange = 0.75f..1.35f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF38BDF8),
                        activeTrackColor = Color(0xFF0284C7),
                        inactiveTrackColor = SlateDark700
                    )
                )

                // Voice Speed Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Cadence Speed",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1)
                    )
                    Text(
                        text = "${String.format("%.2f", voiceSettings.speed)}x",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                }
                Slider(
                    value = voiceSettings.speed,
                    onValueChange = { newSpeed ->
                        onUpdateVoiceSettings(voiceSettings.copy(speed = newSpeed))
                    },
                    valueRange = 0.80f..1.25f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF38BDF8),
                        activeTrackColor = Color(0xFF0284C7),
                        inactiveTrackColor = SlateDark700
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. IQAIR AIRVISUAL DATA SOURCE INTEGRATION
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("iqair_data_source_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDark800),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, HazardOrange.copy(alpha = 0.6f))
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(HazardOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Air,
                                contentDescription = "IQAir AirVisual",
                                tint = HazardOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "IQAIR AIRVISUAL TELEMETRY",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Stream official IQAir stations & particulate data",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (dataSourceSettings.iqAirApiKey.isNotBlank()) HazardGreen.copy(alpha = 0.2f) else SlateDark700
                    ) {
                        Text(
                            text = if (dataSourceSettings.iqAirApiKey.isNotBlank()) "CONNECTED" else "OPTIONAL KEY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (dataSourceSettings.iqAirApiKey.isNotBlank()) HazardGreen else Color(0xFF94A3B8),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "IQAir collects hyper-local air quality from over 30,000 global monitoring stations. Enter your free IQAir AirVisual Community API key below to stream certified IQAir station readings:",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    label = { Text("IQAir / AirVisual API Key") },
                    placeholder = { Text("Enter your API key from iqair.com") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HazardOrange,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("iqair_api_key_input")
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            onUpdateDataSourceSettings(
                                dataSourceSettings.copy(iqAirApiKey = apiKeyInput)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HazardOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("save_iqair_key_button")
                    ) {
                        Text("Save & Sync IQAir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. NIGHTTIME DND (DO NOT DISTURB) SECTION
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dnd_settings_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDark800),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF6366F1).copy(alpha = 0.6f))
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
                                .background(Color(0xFF4F46E5).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DarkMode,
                                contentDescription = "DND Mode",
                                tint = Color(0xFFA5B4FC),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "NIGHTTIME DND SILENCE",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = if (dndSettings.enabled) "Prevents disturbance while sleeping" else "Nighttime silencing disabled",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Switch(
                        checked = dndSettings.enabled,
                        onCheckedChange = { enabled ->
                            onUpdateDnd(dndSettings.copy(enabled = enabled))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF6366F1),
                            uncheckedThumbColor = Color(0xFF94A3B8),
                            uncheckedTrackColor = SlateDark700
                        ),
                        modifier = Modifier.testTag("dnd_enabled_switch")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDndActiveNow) Color(0xFF312E81) else SlateDark700.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDndActiveNow) Color(0xFF818CF8) else SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isDndActiveNow)
                                "🌙 DND IS ACTIVE RIGHT NOW. AI voice and notification sounds are silenced."
                            else "☀️ DND is currently inactive. ${voiceSettings.persona.displayName} will vocalize critical alerts.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDndActiveNow) Color(0xFFE0E7FF) else Color(0xFF94A3B8),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Schedule controls
                Text(
                    text = "DND SCHEDULE: ${dndSettings.formattedStartTime} TO ${dndSettings.formattedEndTime}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFCBD5E1)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Start Time: ${dndSettings.startHour}:00",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                Slider(
                    value = dndSettings.startHour.toFloat(),
                    onValueChange = { newHour ->
                        onUpdateDnd(dndSettings.copy(startHour = newHour.toInt()))
                    },
                    valueRange = 18f..23f,
                    steps = 4,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF818CF8),
                        activeTrackColor = Color(0xFF6366F1),
                        inactiveTrackColor = SlateDark700
                    ),
                    modifier = Modifier.testTag("dnd_start_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "End Time: ${dndSettings.endHour}:00",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                Slider(
                    value = dndSettings.endHour.toFloat(),
                    onValueChange = { newHour ->
                        onUpdateDnd(dndSettings.copy(endHour = newHour.toInt()))
                    },
                    valueRange = 5f..10f,
                    steps = 4,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF818CF8),
                        activeTrackColor = Color(0xFF6366F1),
                        inactiveTrackColor = SlateDark700
                    ),
                    modifier = Modifier.testTag("dnd_end_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Extreme Emergency Override",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Always alert for Tsunami and major quakes even in DND",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Switch(
                        checked = dndSettings.allowExtremeOverride,
                        onCheckedChange = { override ->
                            onUpdateDnd(dndSettings.copy(allowExtremeOverride = override))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = HazardRed,
                            uncheckedThumbColor = Color(0xFF94A3B8),
                            uncheckedTrackColor = SlateDark700
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. TEST VOICE ALERTS BY HAZARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDark800),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "AUDITION ${voiceSettings.persona.displayName} ALERTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Listen to how ${voiceSettings.persona.displayName} delivers each emergency warning",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    VoiceTestRow(
                        title = "Critical Haze (IQAir)",
                        icon = Icons.Default.Air,
                        accentColor = HazardOrange,
                        onClick = { onTestVoiceAlert(HazardType.HAZE) }
                    )
                    VoiceTestRow(
                        title = "Critical Rain & Storm",
                        icon = Icons.Default.Thunderstorm,
                        accentColor = HazardBlue,
                        onClick = { onTestVoiceAlert(HazardType.RAIN) }
                    )
                    VoiceTestRow(
                        title = "Critical Earthquake",
                        icon = Icons.Default.Public,
                        accentColor = HazardPurple,
                        onClick = { onTestVoiceAlert(HazardType.EARTHQUAKE) }
                    )
                    VoiceTestRow(
                        title = "Critical Tsunami Warning",
                        icon = Icons.Default.WaterDrop,
                        accentColor = Color(0xFF0284C7),
                        onClick = { onTestVoiceAlert(HazardType.TSUNAMI) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun VoiceTestRow(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SlateDark700,
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Play",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Speak", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
