package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HazardDao {

    @Query("SELECT * FROM hazard_alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<HazardAlertEntity>>

    @Query("SELECT * FROM hazard_alerts WHERE severity = 'CRITICAL' ORDER BY timestamp DESC LIMIT 20")
    fun getCriticalAlerts(): Flow<List<HazardAlertEntity>>

    @Query("SELECT * FROM hazard_alerts ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestAlert(): HazardAlertEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: HazardAlertEntity): Long

    @Query("UPDATE hazard_alerts SET isAcknowledged = 1 WHERE id = :id")
    suspend fun markAlertAcknowledged(id: Long)

    @Query("DELETE FROM hazard_alerts WHERE id = :id")
    suspend fun deleteAlertById(id: Long)

    @Query("DELETE FROM hazard_alerts")
    suspend fun clearAllAlerts()

    @Query("SELECT * FROM hourly_readings ORDER BY timestamp DESC LIMIT 48")
    fun getRecentHourlyReadings(): Flow<List<HourlyReadingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyReading(reading: HourlyReadingEntity): Long

    @Query("DELETE FROM hourly_readings WHERE timestamp < :olderThanTimestamp")
    suspend fun cleanupOldHourlyReadings(olderThanTimestamp: Long)
}
