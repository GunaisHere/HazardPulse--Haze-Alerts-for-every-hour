package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class HazardType(val displayName: String, val spokenAlertTitle: String) {
    HAZE("Haze & Air Quality", "Critical Haze Alert"),
    RAIN("Torrential Rain & Storm", "Critical Rain Alert"),
    EARTHQUAKE("Earthquake Seismic Alert", "Critical Earthquake Alert"),
    TSUNAMI("Tsunami Warning", "Critical Tsunami Warning")
}

enum class HazardSeverity(val levelName: String) {
    MODERATE("Moderate Risk"),
    HIGH("High Hazard"),
    CRITICAL("Critical Emergency")
}

@Entity(tableName = "hazard_alerts")
data class HazardAlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // HAZE, RAIN, EARTHQUAKE, TSUNAMI
    val severity: String, // MODERATE, HIGH, CRITICAL
    val title: String,
    val description: String,
    val primaryMetric: String, // e.g. "AQI 280 (Very Unhealthy)", "Rain: 28 mm/h", "Mag 6.4 (120 km away)"
    val locationText: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val wasDndSilenced: Boolean = false,
    val isAcknowledged: Boolean = false
)
