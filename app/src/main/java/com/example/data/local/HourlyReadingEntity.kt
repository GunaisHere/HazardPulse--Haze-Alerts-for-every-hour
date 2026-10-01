package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hourly_readings")
data class HourlyReadingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val aqi: Int,
    val pm25: Double,
    val rainMm: Double,
    val temperatureC: Double,
    val maxEarthquakeMag: Double,
    val statusSummary: String,
    val triggeredAlert: Boolean = false
)
