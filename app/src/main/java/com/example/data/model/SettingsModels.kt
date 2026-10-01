package com.example.data.model

import java.util.Calendar

enum class VoicePersona(
    val displayName: String,
    val subtitle: String,
    val defaultCallsign: String,
    val defaultPitch: Float,
    val defaultSpeed: Float
) {
    JARVIS(
        displayName = "J.A.R.V.I.S.",
        subtitle = "Stark Tactical Defense AI (British / Sophisticated Baritone)",
        defaultCallsign = "Sir",
        defaultPitch = 0.88f,
        defaultSpeed = 0.98f
    ),
    FRIDAY(
        displayName = "F.R.I.D.A.Y.",
        subtitle = "Emergency Combat & Hazard AI (Irish / Crisp Tactical)",
        defaultCallsign = "Boss",
        defaultPitch = 1.14f,
        defaultSpeed = 1.04f
    )
}

data class VoiceSettings(
    val persona: VoicePersona = VoicePersona.JARVIS,
    val userCallsign: String = "Sir",
    val pitch: Float = 0.88f,
    val speed: Float = 0.98f,
    val playHudChime: Boolean = true
)

data class DataSourceSettings(
    val iqAirApiKey: String = "",
    val preferIQAir: Boolean = true
)

data class DndSettings(
    val enabled: Boolean = true,
    val startHour: Int = 22, // 10:00 PM
    val startMinute: Int = 0,
    val endHour: Int = 7,    // 07:00 AM
    val endMinute: Int = 0,
    val allowExtremeOverride: Boolean = true // Tsunami & Catastrophic Quakes bypass night silence
) {
    fun isCurrentlyActive(calendar: Calendar = Calendar.getInstance()): Boolean {
        if (!enabled) return false
        val currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
        val startMinutes = startHour * 60 + startMinute
        val endMinutes = endHour * 60 + endMinute

        return if (startMinutes < endMinutes) {
            currentMinutes in startMinutes until endMinutes
        } else {
            // Overnights, e.g. 22:00 (1320m) to 07:00 (420m)
            currentMinutes >= startMinutes || currentMinutes < endMinutes
        }
    }

    val formattedStartTime: String
        get() = String.format("%02d:%02d", startHour, startMinute)

    val formattedEndTime: String
        get() = String.format("%02d:%02d", endHour, endMinute)
}

data class HazardThresholds(
    val hazeCriticalAqi: Int = 150, // >= 150 is Unhealthy/Hazardous
    val torrentialRainMm: Double = 15.0, // >= 15 mm/h
    val earthquakeMinMag: Double = 4.5, // >= 4.5
    val voiceEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)
